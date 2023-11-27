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
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String SKU;
    private int price;
    private int saleOff;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String des;
    private int amount;
    private String topic;
    private String madeIn;
    private String VTId;
    private String age;

    @ManyToOne
    private BrandEntity brand;

    @ManyToOne
    private CategoryEntity category;

    @OneToMany
    private List<CartProductEntity> cart;

    @OneToMany
    private List<FavouriteProductEntity> favourite;

    @OneToMany
    private List<BillItemEntity> billItems;

    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<ProductImageEntity>  images;

    private String gender;

}
