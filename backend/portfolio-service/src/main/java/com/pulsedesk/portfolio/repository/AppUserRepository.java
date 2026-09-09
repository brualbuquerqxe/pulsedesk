package com.pulsedesk.portfolio.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsedesk.portfolio.entity.AppUser;

public interface AppUserRepository
        extends JpaRepository<AppUser, UUID> {
}
