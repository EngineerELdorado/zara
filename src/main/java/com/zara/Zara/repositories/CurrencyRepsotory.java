package com.zara.Zara.repositories;

import com.zara.Zara.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepsotory extends JpaRepository<Currency, Long> {

    Optional<Currency>findByCode(String code);
}
