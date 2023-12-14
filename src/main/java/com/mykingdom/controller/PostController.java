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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        List<PostEntity> postEntityList = postRepository.findAllByOrderByCreateAtDesc();
        return ResponseEntity.ok(postEntityList.stream().map(postEntity -> {

            String content = getContentFromCloudinary(postEntity.getContent());

            return PostDTO.builder()
                    .id(postEntity.getId())
                    .title(postEntity.getTitle())
                    .content(content)
                    .createAt(postEntity.getCreateAt())
                    .des(postEntity.getDes())
                    .thumb(postEntity.getThumb())
                    .build();
        }).collect(Collectors.toList()));
    }

    @GetMapping("/getById")
    private ResponseEntity<?> getAllPostById(@RequestParam Long id) {
        PostEntity postEntity = postRepository.findById(id).orElse(null);

        if (postEntity != null) {

            String content = getContentFromCloudinary(postEntity.getContent());

            return ResponseEntity.ok(
                    PostDTO.builder()
                            .id(postEntity.getId())
                            .title(postEntity.getTitle())
                            .content(content)
                            .createAt(postEntity.getCreateAt())
                            .des(postEntity.getDes())
                            .thumb(postEntity.getThumb())
                            .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<?> createPost(@ModelAttribute("title") String title,
                                         @ModelAttribute("content") String content,
                                         @ModelAttribute("des") String des,
                                         @RequestParam("thumb") MultipartFile thumb) throws IOException {
        Map result = cloudinary.uploader().upload(thumb.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) result.get("secure_url");


        String publicId = title.replace(" ","") + "_content.txt";
        Map uploadResult = cloudinary.uploader().upload(content.getBytes(StandardCharsets.UTF_8),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "raw"));

        String textFileUrl = (String) uploadResult.get("secure_url");
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setContent(textFileUrl);
        postEntity.setCreateAt(new Date());
        postEntity.setDes(des);
        postEntity.setThumb(imageUrl);
        postRepository.save(postEntity);
        return ResponseEntity.ok("ok");
    }

    @PutMapping
    private ResponseEntity<?> updatePost(@RequestBody PostDTO postDTO) throws IOException {
        Optional<PostEntity> postEntity = postRepository.findById(postDTO.getId());
        String publicId = postDTO.getTitle().replace(" ","") + "_content.txt";
        Map uploadResult = cloudinary.uploader().upload(postDTO.getContent().getBytes(StandardCharsets.UTF_8),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "raw"));

        String textFileUrl = (String) uploadResult.get("secure_url");
        postEntity.get().setContent(textFileUrl);
        postEntity.get().setTitle(postDTO.getTitle());
        postEntity.get().setDes(postDTO.getDes());
        postRepository.save(postEntity.get());
        return ResponseEntity.ok("ok");
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
