package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FavoutireDTO {
    private List<Long> productId;
    private List<ProductDTO> products;
    private Long userId;
    private Long favouriteProductId;
}
