package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class    BillItemDTO {
    private ProductDTO productDTO;
    private int amount;
}
