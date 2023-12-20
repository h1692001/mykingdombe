package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillItemDTO {
    private Long id;
    private ProductDTO productDTO;
    private int amount;
    private int vote;
    private int isVoted;
    private int price;
    private String contentVote;
}
