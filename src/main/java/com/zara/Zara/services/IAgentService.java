package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;

import java.util.Collection;

public interface IAgentService {

    Agent save(Agent agent);
    Agent findByPhoneNumber(String phoneNumber);
    Agent findByAgentNumber(String agentNumber);
    Collection<Agent> findAll();
    Collection<Agent>findByStatus(String status);
}
