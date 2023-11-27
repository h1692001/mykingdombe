package com.mykingdom.repository;

import com.mykingdom.entity.CartEntity;
import com.mykingdom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity,Long> {
    CartEntity findByUser(UserEntity user);
}
