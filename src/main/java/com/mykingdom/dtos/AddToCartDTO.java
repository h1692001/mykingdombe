package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddToCartDTO {
    private Long cartId;
    private Long productId;
    private int amount;
}
