package com.mykingdom.service;

import com.mykingdom.dtos.AuthDto;
import com.mykingdom.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    AuthDto getUser(String email);


    UserEntity getUserById(Long userId);

    void updateUserById(Long userId, UserEntity userEntity);

    AuthDto updateUserByIdWithImage(Long userId, AuthDto authDto);
}
