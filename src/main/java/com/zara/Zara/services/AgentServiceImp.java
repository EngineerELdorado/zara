package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Agent findOne(Long id) {
        return agentRepository.getOne(id);
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
    public Page<Agent> findAll(int page, int size, Long start, Long end, String param) {

        Pageable pageable = PageRequest.of(page,size);
        return agentRepository.findPagedAgent(start,end,param,pageable);
    }

    @Override
    public Collection<Agent> findByStatus(String status) {
        return agentRepository.findByStatus(status);
    }

    @Override
    public Long findCount(Long start, Long end) {
        return agentRepository.findCount(start,end);
    }
}
