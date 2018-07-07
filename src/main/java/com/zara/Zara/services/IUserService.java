package com.zara.Zara.services;

import com.zara.Zara.models.AppUser;

import java.util.Collection;
import java.util.Optional;

public interface IUserService {

    AppUser addUser(AppUser appUser);
    AppUser findByAccountNumber(String accounNumber);
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
    Optional<AppUser>findByAgentNumber(String agentNumber);
    Double getUserBalance(String accountNumber);
    Collection<AppUser> getAll();
}
