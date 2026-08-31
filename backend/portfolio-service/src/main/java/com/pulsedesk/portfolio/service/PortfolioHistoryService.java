package com.pulsedesk.portfolio.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pulsedesk.portfolio.client.HistoricalMarketDataClient;
import com.pulsedesk.portfolio.client.TradingClient;
import com.pulsedesk.portfolio.dto.HistoricalPriceResponse;
import com.pulsedesk.portfolio.dto.OrderResponse;
import com.pulsedesk.portfolio.dto.PortfolioHistoryPointResponse;
import com.pulsedesk.portfolio.entity.DailyPortfolioSnapshot;
import com.pulsedesk.portfolio.entity.Portfolio;
import com.pulsedesk.portfolio.entity.Position;
import com.pulsedesk.portfolio.repository.DailyPortfolioSnapshotRepository;
import com.pulsedesk.portfolio.repository.PortfolioRepository;
import com.pulsedesk.portfolio.repository.PositionRepository;

@Service
public class PortfolioHistoryService {

    private final TradingClient tradingClient;
    private final HistoricalMarketDataClient historicalMarketDataClient;
    private final PortfolioRepository portfolioRepository;
    private final DailyPortfolioSnapshotRepository dailyPortfolioSnapshotRepository;
    private final PositionRepository positionRepository;

    public PortfolioHistoryService(
            TradingClient tradingClient,
            HistoricalMarketDataClient historicalMarketDataClient,
            PortfolioRepository portfolioRepository,
            DailyPortfolioSnapshotRepository dailyPortfolioSnapshotRepository,
            PositionRepository positionRepository) {

        this.tradingClient = tradingClient;
        this.historicalMarketDataClient = historicalMarketDataClient;
        this.portfolioRepository = portfolioRepository;
        this.dailyPortfolioSnapshotRepository = dailyPortfolioSnapshotRepository;
        this.positionRepository = positionRepository;
    }

