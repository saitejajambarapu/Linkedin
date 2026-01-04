package com.project.linkedin.postsService.service;

import com.project.linkedin.postsService.client.ConnectionServiceClient;
import com.project.linkedin.postsService.dto.PersonDto;
import com.project.linkedin.postsService.dto.PostCreateRequestDto;
import com.project.linkedin.postsService.dto.PostDto;
import com.project.linkedin.postsService.entity.Post;
import com.project.linkedin.postsService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ConnectionServiceClient connectionServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId){
        log.info("Creating a Post for user with userId : {}", userId);
        Post post = modelMapper.map(postCreateRequestDto,Post.class);
        post.setUserId(userId);
        post = postRepository.save(post);
        return modelMapper.map(post,PostDto.class);

    }

    public PostDto getPostById(Long postId) {
        List<PersonDto> list = connectionServiceClient.getFirstDegreeConnections(2714l);
        log.info("Getting with post id: {}", postId);
        Optional<Post> post = postRepository.findById(postId); 
        return modelMapper.map(post.get(), PostDto.class);

    }

    public List<PostDto> getAllPostsByUser(Long userId) {
        log.info("Getting All posts of user with id: {}",userId);
        List<Post> posts = postRepository.findByUserId(userId);
        List<PostDto> postDtos = posts.stream().map((post) ->
            modelMapper.map(post,PostDto.class)).collect(Collectors.toList());
        return  postDtos;
    }
}
