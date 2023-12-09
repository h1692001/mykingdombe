package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class PostDTO {
    private Long id;

    private Date createAt;
    private String title;
    private String content;
    private String des;
    private String thumb;

}
