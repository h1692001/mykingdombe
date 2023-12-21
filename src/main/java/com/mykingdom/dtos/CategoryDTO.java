package com.mykingdom.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private String name;
    private Long id;
    private String image;
    private List<BrandDTO> brands;
    private boolean isHidden;

}
