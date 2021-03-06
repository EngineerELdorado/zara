package com.zara.Zara.repositories;

import com.zara.Zara.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCodeIgnoreCase(String code);
    @Query(value="SELECT * FROM currencies order by name ASC", nativeQuery=true)
    List<Currency> findAllOrderByName();
}
