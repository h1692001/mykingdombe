package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoutireDTO {
    private Long productId;
    private Long userId;
}
