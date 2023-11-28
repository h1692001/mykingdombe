package com.mykingdom.controller;

import com.mykingdom.dtos.AddToCartDTO;
import com.mykingdom.dtos.CartDTO;
import com.mykingdom.dtos.CartProductDTO;
import com.mykingdom.dtos.ProductDTO;
import com.mykingdom.entity.CartEntity;
import com.mykingdom.entity.CartProductEntity;
import com.mykingdom.entity.ProductEntity;
import com.mykingdom.entity.UserEntity;
import com.mykingdom.exception.ApiException;
import com.mykingdom.repository.CartProductRepository;
import com.mykingdom.repository.CartRepository;
import com.mykingdom.repository.ProductRepository;
import com.mykingdom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @CrossOrigin
    private ResponseEntity<?> getCartByUserId(@RequestParam Long userId){
        Optional<UserEntity> user=userRepository.findById(userId);
        CartEntity cart=cartRepository.findByUser(user.get());

        return ResponseEntity.ok(CartDTO.builder()
                        .id(cart.getId())
                        .cartProducts(cart.getCartProducts().stream().map(cartProductEntity -> {
                            CartProductDTO cartProductDTO=CartProductDTO.builder()
                                    .amount(cartProductEntity.getAmount())
                                    .productDTO(ProductDTO.builder()
                                            .id(cartProductEntity.getProduct().getId())
                                            .images(Collections.singletonList(cartProductEntity.getProduct().getImages().get(0).getImage()))
                                            .price(cartProductEntity.getProduct().getPrice())
                                            .saleOff(cartProductEntity.getProduct().getSaleOff())
                                            .name(cartProductEntity.getProduct().getName())
                                            .build())
                                    .id(cartProductEntity.getId())
                                    .build();
                            return cartProductDTO;
                        }).collect(Collectors.toList()))
                .build());
    }

    @PostMapping
    @CrossOrigin
    private ResponseEntity<?> addToCart(@RequestBody AddToCartDTO addToCartDTO){
        Optional<CartEntity> cart=cartRepository.findById(addToCartDTO.getCartId());
        cart.get().getCartProducts().forEach(cartProductEntity -> {
            if(cartProductEntity.getProduct().getId()== addToCartDTO.getProductId()){
                throw new ApiException(HttpStatus.BAD_REQUEST,"Already on cart");
            }
        });
        Optional<ProductEntity> product=productRepository.findById(addToCartDTO.getProductId());

        CartProductEntity cartProductEntity=CartProductEntity.builder()
                .product(product.get())
                .amount(addToCartDTO.getAmount())
                .cart(cart.get())
                .build();

        cart.get().getCartProducts().add(cartProductEntity);
        cartProductRepository.save(cartProductEntity);
        cartRepository.save(cart.get());

        return ResponseEntity.ok(CartDTO.builder()
                .id(cart.get().getId())
                .cartProducts(cart.get().getCartProducts().stream().map(cartProductEntity1 -> {
                    return CartProductDTO.builder()
                            .amount(cartProductEntity1.getAmount())
                            .productDTO(ProductDTO.builder()
                                    .id(cartProductEntity1.getProduct().getId())
                                    .images(Collections.singletonList(cartProductEntity1.getProduct().getImages().get(0).getImage()))
                                    .price(cartProductEntity1.getProduct().getPrice())
                                    .saleOff(cartProductEntity1.getProduct().getSaleOff())
                                    .build())
                            .id(cartProductEntity.getId())
                            .build();
                }).collect(Collectors.toList()))
                .build());
    }

    @DeleteMapping
    @CrossOrigin
    private ResponseEntity<?> deleteFromCart(@RequestParam Long id){
        Optional<CartProductEntity> cartProductEntity=cartProductRepository.findById(id);
        cartProductRepository.delete(cartProductEntity.get());
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/deleteAllCartItem")
    private ResponseEntity<?> deleteAllCartItem(@RequestParam Long id){
        Optional<CartEntity> cartEntity=cartRepository.findById(id);
        List<CartProductEntity> cartProductEntities=cartProductRepository.findAllByCart(cartEntity.get());
        cartProductRepository.deleteAll(cartProductEntities);
        return ResponseEntity.ok("Success");
    }
}
