package com.zara.Zara.resources;

import com.zara.Zara.dtos.responses.StatsResource;
import com.zara.Zara.specs.StatsSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsResourceService {

    @PersistenceContext
    private EntityManager entityManager;
    private final StatsSpecs statsSpecs;

    public StatsResource findStats(@Nullable Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return statsSpecs.findStats(entityManager, accountId, startDate, endDate);
    }
}
