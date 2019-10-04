package com.zara.Zara.repositories;

import com.zara.Zara.entities.Agent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    @Query(value = "select * from agents where phone_number =?1", nativeQuery = true)
    Agent findByPhoneNumber(String phoneNumber);
    Agent findByAgentNumber(String agentNumber);
    @Query(value = "select * from agents where status =?1 order by id desc", nativeQuery = true)
    Collection<Agent>findByStatus(String status);

    @Override
    @Query(value = "select * from agents order by id desc", nativeQuery = true)
    List<Agent> findAll();

    @Query(value = "select  from agents where creation_date between ?1 and ?2 and lower(full_name) like %?3%", nativeQuery = true)
    Page<Agent> findPagedAgent(Long start, Long end, String param, Pageable pageable);
}
