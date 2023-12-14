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
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Key getSigningKey() {
        byte[] keyBytes = SecurityContants.getTokenSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostMapping("/register")
    private ResponseEntity<?> registerClient(@RequestBody RegisterCommand registerCommand){
        Optional<UserEntity> user=userRepository.findByEmailOrPhone(registerCommand.getEmail(),registerCommand.getPhone());
        if(user.isPresent()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Existed email or mobile phone");
        }
        UserEntity user1=new UserEntity();
        BeanUtils.copyProperties(registerCommand,user1);
        user1.setAvatar("https://res.cloudinary.com/dyvgnrswn/image/upload/v1684721106/mhqiehkoysbdiquyu88a.png");
        user1.setRole(Role.USER);
        CartEntity cart=new CartEntity();
        cart.setUser(user1);
        FavouriteEntity favourite=new FavouriteEntity();
        favourite.setOwner(user1);
        user1.setCart(cart);
        user1.setFavourite(favourite);
        user1.setPassword(bCryptPasswordEncoder.encode(registerCommand.getPassword()));
        userRepository.save(user1);
        return ResponseEntity.ok("Created User");
    }
    @PostMapping("/registerNV")
    private ResponseEntity<?> registerNV(@RequestBody RegisterCommand registerCommand){
        UserEntity user=userRepository.findByEmail(registerCommand.getEmail());
        if(user!=null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Existed email or mobile phone");
        }
        UserEntity user1=new UserEntity();
        BeanUtils.copyProperties(registerCommand,user1);
        user1.setAvatar("https://res.cloudinary.com/dyvgnrswn/image/upload/v1684721106/mhqiehkoysbdiquyu88a.png");
        user1.setRole(Role.NV);
        CartEntity cart=new CartEntity();
        cart.setUser(user1);
        FavouriteEntity favourite=new FavouriteEntity();
        favourite.setOwner(user1);
        user1.setCart(cart);
        user1.setFavourite(favourite);
        user1.setPassword(bCryptPasswordEncoder.encode(registerCommand.getPassword()));
        userRepository.save(user1);
        return ResponseEntity.ok("Created User");
    }

    @PostMapping("/registerAdmin")
    private ResponseEntity<?> registerAdmin(@RequestBody RegisterCommand registerCommand){
        Optional<UserEntity> user=userRepository.findByEmailOrPhone(registerCommand.getEmail(),registerCommand.getPhone());
        if(user.isPresent()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Existed email or mobile phone");
        }
        UserEntity user1=new UserEntity();
        BeanUtils.copyProperties(registerCommand,user1);
        user1.setAvatar("https://res.cloudinary.com/dyvgnrswn/image/upload/v1684721106/mhqiehkoysbdiquyu88a.png");
        user1.setRole(Role.ADMIN);
        user1.setPassword(bCryptPasswordEncoder.encode(registerCommand.getPassword()));
        CartEntity cart=new CartEntity();
        cart.setUser(user1);
        FavouriteEntity favourite=new FavouriteEntity();
        favourite.setOwner(user1);
        user1.setCart(cart);
        user1.setFavourite(favourite);
        userRepository.save(user1);
        return ResponseEntity.ok("Created User");
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

    @GetMapping("/getAllNV")
    private ResponseEntity<?> getAllNV(){
        List<UserEntity> users=userRepository.findAllByRole(Role.NV);
        List<GetUserResponse> getUserResponses=new ArrayList<>();
        users.forEach(userEntity -> {
            GetUserResponse getUserResponse= GetUserResponse.builder()
                    .role(userEntity.getRole())
                    .fullname(userEntity.getFullname())
                    .email(userEntity.getEmail())
                    .build();

            getUserResponses.add(getUserResponse);
        });
        return ResponseEntity.ok(getUserResponses);
    }
}