package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SaleDTO {
    private Long id;

    private String name;
    private String image;
    private int sale;
    private List<CategoryDTO> categories;
}
