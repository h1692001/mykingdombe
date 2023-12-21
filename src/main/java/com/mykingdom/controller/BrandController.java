package com.mykingdom.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mykingdom.dtos.BrandDTO;
import com.mykingdom.dtos.RegisterCommand;
import com.mykingdom.entity.BrandEntity;
import com.mykingdom.entity.CategoryEntity;
import com.mykingdom.entity.UserEntity;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.BrandRepository;
import com.mykingdom.repository.CategoryRepository;
import com.mykingdom.repository.ProductRepository;
import com.mykingdom.repository.UserRepository;
import com.mykingdom.security.Role;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    Cloudinary cloudinary;


    @GetMapping
    private ResponseEntity<?> getAllBrand() {
        List<BrandEntity> brands = brandRepository.findAll();
        List<BrandDTO> brandDTOS = new ArrayList<>();
        brands.forEach(brandEntity -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(brandEntity, brandDTO);
            brandDTO.setHidden(brandEntity.getIsHidden() );
            brandDTOS.add(brandDTO);
        });
        return ResponseEntity.ok(brandDTOS);
    }

    @PostMapping
    private ResponseEntity<?> createBrand(@RequestParam("logo") MultipartFile logo, @ModelAttribute("name") String name, @ModelAttribute("comeFrom") String comeFrom, @ModelAttribute("category") Long category) throws IOException {
        Map result = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
        BrandEntity check = brandRepository.findByName(name);
        if (check != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Existed brand");
        }
        String imageUrl = (String) result.get("secure_url");
        Optional<CategoryEntity> categoryE=categoryRepository.findById(category);
        if(categoryE.isEmpty()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Can't fint Category");
        }
        BrandEntity brandEntity = BrandEntity.builder()
                .name(name)
                .comeFrom(comeFrom)
                .logo(imageUrl)
                .category(categoryE.get())
                .build();
        brandRepository.save(brandEntity);
        categoryE.get().getBrand().add(brandEntity);
        categoryRepository.save(categoryE.get());
        return ResponseEntity.ok("Created");
    }

    @PutMapping
    private ResponseEntity<?> updateBrand(@RequestParam("logo") MultipartFile logo, @ModelAttribute("name") String name, @ModelAttribute("id") Long id, @ModelAttribute("comeFrom") String comeFrom, @ModelAttribute("category") Long category) throws IOException {
        Map result = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
        BrandEntity check = brandRepository.findById(id).get();
        String imageUrl = (String) result.get("secure_url");
        Optional<CategoryEntity> categoryE=categoryRepository.findById(category);
        if(categoryE.isEmpty()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Can't fint Category");
        }
        BrandEntity brandEntity = BrandEntity.builder()
                .id(check.getId())
                .name(name)
                .comeFrom(comeFrom)
                .logo(imageUrl)
                .category(categoryE.get())
                .build();
        brandRepository.save(brandEntity);
        categoryE.get().getBrand().add(brandEntity);
        categoryRepository.save(categoryE.get());
        return ResponseEntity.ok("Created");
    }

    @GetMapping("/disable")
    private ResponseEntity<?> disable(@RequestParam Long brandId){
        BrandEntity category=brandRepository.findById(brandId).orElseThrow(()-> new ApiException(HttpStatus.BAD_REQUEST, "CantFind brand"));
        category.setIsHidden(true);
        category.getProducts().forEach(productEntity -> {
            productEntity.setIsHidden(true);
        });
        productRepository.saveAll(category.getProducts());
        brandRepository.save(category);
        return ResponseEntity.ok("");
    }
    @GetMapping("/enable")
    private ResponseEntity<?> enable(@RequestParam Long brandId){
        BrandEntity category=brandRepository.findById(brandId).orElseThrow(()-> new ApiException(HttpStatus.BAD_REQUEST, "CantFind brand"));
        category.setIsHidden(false);
        category.getProducts().forEach(productEntity -> {
            productEntity.setIsHidden(false);
        });
        productRepository.saveAll(category.getProducts());
        brandRepository.save(category);
        return ResponseEntity.ok("");
    }

}
