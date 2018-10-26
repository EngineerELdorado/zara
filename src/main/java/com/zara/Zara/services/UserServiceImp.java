package com.zara.Zara.services;

import com.zara.Zara.entities.AppUser;
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
    public Collection<AppUser> getAll() {
        return userRepository.getAll();
    }


}
