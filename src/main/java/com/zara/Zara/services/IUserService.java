package com.zara.Zara.services;

import com.zara.Zara.entities.User;

import java.util.Collection;

public interface IUserService {

    User addUser(User user);
    User findByAccountNumber(String accounNumber);
    User findByPhoneNumber(String phoneNumber);
    User findByAgentNumber(String agentNumber);
    Double getUserBalance(String accountNumber);
    Collection<User> getAll();
}
