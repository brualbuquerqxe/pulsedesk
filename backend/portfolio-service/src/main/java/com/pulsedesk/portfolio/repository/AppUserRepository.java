package com.pulsedesk.portfolio.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsedesk.portfolio.entity.AppUser;

/* Explicação: JpaRepository é uma interface fornecida pelo Spring Data JPA (framework que facilita a comunicação com bancos de dados). Assim, a interface terá todas as operações de JPA Repository e serão aplicadas na classe Appuser com chave primária UUID. Então, ele tá basicamente falando para tirar as info necessárias que serão usadas no AppUser.  */

// Repository fornece acesso aos dados declarados na entidade
public interface AppUserRepository
        extends JpaRepository<AppUser, UUID> {
}
