package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class BillDTO {
    private Long id;
    private String status;
    private Date createdAt;
    private String paymentMethod;
    private String paymentCode;
    private String name;
    private String phone;
    private String address;
    private List<BillItemDTO> billItemDTOS;
    private Long userId;
    private Long cartId;
}
