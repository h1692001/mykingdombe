package com.mykingdom.dtos;

import com.mykingdom.entity.BrandEntity;
import com.mykingdom.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;
    private String SKU;
    private int price;
    private int saleOff;
    private String des;
    private int amount;
    private String topic;
    private String madeIn;
    private String VTId;
    private String age;
    private BrandDTO brand;
    private List<String> images;
    private String gender;
    private CategoryDTO category;
}
