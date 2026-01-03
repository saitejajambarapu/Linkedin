package com.project.linkedin.postsService.controller;

import com.project.linkedin.postsService.dto.PostCreateRequestDto;
import com.project.linkedin.postsService.dto.PostDto;
import com.project.linkedin.postsService.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {


    @Autowired
    private PostService postService;


    @PostMapping("post")
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postRequestDto){
        PostDto postDto = postService.createPost(postRequestDto,1l);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId){
        PostDto postDto = postService.getPostById(postId);
        return new ResponseEntity<>(postDto,HttpStatus.FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getAllPostByUser(@PathVariable Long  userId){
        List<PostDto> postDtos = postService.getAllPostsByUser(userId);
        return new ResponseEntity<>(postDtos,HttpStatus.ACCEPTED);
    }

}
