package com.mykingdom.controller;

import com.mykingdom.dtos.BillDTO;
import com.mykingdom.dtos.BillItemDTO;
import com.mykingdom.dtos.ProductDTO;
import com.mykingdom.entity.*;
import com.mykingdom.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("bill")
public class BillController {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BillItemRepository billItemRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CartProductRepository cartProductRepository;

    @GetMapping("/getAllBill")
    private ResponseEntity<List<BillDTO>> getAllBill() {
        List<BillEntity> billEntityList = billRepository.findAll();
        List<BillDTO> returnValue = new ArrayList<>();
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

    @GetMapping("/getBillById")
    private ResponseEntity<BillDTO> getBillById(@RequestParam Long id) {
        BillEntity billEntity = billRepository.findById(id).get();
        BillDTO billDTO = BillDTO.builder()
                .id(billEntity.getId())
                .address(billEntity.getAddress().getAddress())
                .phone(billEntity.getAddress().getPhone())
                .name(billEntity.getAddress().getName())
                .status(billEntity.getStatus())
                .paymentCode(billEntity.getPaymentCode())
                .createdAt(billEntity.getCreatedAt())
                .paymentMethod(billEntity.getPaymentMethod())
                .billItemDTOS(billEntity.getBillItems().stream().map(billItemEntity -> {
                    ProductDTO productDTO = new ProductDTO();
                    BeanUtils.copyProperties(billItemEntity.getProduct(), productDTO);
                    return BillItemDTO.builder()
                            .amount(billItemEntity.getAmount())
                            .productDTO(productDTO)
                            .price(billItemEntity.getPrice())
                            .build();
                }).collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(billDTO);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody BillDTO billDTO) {
        BillEntity bill = new BillEntity();
        BeanUtils.copyProperties(billDTO, bill);
        AddressEntity address = AddressEntity.builder()
                .address(billDTO.getAddress())
                .phone(billDTO.getPhone())
                .name(billDTO.getName())
                .build();
        addressRepository.save(address);
        Optional<UserEntity> user = userRepository.findById(billDTO.getUserId());
        bill.setOwner(user.get());
        bill.setAddress(address);
        bill.setPaymentCode(billDTO.getPaymentCode());
        List<BillItemEntity> billItemEntities = new ArrayList<>();
        billDTO.getBillItemDTOS().forEach(billItemDTO -> {
            Optional<ProductEntity> product = productRepository.findById(billItemDTO.getProductDTO().getId());
            product.get().setAmount(product.get().getAmount() - billItemDTO.getAmount());
            BillItemEntity billItemEntity = BillItemEntity.builder()
                    .product(product.get())
                    .bill(bill)
                    .isVoted(0)
                    .amount(billItemDTO.getAmount())
                    .price((product.get().getPrice()-product.get().getSaleOff()*product.get().getPrice()/100)* billItemDTO.getAmount())
                    .build();
            productRepository.save(product.get());
            billItemEntities.add(billItemEntity);
        });
        bill.setBillItems(billItemEntities);
        Optional<CartEntity> cart = cartRepository.findById(billDTO.getCartId());
        cartProductRepository.deleteAllByCart(cart.get());
        billRepository.save(bill);
        billItemRepository.saveAll(billItemEntities);
        return ResponseEntity.ok("Success");
    }

}
