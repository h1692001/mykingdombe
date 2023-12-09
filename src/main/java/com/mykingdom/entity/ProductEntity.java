package com.mykingdom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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
    private int vote;

    private Boolean isHidden;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product")
    private List<CartProductEntity> cart;

    @OneToMany(mappedBy = "product")
    private List<FavouriteProductEntity> favourite;

    @OneToMany(mappedBy = "product")
    private List<BillItemEntity> billItems;

    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST},mappedBy = "product")
    private List<ProductImageEntity>  images;

    private Date createdAt;

    private String gender;

}
