package com.linkedinproject.service;

import com.linkedinproject.auth.AuthContextHolder;
import com.linkedinproject.client.ConnectionServiceClient;
import com.linkedinproject.client.UploaderServiceClient;
import com.linkedinproject.dto.PersonDto;
import com.linkedinproject.dto.PostCreateRequestDto;
import com.linkedinproject.dto.PostDto;
import com.linkedinproject.entity.Post;
import com.linkedinproject.event.PostCreated;
import com.linkedinproject.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;

    @Autowired
    private UploaderServiceClient uploaderServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId, MultipartFile multipartFile){
        log.info("Creating a Post for user with userId : {}", userId);
        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(multipartFile);
        log.info("image url : {}", imageUrl);
        Post post = modelMapper.map(postCreateRequestDto,Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());
        post = postRepository.save(post);



        List<PersonDto> list = connectionServiceClient.getFirstDegreeConnections(AuthContextHolder.getCurrentUserId());
        for(PersonDto personDto: list){
            PostCreated postCreated = PostCreated.builder().
                    postId(post.getId())
                    .content(post.getContent())
                    .userId(personDto.getUserId())
                    .owneruserId(userId).build();
            postCreatedKafkaTemplate.send("post_created_topic",postCreated);
        }
        return modelMapper.map(post,PostDto.class);

    }
    public PostDto createPostText(PostCreateRequestDto postCreateRequestDto, Long userId){
        log.info("Creating a Post for user with userId : {}", userId);
        Post post = modelMapper.map(postCreateRequestDto,Post.class);
        post.setUserId(userId);
        post = postRepository.save(post);
        List<PersonDto> list = connectionServiceClient.getFirstDegreeConnections(AuthContextHolder.getCurrentUserId());
        for(PersonDto personDto: list){
            PostCreated postCreated = PostCreated.builder().
                    postId(post.getId())
                    .content(post.getContent())
                    .userId(personDto.getUserId())
                    .owneruserId(userId).build();
            postCreatedKafkaTemplate.send("post_created_topic",postCreated);
        }
        return modelMapper.map(post,PostDto.class);

    }

    public PostDto getPostById(Long postId) {
        List<PersonDto> list = connectionServiceClient.getFirstDegreeConnections(AuthContextHolder.getCurrentUserId());
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
