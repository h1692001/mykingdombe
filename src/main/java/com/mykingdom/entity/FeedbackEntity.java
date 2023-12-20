package com.mykingdom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private int point;
    private String content;
    private Date createdAt;

    @ManyToOne
    private ProductEntity product;

    @ManyToOne
    private UserEntity user;

}
