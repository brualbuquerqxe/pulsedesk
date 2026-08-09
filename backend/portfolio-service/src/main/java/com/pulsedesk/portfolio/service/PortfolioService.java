package com.pulsedesk.portfolio.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.portfolio.dto.PortfolioResponse;
import com.pulsedesk.portfolio.dto.PositionResponse;
import com.pulsedesk.portfolio.entity.Portfolio;
import com.pulsedesk.portfolio.entity.Position;
import com.pulsedesk.portfolio.exception.PortfolioNotFoundException;
import com.pulsedesk.portfolio.repository.PortfolioRepository;
import com.pulsedesk.portfolio.repository.PositionRepository;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, PositionRepository positionRepository) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
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
}
