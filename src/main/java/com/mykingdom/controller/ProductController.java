package com.mykingdom.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mykingdom.dtos.*;
import com.mykingdom.entity.*;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.*;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CartProductRepository cartProductRepository;


    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CartRepository cartRepository;

    @GetMapping
    private ResponseEntity<List<ProductDTO>> getAllProduct() {
        List<ProductEntity> products = productRepository.findAll();

        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(product.getBrand(), brandDTO);
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(product, productDTO);
            productDTO.setBrand(brandDTO);
            List<String> imgs = new ArrayList<>();
            product.getImages().forEach(img -> {
                imgs.add(img.getImage());
            });
            productDTO.setImages(imgs);
            productDTO.setCategory(CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build());
            returnValue.add(productDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/getBestSaleOff")
    private ResponseEntity<List<ProductDTO>> getBestSaleOff() {
        List<ProductEntity> products = productRepository.findAll();

        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            if (product.getSaleOff() > 40) {
                BrandDTO brandDTO = new BrandDTO();
                BeanUtils.copyProperties(product.getBrand(), brandDTO);
                ProductDTO productDTO = new ProductDTO();
                BeanUtils.copyProperties(product, productDTO);
                productDTO.setBrand(brandDTO);
                List<String> imgs = new ArrayList<>();
                product.getImages().forEach(img -> {
                    imgs.add(img.getImage());
                });
                productDTO.setImages(imgs);
                productDTO.setCategory(CategoryDTO.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build());
                returnValue.add(productDTO);
            }
        });
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/getByCategory")
    private ResponseEntity<List<ProductDTO>> getBestSaleOff(@RequestParam Long categoryId) {
        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(categoryId);
        List<ProductEntity> products = productRepository.findAllByCategory(categoryEntity.get());
        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(product.getBrand(), brandDTO);
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(product, productDTO);
            productDTO.setBrand(brandDTO);
            List<String> imgs = new ArrayList<>();
            product.getImages().forEach(img -> {
                imgs.add(img.getImage());
            });
            productDTO.setImages(imgs);
            productDTO.setCategory(CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build());
            returnValue.add(productDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/getByBrand")
    private ResponseEntity<List<ProductDTO>> getAllByBrand(@RequestParam Long brandId) {
        Optional<BrandEntity> brandEntity = brandRepository.findById(brandId);
        List<ProductEntity> products = productRepository.findAllByBrand(brandEntity.get());
        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(product.getBrand(), brandDTO);
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(product, productDTO);
            productDTO.setBrand(brandDTO);
            List<String> imgs = new ArrayList<>();
            product.getImages().forEach(img -> {
                imgs.add(img.getImage());
            });
            productDTO.setImages(imgs);
            productDTO.setCategory(CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build());
            returnValue.add(productDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/getById")
    private ResponseEntity<ProductDTO> getAllById(@RequestParam Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        BrandDTO brandDTO = new BrandDTO();
        BeanUtils.copyProperties(product.get().getBrand(), brandDTO);
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product.get(), productDTO);
        productDTO.setBrand(brandDTO);
        List<String> imgs = new ArrayList<>();
        product.get().getImages().forEach(img -> {
            imgs.add(img.getImage());
        });
        productDTO.setImages(imgs);
        productDTO.setCategory(CategoryDTO.builder()
                .id(product.get().getCategory().getId())
                .name(product.get().getCategory().getName())
                .build());
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping
    private ResponseEntity<?> createProduct(@ModelAttribute("name") String name,
                                            @ModelAttribute("SKU") String SKU,
                                            @ModelAttribute("price") int price,
                                            @ModelAttribute("saleOff") int saleOff,
                                            @ModelAttribute("des") String des,
                                            @ModelAttribute("amount") int amount,
                                            @ModelAttribute("topic") String topic,
                                            @ModelAttribute("madeIn") String madeIn,
                                            @ModelAttribute("VTId") String VTId,
                                            @ModelAttribute("age") String age,
                                            @ModelAttribute("brand") Long brand,
                                            @RequestParam("images") List<MultipartFile> images,
                                            @ModelAttribute("category") Long category,
                                            @ModelAttribute("gender") String gender) {
        ProductEntity check = productRepository.findByName(name);
        if (check != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Existed product");
        }
        Optional<BrandEntity> brandEntity = brandRepository.findById(brand);
        if (brandEntity.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Can't find brand");
        }
        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(category);
        if (categoryEntity.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Can't find category");
        }
        ProductEntity newProduct = ProductEntity.builder()
                .name(name)
                .SKU(SKU)
                .price(price)
                .saleOff(saleOff)
                .des(des)
                .amount(amount)
                .topic(topic)
                .madeIn(madeIn)
                .VTId(VTId)
                .age(age)
                .gender(gender)
                .brand(brandEntity.get())
                .category(categoryEntity.get())
                .build();
        List<ProductImageEntity> imageEntities = new ArrayList<>();
        images.forEach(image -> {
            Map result = null;
            try {
                result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) result.get("secure_url");
                ;
                ProductImageEntity productImageEntity = new ProductImageEntity();
                productImageEntity.setProduct(newProduct);
                productImageEntity.setImage(imageUrl);
                imageEntities.add(productImageEntity);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't upload image");
            }
        });
        newProduct.setImages(imageEntities);
        productRepository.save(newProduct);
        productImageRepository.saveAll(imageEntities);

        return ResponseEntity.ok("Created product");
    }

    @GetMapping("/getAllBill")
    private ResponseEntity<List<BillDTO>> getAllBill(){
        List<BillEntity> billEntityList=billRepository.findAll();
        List<BillDTO> returnValue=new ArrayList<>();
        billEntityList.forEach(billEntity -> {
            BillDTO billDTO=BillDTO.builder()
                    .id(billEntity.getId())
                    .address(billEntity.getAddress().getAddress())
                    .phone(billEntity.getAddress().getPhone())
                    .name(billEntity.getAddress().getName())
                    .status(billEntity.getStatus())
                    .paymentCode(billEntity.getPaymentCode())
                    .createdAt(billEntity.getCreatedAt())
                    .paymentMethod(billEntity.getPaymentMethod())
                    .billItemDTOS(billEntity.getBillItems().stream().map(billItemEntity -> {
                        ProductDTO productDTO=new ProductDTO();
                        BeanUtils.copyProperties(billItemEntity.getProduct(),productDTO);

                        return BillItemDTO.builder()
                                .amount(billItemEntity.getAmount())
                                .productDTO(productDTO)
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
            returnValue.add(billDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @PutMapping("/changeStatusBill")
    private ResponseEntity<?> changeStatusBill(@RequestBody BillDTO billDTO) {
        Optional<BillEntity> bill = billRepository.findById(billDTO.getId());
        bill.get().setStatus(billDTO.getStatus());
        billRepository.save(bill.get());
        return ResponseEntity.ok("Success");
    }

}
