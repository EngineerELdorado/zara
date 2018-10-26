package com.zara.Zara.services;

import com.zara.Zara.entities.AppUser;

import java.util.Collection;
import java.util.Optional;

public interface IUserService {

    AppUser addUser(AppUser appUser);
    Collection<AppUser> getAll();
}
