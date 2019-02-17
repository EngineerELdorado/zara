package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AgentServiceImp implements IAgentService {

    @Autowired
    AgentRepository agentRepository;
    @Override
    public Agent save(Agent agent) {
        return agentRepository.save(agent);
    }

    @Override
    public Agent findByPhoneNumber(String phoneNumber) {
        return agentRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Agent findByAgentNumber(String agentNumber) {
        return agentRepository.findByAgentNumber(agentNumber);
    }

    @Override
    public Collection<Agent> findAll() {
        return agentRepository.findAll();
    }

    @Override
    public Collection<Agent> findByStatus(String status) {
        return agentRepository.findByStatus(status);
    }
}
