package com.zara.Zara.configs.security;

import com.zara.Zara.entities.Admin;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Admin appAdmin = userService.findByEmail(username).orElseThrow(() -> new Zaka400Exception("Admin not found"));
        UserDetails userDetails;
        try {
            userDetails = new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return appAdmin.getPassword();
                }

                @Override
                public String getUsername() {
                    return appAdmin.getEmail();
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
        } catch (Exception e) {
            throw new Zaka400Exception("Account not found");
        }
        return userDetails;
    }

}
