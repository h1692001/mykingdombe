package com.mykingdom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String status;
    private Date createdAt;

    @ManyToOne
    private AddressEntity address;
    @ManyToOne
    private UserEntity owner;

    @OneToMany
    private List<BillItemEntity> billItems;
}
