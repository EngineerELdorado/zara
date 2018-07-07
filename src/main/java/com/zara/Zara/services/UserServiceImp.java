package com.zara.Zara.services;

import com.zara.Zara.models.AppUser;
import com.zara.Zara.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImp implements IUserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public AppUser addUser(AppUser appUser) {
        return userRepository.save(appUser);
    }

    @Override
    public AppUser findByAccountNumber(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Optional<AppUser> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<AppUser> findByAgentNumber(String agentNumber) {
        return userRepository.findByAgentNumber(agentNumber);
    }

    @Override
    public Double getUserBalance(String accountNumber) {
        return userRepository.getUserBalance(accountNumber);
    }

    @Override
    public Collection<AppUser> getAll() {
        return userRepository.findAll();
    }


}
