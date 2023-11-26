package com.mykingdom.serviceImpl;

import com.mykingdom.dtos.AuthDto;
import com.mykingdom.entity.UserEntity;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.UserRepository;
import com.mykingdom.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public AuthDto getUser(String email) {
        UserEntity userEntity=userRepository.findByEmail(email);
        if(userEntity==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Not found user");
        AuthDto returnValue=new AuthDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserEntity getUserById(Long userId) {
        UserEntity returnValue=userRepository.findById(userId).get();
        return returnValue;
    }

    @Override
    public void updateUserById(Long userId, UserEntity userEntity) {
        UserEntity user=userRepository.findById(userId).get();
        userRepository.save(userEntity);
    }

    @Override
    public AuthDto updateUserByIdWithImage(Long userId, AuthDto authDto) {
        UserEntity user=userRepository.findById(userId).get();
        user.setEmail(authDto.getEmail());
        user.setAvatar(authDto.getAvatar());
        UserEntity storedUser=userRepository.save(user);
        AuthDto returnValue=new AuthDto();
        BeanUtils.copyProperties(storedUser,returnValue);
        return returnValue;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


}
