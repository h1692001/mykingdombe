package com.mykingdom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FavouriteProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "favourite_id")
    private FavouriteEntity favourite;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
