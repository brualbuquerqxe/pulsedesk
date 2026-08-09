package com.pulsedesk.portfolio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.pulsedesk.portfolio.entity.AppUser;
import com.pulsedesk.portfolio.entity.Portfolio;
import com.pulsedesk.portfolio.entity.Position;
import com.pulsedesk.portfolio.repository.AppUserRepository;
import com.pulsedesk.portfolio.repository.PortfolioRepository;
import com.pulsedesk.portfolio.repository.PositionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioServiceApplicationTests {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PortfolioRepository portfolioRepository;

	@Autowired
	private PositionRepository positionRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReadUsersFromDatabase() {
		List<AppUser> users = appUserRepository.findAll();

		assertFalse(users.isEmpty());
	}

	@Test
	void shouldFindPortfolioByUserId() {
		List<AppUser> users = appUserRepository.findAll();

		AppUser user = users.get(0);

		Optional<Portfolio> portfolio = portfolioRepository.findByUserId(user.getId());

		assertTrue(portfolio.isPresent());
	}

	@Test
	@Transactional
	void shouldFindPositionsByPortfolioId() {
		List<AppUser> users = appUserRepository.findAll();

		AppUser user = users.get(0);

		Portfolio portfolio = portfolioRepository
				.findByUserId(user.getId())
				.orElseThrow();

		Position position = new Position(
				portfolio,
				"AAPL",
				10,
				new BigDecimal("150.00"));

		positionRepository.save(position);

		List<Position> positions = positionRepository.findByPortfolioId(portfolio.getId());

		assertFalse(positions.isEmpty());
	}

	@Test
	void shouldReturnPortfolioForExistingUser() throws Exception {

		AppUser user = appUserRepository
				.findAll()
				.get(0);

		mockMvc.perform(
				get("/api/portfolio/{userId}", user.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.portfolioId").exists())
				.andExpect(jsonPath("$.cashBalance").exists())
				.andExpect(jsonPath("$.positions").isArray());
	}

	@Test
	void shouldReturnNotFoundForUnknownUser() throws Exception {

		UUID unknownUserId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

		mockMvc.perform(
				get("/api/portfolio/{userId}", unknownUserId))
				.andExpect(status().isNotFound());
	}
}
