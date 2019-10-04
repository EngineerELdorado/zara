package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface IAgentService {

    Agent save(Agent agent);
    Agent findOne(Long id);
    Agent findByPhoneNumber(String phoneNumber);
    Agent findByAgentNumber(String agentNumber);
    Page<Agent> findAll(int page, int size, Long start, Long end, String param);
    Collection<Agent>findByStatus(String status);
}