    public void reconstructPortfolioHistory(UUID userId) {

        Portfolio portfolio = portfolioRepository
                .findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Portfolio not found for user " + userId));

        List<Position> currentPositions = positionRepository.findByPortfolioId(portfolio.getId());

        Instant reliableStartInstant = currentPositions.stream()
                .map(Position::getCreatedAt)
                .max(Instant::compareTo)
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot determine reliable portfolio history start"));

        LocalDate reliableStartDate = reliableStartInstant
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDate();

        List<OrderResponse> orders = tradingClient.getOrderResponses(userId);

        List<OrderResponse> executedOrders = orders.stream()
                .filter(order -> "EXECUTED".equals(order.status()))
                .sorted(Comparator.comparing(OrderResponse::updatedAt))
                .toList();

        List<OrderResponse> relevantExecutedOrders = executedOrders.stream()
                .filter(order -> !getExecutionDate(order).isBefore(reliableStartDate))
                .toList();

        Set<String> symbols = new HashSet<>();

        symbols.addAll(
                relevantExecutedOrders.stream()
                        .map(OrderResponse::symbol)
                        .collect(Collectors.toSet()));

        symbols.addAll(
                currentPositions.stream()
                        .map(Position::getSymbol)
                        .collect(Collectors.toSet()));

        Map<String, Map<LocalDate, BigDecimal>> historicalPrices = new HashMap<>();

        for (String symbol : symbols) {

            List<HistoricalPriceResponse> history = historicalMarketDataClient.getHistoricalPrices(symbol);

            Map<LocalDate, BigDecimal> pricesByDate = history.stream()
                    .collect(Collectors.toMap(
                            HistoricalPriceResponse::date,
                            HistoricalPriceResponse::closePrice));

            historicalPrices.put(symbol, pricesByDate);

            waitBeforeNextMarketDataRequest();
        }

        LocalDate endDate = LocalDate.now(ZoneId.of("America/New_York"));

        LocalDate oneMonthAgo = endDate.minusMonths(1);

        LocalDate startDate = reliableStartDate.isAfter(oneMonthAgo)
                ? reliableStartDate
                : oneMonthAgo;

        TreeSet<LocalDate> tradingDates = new TreeSet<>();

        for (Map<LocalDate, BigDecimal> pricesByDate : historicalPrices.values()) {

            for (LocalDate date : pricesByDate.keySet()) {

                if (!date.isBefore(startDate)
                        && !date.isAfter(endDate)) {

                    tradingDates.add(date);
                }
            }
        }

        BigDecimal cashBalance = portfolio.getCashBalance();

        Map<String, Long> positions = new HashMap<>();

        for (Position position : currentPositions) {

            positions.put(
                    position.getSymbol(),
                    Long.valueOf(position.getQuantity()));
        }

        int orderIndex = relevantExecutedOrders.size() - 1;

        for (LocalDate date : tradingDates.descendingSet()) {

            while (orderIndex >= 0
                    && getExecutionDate(
                            relevantExecutedOrders.get(orderIndex))
                            .isAfter(date)) {

                OrderResponse order = relevantExecutedOrders.get(orderIndex);

                BigDecimal totalOrderValue = order.price().multiply(
                        BigDecimal.valueOf(
                                order.quantity()));

                if ("BUY".equals(order.side())) {

                    cashBalance = cashBalance.add(totalOrderValue);

                    positions.merge(
                            order.symbol(),
                            -order.quantity(),
                            Long::sum);

                } else if ("SELL".equals(order.side())) {

                    cashBalance = cashBalance.subtract(totalOrderValue);

                    positions.merge(
                            order.symbol(),
                            order.quantity(),
                            Long::sum);
                }

                Long resultingQuantity = positions.get(order.symbol());

                if (resultingQuantity != null
                        && resultingQuantity == 0L) {

                    positions.remove(order.symbol());
                }

                orderIndex--;
            }

            BigDecimal positionsValue = BigDecimal.ZERO;

            for (Map.Entry<String, Long> position : positions.entrySet()) {

                String symbol = position.getKey();

                Long quantity = position.getValue();

                if (quantity == 0) {
                    continue;
                }

                if (quantity < 0) {
                    throw new IllegalStateException(
                            "Cannot reconstruct portfolio history: "
                                    + "negative quantity for "
                                    + symbol
                                    + " on "
                                    + date);
                }

                BigDecimal closePrice = getClosePriceOnOrBefore(
                        historicalPrices.get(symbol),
                        date,
                        symbol);

                BigDecimal marketValue = closePrice.multiply(
                        BigDecimal.valueOf(quantity));

                positionsValue = positionsValue.add(marketValue);
            }

            BigDecimal totalValue = cashBalance.add(positionsValue);

            boolean snapshotAlreadyExists = dailyPortfolioSnapshotRepository
                    .findByPortfolioIdAndSnapshotDate(
                            portfolio.getId(),
                            date)
                    .isPresent();

            if (!snapshotAlreadyExists) {

                DailyPortfolioSnapshot snapshot = new DailyPortfolioSnapshot(
                        UUID.randomUUID(),
                        portfolio,
                        date,
                        cashBalance,
                        totalValue,
                        Instant.now());

                dailyPortfolioSnapshotRepository.save(snapshot);
            }
        }
    }

    private BigDecimal getClosePriceOnOrBefore(
            Map<LocalDate, BigDecimal> pricesByDate,
            LocalDate date,
            String symbol) {

        BigDecimal exactPrice = pricesByDate.get(date);

        if (exactPrice != null) {
            return exactPrice;
        }

        return pricesByDate.entrySet().stream()
                .filter(entry -> !entry.getKey().isAfter(date))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalStateException(
                        "No historical price available for "
                                + symbol
                                + " on or before "
                                + date));
    }

    private LocalDate getExecutionDate(
            OrderResponse order) {

        return order.updatedAt()
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDate();
    }

    private void waitBeforeNextMarketDataRequest() {

        try {

            Thread.sleep(1500);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while waiting for market data",
                    e);
        }
    }

    public List<PortfolioHistoryPointResponse> getPortfolioHistory(UUID userId) {

        Portfolio portfolio = portfolioRepository
                .findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Portfolio not found for user " + userId));

        return dailyPortfolioSnapshotRepository
                .findByPortfolioIdOrderBySnapshotDateAsc(portfolio.getId())
                .stream()
                .map(snapshot -> new PortfolioHistoryPointResponse(
                        snapshot.getSnapshotDate(),
                        snapshot.getTotalValue()))
                .toList();
    }
}
