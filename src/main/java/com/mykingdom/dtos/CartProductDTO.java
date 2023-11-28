package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartProductDTO {
    private ProductDTO productDTO;
    private int amount;
    private Long id;
}
