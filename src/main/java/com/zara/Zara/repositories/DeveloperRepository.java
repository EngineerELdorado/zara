package com.zara.Zara.repositories;

import com.zara.Zara.entities.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Developer findByApiKey(String apiKey);
}
