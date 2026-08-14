package com.pulsedesk.portfolio.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.ExecutedOrderSide;
import com.pulsedesk.contracts.events.OrderExecuted;
import com.pulsedesk.portfolio.dto.PortfolioResponse;
import com.pulsedesk.portfolio.dto.PositionResponse;
import com.pulsedesk.portfolio.entity.Portfolio;
import com.pulsedesk.portfolio.entity.Position;
import com.pulsedesk.portfolio.exception.PortfolioNotFoundException;
import com.pulsedesk.portfolio.producer.PortfolioUpdatedEventProducer;
import com.pulsedesk.portfolio.repository.PortfolioRepository;
import com.pulsedesk.portfolio.repository.PositionRepository;
import com.pulsedesk.portfolio.producer.PortfolioUpdatedEventProducer;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final PortfolioUpdatedEventProducer portfolioUpdatedEventProducer;

    public PortfolioService(PortfolioRepository portfolioRepository, PositionRepository positionRepository,
            PortfolioUpdatedEventProducer portfolioUpdatedEventProducer) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.portfolioUpdatedEventProducer = portfolioUpdatedEventProducer;
    }

    // Retorna o portfolio com base no ID do usuário
    public Portfolio getPortfolioByUserId(UUID userId) {
        return portfolioRepository
                .findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(
                        "Portfolio not found for user " + userId));
    }

    // Retorna lista de posições daquele portfólio
    public List<Position> getPositionsByPortfolioId(UUID portfolioId) {
        return positionRepository.findByPortfolioId(portfolioId);
    }

    public PortfolioResponse getPortfolioResponseByUserId(UUID userId) {

        Portfolio portfolio = getPortfolioByUserId(userId);

        // Lista com as posições que pertencem ao portfólio
        List<Position> positions = getPositionsByPortfolioId(portfolio.getId());

        // Muda a classe, já que essa tem apenas as info transmitidas
        List<PositionResponse> positionResponses = new ArrayList<>();

        for (Position position : positions) {

            // Cria um objeto de PositionResponse
            PositionResponse response = new PositionResponse(
                    position.getSymbol(),
                    position.getQuantity(),
                    position.getAveragePrice(),
                    position.getLastPrice());

            // Precisa repassar para o Portfolio
            positionResponses.add(response);
        }

        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getCashBalance(),
                positionResponses);

    }

    @Transactional
    public void processExecutedOrder(OrderExecuted event) {

        UUID userId = UUID.fromString(event.getUserId().toString());
        String symbol = event.getSymbol().toString();
        ExecutedOrderSide side = event.getSide();
        int quantity = (int) event.getQuantity();
        BigDecimal price = event.getPrice();

        Portfolio portfolio = getPortfolioByUserId(userId);

        BigDecimal orderValue = price.multiply(BigDecimal.valueOf(quantity));

        Optional<Position> existingPosition = positionRepository.findByPortfolioIdAndSymbol(
                portfolio.getId(),
                symbol);

        if (side == ExecutedOrderSide.BUY) {

            Position position;

            if (existingPosition.isPresent()) {

                position = existingPosition.get();

                position.setAveragePrice(price, quantity);
                position.addQuantity(quantity);
                position.setLastPrice(price);

            } else {

                position = new Position(
                        portfolio,
                        symbol,
                        quantity,
                        price);

                position.setLastPrice(price);
            }

            portfolio.debitCashBalance(orderValue);

            positionRepository.save(position);
            portfolioRepository.save(portfolio);

            portfolioUpdatedEventProducer.publish(
                    portfolio,
                    position);

        } else if (side == ExecutedOrderSide.SELL) {

            Position position = existingPosition
                    .orElseThrow(() -> new IllegalStateException(
                            "Position not found for symbol " + symbol));

            if (position.getQuantity() < quantity) {
                throw new IllegalStateException(
                        "Insufficient quantity for symbol " + symbol);
            }

            position.removeQuantity(quantity);
            position.setLastPrice(price);

            portfolio.creditCashBalance(orderValue);

            portfolioRepository.save(portfolio);

            if (position.getQuantity() == 0) {

                portfolioUpdatedEventProducer.publish(
                        portfolio,
                        position);

                positionRepository.delete(position);

            } else {

                positionRepository.save(position);

                portfolioUpdatedEventProducer.publish(
                        portfolio,
                        position);
            }
        }
    }

}
