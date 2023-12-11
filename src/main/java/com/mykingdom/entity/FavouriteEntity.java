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
public class FavouriteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private UserEntity owner;

    @OneToMany(mappedBy = "favourite")
    private List<FavouriteProductEntity> items;

}
