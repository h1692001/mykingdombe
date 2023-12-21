package com.mykingdom.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mykingdom.dtos.BrandDTO;
import com.mykingdom.dtos.CategoryDTO;
import com.mykingdom.dtos.CreateCategoryCommand;
import com.mykingdom.entity.CategoryEntity;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.CategoryRepository;
import com.mykingdom.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("category")
public class CategoryController
{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    private ResponseEntity<?> getAllCategory(){
        List<CategoryDTO> returnValue=new ArrayList<>();
        List<CategoryEntity> categoryEntities=categoryRepository.findAll();
        categoryEntities.forEach(categoryEntity -> {
            CategoryDTO categoryDTO=new CategoryDTO();
            BeanUtils.copyProperties(categoryEntity,categoryDTO);
            List<BrandDTO> brandDTOS=new ArrayList<>();
            categoryEntity.getBrand().forEach(brandEntity -> {
                BrandDTO brandDTO=new BrandDTO();
                BeanUtils.copyProperties(brandEntity,brandDTO);
                brandDTOS.add(brandDTO);
            });
            categoryDTO.setBrands(brandDTOS);
            categoryDTO.setHidden(categoryEntity.getIsHidden());
            returnValue.add(categoryDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @PostMapping
    private ResponseEntity<?> createCategory(@RequestParam("logo") MultipartFile logo, @ModelAttribute("name") String name) throws IOException {
        CategoryEntity check =categoryRepository.findByName(name);
        if(check!=null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Existed category");
        }
        Map result = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");

        CategoryEntity newCate= CategoryEntity.builder()
                .name(name)
                .image(imageUrl)
                .build();
        categoryRepository.save(newCate);
        return ResponseEntity.ok("Created");
    }
    @PutMapping
    private ResponseEntity<?> updateCategory(@RequestParam("logo") MultipartFile logo, @ModelAttribute("name") String name, @ModelAttribute("id") Long id) throws IOException {
        CategoryEntity check =categoryRepository.findById(id).get();
        Map result = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");
        CategoryEntity newCate= CategoryEntity.builder()
                .id(check.getId())
                .name(name)
                .image(imageUrl)
                .build();
        categoryRepository.save(newCate);
        return ResponseEntity.ok("Created");
    }
    @GetMapping("/disable")
    private ResponseEntity<?> disable(@RequestParam Long categoryId){
        CategoryEntity category=categoryRepository.findById(categoryId).orElseThrow(()-> new ApiException(HttpStatus.BAD_REQUEST, "CantFind category"));
        category.setIsHidden(true);
        category.getProducts().forEach(productEntity -> {
            productEntity.setIsHidden(true);
        });
        productRepository.saveAll(category.getProducts());
        categoryRepository.save(category);
        return ResponseEntity.ok("");
    }
    @GetMapping("/enable")
    private ResponseEntity<?> enable(@RequestParam Long categoryId){
        CategoryEntity category=categoryRepository.findById(categoryId).orElseThrow(()-> new ApiException(HttpStatus.BAD_REQUEST, "CantFind category"));
        category.setIsHidden(false);
        category.getProducts().forEach(productEntity -> {
            productEntity.setIsHidden(false);
        });
        productRepository.saveAll(category.getProducts());
        categoryRepository.save(category);
        return ResponseEntity.ok("");
    }
}
