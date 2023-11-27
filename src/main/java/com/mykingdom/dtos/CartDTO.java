package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartDTO {
    private Long id;
    private List<CartProductDTO> cartProducts;
}
