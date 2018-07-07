package com.zara.Zara.security;

import com.zara.Zara.models.AppUser;
import com.zara.Zara.models.Role;
import com.zara.Zara.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            LOGGER.info("BEFORE LOADING USER "+username);
            AppUser applicationUser = userRepository.findByAccountNumber(username);
            LOGGER.info("LOADED USER BY USERNAME---> "+applicationUser.getFullName());
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            for (Role role : applicationUser.getRoles()){
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            }
            return new User(applicationUser.getAccountNumber(), applicationUser.getPin(), grantedAuthorities);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;


    }
}
