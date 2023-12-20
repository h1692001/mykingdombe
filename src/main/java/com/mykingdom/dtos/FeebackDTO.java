package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FeebackDTO {
    private GetUserResponse getUserResponse;
    private String content;
    private int vote;
    private Date createdAt;

}
