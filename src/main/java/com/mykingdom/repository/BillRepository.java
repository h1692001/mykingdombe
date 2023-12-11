package com.mykingdom.repository;

import com.mykingdom.entity.BillEntity;
import com.mykingdom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity,Long> {
    List<BillEntity> findAllByOwner(UserEntity userEntity);

    @Query("SELECT b FROM BillEntity b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<BillEntity> findBillsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
