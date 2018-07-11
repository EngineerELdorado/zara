package com.zara.Zara.services;

import com.zara.Zara.entities.AppUser;

import java.util.Collection;
import java.util.Optional;

public interface IUserService {

    AppUser addUser(AppUser appUser);
    AppUser findByAccountNumber(String accounNumber);
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
    AppUser findByAgentNumber(String agentNumber);
    Double getUserBalance(String accountNumber);
    Collection<AppUser> getAll();
}
