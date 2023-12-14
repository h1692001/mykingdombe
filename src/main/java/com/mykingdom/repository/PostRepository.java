package com.mykingdom.repository;

import com.mykingdom.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Long> {
    List<PostEntity> findAllByOrderByCreateAtDesc();
}
