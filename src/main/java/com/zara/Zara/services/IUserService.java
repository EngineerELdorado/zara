package com.zara.Zara.services;

import com.zara.Zara.models.AppUser;

import java.util.Collection;
import java.util.Optional;

public interface IUserService {

    public AppUser addUser(AppUser appUser);
    public AppUser findByAccountNumber(String accounNumber);
    public Optional<AppUser> findByPhoneNumber(String phoneNumber);
    public Optional<AppUser>findByAgentNumber(String agentNumber);
    public Double getUserBalance(String accountNumber);
    Collection<AppUser> getAll();
}
