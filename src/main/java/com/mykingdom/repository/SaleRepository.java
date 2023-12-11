package com.mykingdom.repository;

import com.mykingdom.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity,Long> {
    SaleEntity findByName(String name);
}
