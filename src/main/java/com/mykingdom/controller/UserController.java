package com.mykingdom.controller;
import com.mykingdom.dtos.GetUserResponse;
import com.mykingdom.dtos.RegisterCommand;
import com.mykingdom.entity.CartEntity;
import com.mykingdom.entity.FavouriteEntity;
import com.mykingdom.entity.UserEntity;
import com.mykingdom.exception.ApiException;

import com.mykingdom.repository.UserRepository;
import com.mykingdom.security.Role;
import com.mykingdom.security.SecurityContants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Key getSigningKey() {
        byte[] keyBytes = SecurityContants.getTokenSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @GetMapping("/getCurrentUser")
    @CrossOrigin
    private ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token.replace(SecurityContants.TOKEN_PREFIX, "")).getBody();
        String user = claims.getSubject();
        UserEntity userEntity=userRepository.findByEmail(user);
        return ResponseEntity.ok(GetUserResponse.builder()
                .id(userEntity.getId())
                .avatar(userEntity.getAvatar())
                .email(userEntity.getEmail())
                .fullname(userEntity.getFullname())
                .role(userEntity.getRole())
                   
                .build());
    }

    @GetMapping("/getAllUsers")
    @CrossOrigin
    private ResponseEntity<?> getAllUsers(){
        List<UserEntity> users=userRepository.findAll();
        List<GetUserResponse> getUserResponses=new ArrayList<>();
        users.forEach(userEntity -> {
            GetUserResponse getUserResponse= GetUserResponse.builder()
                    .role(userEntity.getRole())
                    .fullname(userEntity.getFullname())
                    .email(userEntity.getEmail())
                    .id(userEntity.getId())
                    .avatar(userEntity.getAvatar())
                    .build();

            getUserResponses.add(getUserResponse);
        });
        return ResponseEntity.ok(getUserResponses);
    }
}