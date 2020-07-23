package com.zara.Zara.services;

import com.zara.Zara.entities.User;
import com.zara.Zara.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImp implements IUserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByAccountNumber(String accountNumber) {
        return null;
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public User findByAgentNumber(String agentNumber) {
        return null;
    }

    @Override
    public Double getUserBalance(String accountNumber) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }


}
