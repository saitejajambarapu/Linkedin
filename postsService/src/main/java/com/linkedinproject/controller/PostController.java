package com.linkedinproject.controller;

import com.linkedinproject.auth.AuthContextHolder;
import com.linkedinproject.dto.PostCreateRequestDto;
import com.linkedinproject.dto.PostDto;
import com.linkedinproject.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {


    @Autowired
    private PostService postService;

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(
            @RequestPart("post") PostCreateRequestDto postRequestDto,
            @RequestPart("file") MultipartFile multipartFile
    ) {
        PostDto postDto = postService.createPost(
                postRequestDto,
                AuthContextHolder.getCurrentUserId(),
                multipartFile
        );
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId){
        Long userId = AuthContextHolder.getCurrentUserId();
        PostDto postDto = postService.getPostById(postId);
        return new ResponseEntity<>(postDto,HttpStatus.FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getAllPostByUser(@PathVariable Long  userId){
        List<PostDto> postDtos = postService.getAllPostsByUser(userId);
        return new ResponseEntity<>(postDtos,HttpStatus.ACCEPTED);
    }

}
