package com.mykingdom.repository;

import com.mykingdom.entity.BillEntity;
import com.mykingdom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity,Long> {
    List<BillEntity> findAllByOwner(UserEntity userEntity);
}
