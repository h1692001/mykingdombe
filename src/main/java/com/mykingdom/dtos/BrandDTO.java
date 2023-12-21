package com.mykingdom.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandDTO {
    private Long id;
    private String name;
    private String comeFrom;
    private String logo;
    private boolean isHidden;
}
