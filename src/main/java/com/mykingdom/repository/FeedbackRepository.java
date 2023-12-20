package com.mykingdom.repository;

import com.mykingdom.entity.FeedbackEntity;
import com.mykingdom.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity,Long> {
    List<FeedbackEntity> findAllByProduct(ProductEntity productEntity);
}
