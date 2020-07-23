
/**
package com.zara.Zara.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zara.Zara.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.zara.Zara.constants.Keys.*;
import static com.zara.Zara.constants.Responses.LOGIN_SUCCESS;
import static com.zara.Zara.security.SecurityConstants.EXPIRATION_TIME;
import static com.zara.Zara.security.SecurityConstants.HEADER_STRING;
import static com.zara.Zara.security.SecurityConstants.TOKEN_PREFIX;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    AuthenticationManager authenticationManager;
    Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);
    User appUser;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
       try
       {
         appUser  = new ObjectMapper()
                   .readValue(request.getInputStream(), User.class);
           if (appUser==null){
               response.addHeader(RESPONSE_CODE,RESPONSE_FAILURE);
               response.addHeader(RESPONSE_MESSAGE,"Wrong Username Or Password");
               return null;
           }
           else {
               return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                       appUser.getAccountNumber(), appUser.getPin(),new ArrayList<>()
               ));
           }


       }
       catch (IOException e)
       {
           e.printStackTrace();

        throw  new RuntimeException(e);
       }
        //return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        String token = Jwts.builder()
                .setSubject(((User) authResult.getPrincipal()).getUsername())
                .claim("roles", ((User) authResult.getPrincipal()).getAuthorities().toString())
                .claim("Firstname", appUser.getFullName())
                .claim("phone", appUser.getPhone())
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
        response.addHeader("Access-Control-Expose-Headers", HEADER_STRING);
        response.addHeader(HEADER_STRING, TOKEN_PREFIX+ token);
        response.addHeader(RESPONSE_CODE,RESPONSE_SUCCESS);
        response.addHeader(RESPONSE_MESSAGE,LOGIN_SUCCESS);
        //LOGGER.info(token);
    }
}

 */
