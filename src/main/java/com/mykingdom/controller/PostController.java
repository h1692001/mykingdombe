package com.mykingdom.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mykingdom.dtos.PostDTO;
import com.mykingdom.entity.PostEntity;
import com.mykingdom.repository.PostRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("post")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    private ResponseEntity<?> getAllPost() {
        List<PostEntity> postEntityList = postRepository.findAll();
        return ResponseEntity.ok(postEntityList.stream().map(postEntity -> {
            return PostDTO.builder()
                    .id(postEntity.getId())
                    .title(postEntity.getTitle())
                    .content(postEntity.getContent())
                    .createAt(postEntity.getCreateAt())
                    .des(postEntity.getDes())
                    .thumb(postEntity.getThumb())
                    .build();
        }).collect(Collectors.toList()));
    }

    @GetMapping("/getById")
    private ResponseEntity<?> getAllPostById(@RequestParam Long id) {
        PostEntity postEntity = postRepository.findById(id).get();
        return ResponseEntity.ok(
                PostDTO.builder()
                        .id(postEntity.getId())
                        .title(postEntity.getTitle())
                        .content(postEntity.getContent())
                        .createAt(postEntity.getCreateAt())
                        .des(postEntity.getDes())
                        .thumb(postEntity.getThumb())
                        .build());
    }

    @PostMapping
    private ResponseEntity<?> createPost(@ModelAttribute("title") String title,
                                         @ModelAttribute("content") String content,
                                         @ModelAttribute("des") String des,
                                         @RequestParam("thumb") MultipartFile thumb) throws IOException {
        Map result = cloudinary.uploader().upload(thumb.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setContent(content);
        postEntity.setCreateAt(new Date());
        postEntity.setDes(des);
        postEntity.setThumb(imageUrl);
        postRepository.save(postEntity);
        return ResponseEntity.ok("ok");
    }

    @PutMapping
    private ResponseEntity<?> updatePost(@RequestBody PostDTO postDTO) {
        Optional<PostEntity> postEntity = postRepository.findById(postDTO.getId());
        postEntity.get().setContent(postDTO.getContent());
        postEntity.get().setTitle(postDTO.getTitle());
        postEntity.get().setDes(postDTO.getDes());
        postRepository.save(postEntity.get());
        return ResponseEntity.ok("ok");
    }
}
