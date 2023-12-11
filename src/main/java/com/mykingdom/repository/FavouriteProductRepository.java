package com.mykingdom.repository;

import com.mykingdom.entity.FavouriteProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteProductRepository extends JpaRepository<FavouriteProductEntity,Long> {

    @Query("SELECT f FROM FavouriteProductEntity f WHERE f.favourite.owner.id = :userId")
    List<FavouriteProductEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM FavouriteProductEntity f WHERE f.favourite.owner.id = :userId AND f.product.id=:productId")
    FavouriteProductEntity findByUserIdAndProductId(@Param("userId") Long userId,@Param("productId") Long productId);
}
