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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    private BillItemRepository billItemRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private FavouriteProductRepository favouriteProductRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;


    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    private ResponseEntity<List<ProductDTO>> getAllProduct() {
        List<ProductEntity> products = productRepository.findAll();
        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(product.getBrand(), brandDTO);
            String content = getContentFromCloudinary(product.getDes());
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

            productDTO.setDes(content);
            returnValue.add(productDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("search")
    private ResponseEntity<List<ProductDTO>> search(@RequestParam String keyword) {
        List<ProductEntity> products = productRepository.findByNameContainingIgnoreCase(keyword);
        List<ProductDTO> returnValue = new ArrayList<>();
        products.forEach(product -> {
            BrandDTO brandDTO = new BrandDTO();
            BeanUtils.copyProperties(product.getBrand(), brandDTO);
            String content = getContentFromCloudinary(product.getDes());
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

            productDTO.setDes(content);
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
                String content = getContentFromCloudinary(product.getDes());
                productDTO.setDes(content);

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
            String content = getContentFromCloudinary(product.getDes());
            productDTO.setDes(content);
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
            String content = getContentFromCloudinary(product.getDes());
            productDTO.setDes(content);
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
        List<FeedbackEntity> feedbackEntities=feedbackRepository.findAllByProduct(product.get());
        BrandDTO brandDTO = new BrandDTO();
        BeanUtils.copyProperties(product.get().getBrand(), brandDTO);
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product.get(), productDTO);
        String content = getContentFromCloudinary(product.get().getDes());
        productDTO.setDes(content);
        productDTO.setBrand(brandDTO);
        List<String> imgs = new ArrayList<>();
        product.get().getImages().forEach(img -> {
            imgs.add(img.getImage());
        });
        productDTO.setImages(imgs);

        productDTO.setIsHidden(product.get().getIsHidden());
        productDTO.setCategory(CategoryDTO.builder()
                .id(product.get().getCategory().getId())
                .name(product.get().getCategory().getName())
                .build());
        productDTO.setVote(product.get().getVote());
        productDTO.setVoteCount(feedbackEntities.size());
        productDTO.setFeedbacks(feedbackEntities.stream().map(feedbackEntity -> {
            return FeebackDTO.builder()
                    .vote(feedbackEntity.getPoint())
                    .content(feedbackEntity.getContent())
                    .getUserResponse(GetUserResponse.builder()
                            .avatar(feedbackEntity.getUser().getAvatar())
                            .fullname(feedbackEntity.getUser().getFullname())
                            .build())
                    .createdAt(feedbackEntity.getCreatedAt())
                    .build();
        }).collect(Collectors.toList()));
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
                                            @ModelAttribute("gender") String gender) throws IOException {
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
        String randomString = UUID.randomUUID().toString();
        String publicId = randomString + "_content.txt";
        Map uploadResult = cloudinary.uploader().upload(des.getBytes(StandardCharsets.UTF_8),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "raw"));

        String textFileUrl = (String) uploadResult.get("secure_url");

        ProductEntity newProduct = ProductEntity.builder()
                .name(name)
                .SKU(SKU)
                .price(price)
                .saleOff(saleOff)
                .des(textFileUrl)
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
        newProduct.setIsHidden(false);
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
                        List<String> imgs = new ArrayList<>();
                        billItemEntity.getProduct().getImages().forEach(img -> {
                            imgs.add(img.getImage());
                        });
                        productDTO.setImages(imgs);

                        return BillItemDTO.builder()
                                .amount(billItemEntity.getAmount())
                                .productDTO(productDTO)
                                .id(billItemEntity.getId())
                                .isVoted(billItemEntity.getIsVoted())
                                .price(billItemEntity.getPrice())
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
            returnValue.add(billDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @PostMapping("/voteProduct")
    private ResponseEntity<?> voteProduct(@RequestBody BillItemDTO billItemDTO){
        ProductEntity product=productRepository.findById(billItemDTO.getProductDTO().getId()).get();
        BillItemEntity billItemEntity=billItemRepository.findById(billItemDTO.getId()).get();
        billItemEntity.setIsVoted(1);
        billItemRepository.save(billItemEntity);
        product.setVote(product.getVote()+billItemDTO.getVote());
        productRepository.save(product);
        FeedbackEntity feedbackEntity=FeedbackEntity.builder()
                .content(billItemDTO.getContentVote())
                .point(billItemDTO.getVote())
                .user(billItemEntity.getBill().getOwner())
                .product(product)
                .createdAt(new Date())
                .build();
        feedbackRepository.save(feedbackEntity);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/updateProduct")
    private ResponseEntity<?> updateProduct(@ModelAttribute("name") String name,
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
                                            @ModelAttribute("gender") String gender,
                                            @ModelAttribute("isHidden") Boolean isHidden,
                                            @ModelAttribute("id") Long id
    ) throws IOException {
        ProductEntity check = productRepository.findByName(name);
        if (check != null &&check.getId()!=id) {
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
        String randomString = UUID.randomUUID().toString();
        String publicId = randomString + "_content.txt";
        Map uploadResult = cloudinary.uploader().upload(des.getBytes(StandardCharsets.UTF_8),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "raw"));

        String textFileUrl = (String) uploadResult.get("secure_url");

        ProductEntity newProduct = ProductEntity.builder()
                .name(name)
                .SKU(SKU)
                .price(price)
                .saleOff(saleOff)
                .des(textFileUrl)
                .amount(amount)
                .topic(topic)
                .madeIn(madeIn)
                .VTId(VTId)
                .age(age)
                .gender(gender)
                .brand(brandEntity.get())
                .category(categoryEntity.get())
                .id(id)
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
        newProduct.setIsHidden(isHidden);
        newProduct.setImages(imageEntities);
        productRepository.save(newProduct);
        productImageRepository.saveAll(imageEntities);
        return ResponseEntity.ok("Updated product");
    }

    @PutMapping("/changeStatusBill")
    private ResponseEntity<?> changeStatusBill(@RequestBody BillDTO billDTO) {
        Optional<BillEntity> bill = billRepository.findById(billDTO.getId());
        bill.get().setStatus(billDTO.getStatus());
        billRepository.save(bill.get());
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/getAllBillByUserId")
    private ResponseEntity<?> getAllBillByUserId(@RequestParam Long userId){
        Optional<UserEntity> user=userRepository.findById(userId);
        List<BillEntity> billEntityList=billRepository.findAllByOwner(user.get());
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
                        List<String> imgs = new ArrayList<>();
                        billItemEntity.getProduct().getImages().forEach(img -> {
                            imgs.add(img.getImage());
                        });
                        productDTO.setImages(imgs);

                        return BillItemDTO.builder()
                                .amount(billItemEntity.getAmount())
                                .productDTO(productDTO)
                                .id(billItemEntity.getId())
                                .isVoted(billItemEntity.getIsVoted())
                                .price(billItemEntity.getPrice())
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
            returnValue.add(billDTO);
        });
        return ResponseEntity.ok(returnValue);
    }

    @PostMapping("/addFavourite")
    public ResponseEntity<?> addFavourite(@RequestBody FavoutireDTO favoutireDTO){
        FavouriteEntity favourite=favouriteRepository.findByUserId(favoutireDTO.getUserId());
        ProductEntity product=productRepository.findById(favoutireDTO.getProductId().get(0)).get();
        FavouriteProductEntity favouriteProductEntity=FavouriteProductEntity.builder()
                .product(product)
                .favourite(favourite)
                .build();
        favouriteProductRepository.save(favouriteProductEntity);
        product.getFavourite().add(favouriteProductEntity);
        productRepository.save(product);
        favourite.getItems().add(favouriteProductEntity);
        favouriteRepository.save(favourite);
        return ResponseEntity.ok("ok");
    }
    @PostMapping("/removeFavourite")
    public ResponseEntity<?> removeFavourite(@RequestBody FavoutireDTO favoutireDTO){
        FavouriteProductEntity favouriteProductEntity=favouriteProductRepository.findByUserIdAndProductId(favoutireDTO.getUserId(),favoutireDTO.getProductId().get(0));
       favouriteProductRepository.delete(favouriteProductEntity);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/getAllFavourite")
    public ResponseEntity<?> getAllFavourite(@RequestParam Long userId)
    {
        List<FavouriteProductEntity> favouriteProductEntities=favouriteProductRepository.findAllByUserId(userId);
        List<ProductDTO> productDTOS=new ArrayList<>();
        favouriteProductEntities.stream().forEach(item->{

            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(item.getProduct(), productDTO);
            List<String> imgs = new ArrayList<>();
            item.getProduct().getImages().forEach(img -> {
                imgs.add(img.getImage());
            });
            productDTO.setImages(imgs);
            productDTO.setCategory(CategoryDTO.builder()
                    .id(item.getProduct().getCategory().getId())
                    .name(item.getProduct().getCategory().getName())
                    .build());
            productDTOS.add(productDTO);
        });
        return ResponseEntity.ok(FavoutireDTO.builder()
                .products(productDTOS)
                .build());
    }

    @GetMapping("/checkFavourite")
    public ResponseEntity<?> checkFavourite(@RequestParam Long userId,@RequestParam Long productId){
         FavouriteProductEntity favouriteProductEntity=favouriteProductRepository.findByUserIdAndProductId(userId,productId);
        if(favouriteProductEntity!=null){
            return ResponseEntity.ok("ok");
        }else return ResponseEntity.ok("no");
    }

    private String getContentFromCloudinary(String cloudinaryUrl) {
        try {
            URL url = new URL(cloudinaryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
