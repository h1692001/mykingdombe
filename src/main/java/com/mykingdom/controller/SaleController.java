package com.mykingdom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("sale")
public class SaleController {

    @PostMapping
    private ResponseEntity<?> createSale(
            @ModelAttribute("name") String name,
            @RequestParam("image") MultipartFile image,
            @ModelAttribute("sale") int sale,
            @ModelAttribute("gender") List<Long> categoryId
    ){

    }
}
