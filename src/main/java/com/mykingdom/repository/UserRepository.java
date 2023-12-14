package com.mykingdom.repository;

import com.mykingdom.entity.UserEntity;
import com.mykingdom.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);

    Optional<UserEntity> findById(Long userId);

    Optional<UserEntity> findByEmailOrPhone(String email, String phone);

    List<UserEntity> findAllByRole(Role role);
}
