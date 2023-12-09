package com.mykingdom.repository;

import com.mykingdom.entity.FavouriteEntity;
import com.mykingdom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteRepository extends JpaRepository<FavouriteEntity,Long> {
    FavouriteEntity findByOwner(UserEntity userEntity);
}
