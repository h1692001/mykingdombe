package com.mykingdom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String image;

    @OneToMany(mappedBy = "category",cascade = {CascadeType.PERSIST,CascadeType.ALL})
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "category")
    private List<BrandEntity> brand;

}
