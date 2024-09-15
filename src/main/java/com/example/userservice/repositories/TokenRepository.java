package com.example.userservice.repositories;

import com.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);
    //check whether the tokenValue isDeleted or not
    Optional<Token> findByTokenValueAndDeletedEquals(String tokenValue, boolean deleted);
    Optional<Token> findByTokenValueAndDeletedEqualsAndExpiryAtGreaterThan(String tokenValue, boolean deleted, Date expiryGreaterThan);

    //Suppose currentTime is Sep 12 2024 9.00PM, tokenValue = jhkjh8iui2kjkj24ljkj2, deleted = false
    //if the expiry time will be greater than Sep 12 2024 9.00PM, then the validation of token gets fails
    //if the deleted = true, then the validation of token gets fails
}
