package com.mykingdom.repository;

import com.mykingdom.entity.FavouriteProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteProductRepository extends JpaRepository<FavouriteProductEntity,Long> {
}
