package com.mykingdom.controller;

import com.mykingdom.dtos.StatisticDTO;
import com.mykingdom.entity.BillEntity;
import com.mykingdom.repository.BillItemRepository;
import com.mykingdom.repository.BillRepository;
import com.mykingdom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("statistic")
public class StatisticController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillItemRepository billItemRepository;

    @GetMapping
    private ResponseEntity<?> getDonHang(@RequestParam Date startDate,@RequestParam Date endDate){
        List<BillEntity> billEntityList=billRepository.findBillsByDateRange(startDate,endDate);
        return ResponseEntity.ok(StatisticDTO.builder()
                        .donHuy(billEntityList.stream().filter(billEntity -> {
                            return billEntity.getStatus().equals("CANCELED");
                        }).collect(Collectors.toList()).size())
                        .donThanhcong(billEntityList.stream().filter(billEntity -> {
                            return billEntity.getStatus().equals("COMPLETED");
                        }).collect(Collectors.toList()).size())
                        .donDangCho(billEntityList.stream().filter(billEntity -> {
                            return billEntity.getStatus().equals("PENDING");
                        }).collect(Collectors.toList()).size())
                        .donDangGiao(billEntityList.stream().filter(billEntity -> {
                            return billEntity.getStatus().equals("SHIPPING");
                        }).collect(Collectors.toList()).size())
                        .donDangChuanBi(billEntityList.stream().filter(billEntity -> {
                            return billEntity.getStatus().equals("PREPARING");
                        }).collect(Collectors.toList()).size())
                .build());
    }

    @GetMapping("/getDoanhThu")
    private ResponseEntity<?> getDoanhThu (@RequestParam Date startDate,@RequestParam Date endDate){
        List<BillEntity> billEntityList=billRepository.findBillsByDateRange(startDate,endDate);
        AtomicInteger doanhthu= new AtomicInteger();
        billEntityList.forEach(billEntity -> {
            if(billEntity.getStatus().equals("COMPLETED")){
                billEntity.getBillItems().forEach(item->{
                    doanhthu.addAndGet(item.getProduct().getPrice()-item.getProduct().getPrice()*item.getProduct().getSaleOff()/100);
                });
            }
        });
        return ResponseEntity.ok(StatisticDTO.builder()
                        .doanhthu(doanhthu.get())
                .build());
    }

}
