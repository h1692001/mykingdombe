package com.mykingdom.repository;

import com.mykingdom.entity.BillItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillItemRepository extends JpaRepository<BillItemEntity,Long> {
}
