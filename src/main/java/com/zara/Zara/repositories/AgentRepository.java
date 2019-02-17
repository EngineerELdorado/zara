package com.zara.Zara.repositories;

import com.zara.Zara.entities.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Agent findByPhoneNumber(String phoneNumber);
    Agent findByAgentNumber(String agentNumber);
    @Query(value = "select * from agent where status =?1 order by id desc", nativeQuery = true)
    Collection<Agent>findByStatus(String status);

    @Override
    @Query(value = "select * from agent order by id desc", nativeQuery = true)
    List<Agent> findAll();
}
