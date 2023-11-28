package com.mykingdom.repository;

import com.mykingdom.entity.CartEntity;
import com.mykingdom.entity.CartProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProductEntity,Long> {
    void deleteAllByCart(CartEntity cartEntity);

    List<CartProductEntity> findAllByCart(CartEntity cartEntity);
}
