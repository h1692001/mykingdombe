package com.mykingdom.service;

import com.mykingdom.dtos.AuthDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    AuthDto createUser(AuthDto authDto);

    AuthDto resetPassword(AuthDto authDto);
}
