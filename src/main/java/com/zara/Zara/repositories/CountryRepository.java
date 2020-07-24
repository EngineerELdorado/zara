package com.zara.Zara.repositories;

import com.zara.Zara.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCodeIgnoreCase(String code);
    Optional<Country> findByIsoName3IgnoreCase(String iso);
    @Query(value="SELECT * FROM countries order by name ASC", nativeQuery=true)
    List<Country> findAllOrderByName();
}
