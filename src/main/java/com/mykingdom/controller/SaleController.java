package com.mykingdom.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mykingdom.dtos.CategoryDTO;
import com.mykingdom.dtos.SaleDTO;
import com.mykingdom.entity.CategoryEntity;
import com.mykingdom.entity.SaleEntity;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.CategoryRepository;
import com.mykingdom.repository.ProductRepository;
import com.mykingdom.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("sale")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    private ResponseEntity<?> createSale(
            @ModelAttribute("name") String name,
            @RequestParam("image") MultipartFile image,
            @ModelAttribute("sale") int sale,
            @RequestParam("category") List<Long> categoryId
    ) throws IOException {
        SaleEntity saleEntity =saleRepository.findByName(name);
        if(saleEntity!=null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Existed sale");
        }
        Map result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");
        List<CategoryEntity> categoryEntities= categoryRepository.findAllById(categoryId);
        categoryEntities.forEach(categoryEntity -> {
            if(categoryEntity.getSale()!=null){
                throw new ApiException(HttpStatus.BAD_REQUEST,"fail");
            }
        });
        SaleEntity saleEntity1=SaleEntity.builder()
                .sale(sale)
                .image(imageUrl)
                .name(name)
                .categories(categoryEntities)
                .build();
        saleRepository.save(saleEntity1);
        categoryEntities.forEach(categoryEntity -> {
            categoryEntity.setSale(saleEntity1);
            categoryEntity.getProducts().forEach(
                    productEntity -> {
                        productEntity.setSaleOff(sale);
                    }
            );
            productRepository.saveAll(categoryEntity.getProducts());
        });
        categoryRepository.saveAll(categoryEntities);
        return ResponseEntity.ok("ok");
    }

    @PutMapping
    private ResponseEntity<?> editSale(
            @ModelAttribute("id") Long id,
            @ModelAttribute("name") String name,
            @RequestParam("image") MultipartFile image,
            @ModelAttribute("sale") int sale,
            @RequestParam("category") List<Long> categoryId
    ) throws IOException {
        SaleEntity saleEntity =saleRepository.findById(id).get();
        Map result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");
        List<CategoryEntity> categoryEntities= categoryRepository.findAllById(categoryId);
        categoryEntities.forEach(categoryEntity -> {
            if(categoryEntity.getSale()!=null){
                throw new ApiException(HttpStatus.BAD_REQUEST,"fail");
            }
        });
        SaleEntity saleEntity1=SaleEntity.builder()
                .id(saleEntity.getId())
                .sale(sale)
                .image(imageUrl)
                .name(name)
                .categories(categoryEntities)
                .build();
        saleRepository.save(saleEntity1);
        categoryEntities.forEach(categoryEntity -> {
            categoryEntity.setSale(saleEntity1);
            categoryEntity.getProducts().forEach(
                    productEntity -> {
                        productEntity.setSaleOff(sale);
                    }
            );
            productRepository.saveAll(categoryEntity.getProducts());
        });
        categoryRepository.saveAll(categoryEntities);
        return ResponseEntity.ok("ok");
    }

    @GetMapping
    private ResponseEntity<?> getAllSale(){
        List<SaleEntity> saleEntities=saleRepository.findAll();
        return ResponseEntity.ok(saleEntities.stream().map(saleEntity->{
            return SaleDTO.builder()
                    .id(saleEntity.getId())
                    .name(saleEntity.getName())
                    .sale(saleEntity.getSale())
                    .image(saleEntity.getImage())
                    .categories(saleEntity.getCategories().stream().map(category->{
                        return CategoryDTO.builder()
                                .name(category.getName())
                                .id(category.getId())
                                .image(category.getImage())
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList()));
    }
    @DeleteMapping
    private ResponseEntity<?> deleteSale(  @RequestParam Long saleId){
        SaleEntity saleEntity=saleRepository.findById(saleId).orElseThrow(()->{
            throw new ApiException(HttpStatus.BAD_REQUEST,"Can't find sale!");
        });
        saleEntity.getCategories().forEach(categoryEntity -> {
            categoryEntity.getProducts().forEach(
                    productEntity -> {
                        productEntity.setSaleOff(0);
                    }
            );
            productRepository.saveAll(categoryEntity.getProducts());
            categoryEntity.setSale(null);
            categoryRepository.save(categoryEntity);
        });
        saleRepository.delete(saleEntity);
        return ResponseEntity.ok("");
    }
}
