package com.example.userservice.repositories;

import com.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);
    //check whether the tokenValue isDeleted or not
    Optional<Token> findByTokenValueAndDeletedEquals(String tokenValue, boolean deleted);
}
