package com.mykingdom.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class StatisticDTO {
    private int doanhthu;
    private int donHuy;
    private int donThanhcong;
    private int donDangChuanBi;

    private int donDangGiao;
    private int donDangCho;
    private Date startDate;
    private Date endDate;
}
