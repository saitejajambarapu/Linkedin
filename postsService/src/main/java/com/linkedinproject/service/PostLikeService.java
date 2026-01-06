package com.linkedinproject.service;

import com.linkedinproject.entity.Post;
import com.linkedinproject.entity.PostLikes;
import com.linkedinproject.event.PostLiked;
import com.linkedinproject.exception.BadRequestException;
import com.linkedinproject.exception.ResourceNotFoundException;
import com.linkedinproject.repository.PostLikeRepo;
import com.linkedinproject.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {


    @Autowired
    private PostLikeRepo postLikeRepo;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaTemplate<Long, PostLiked> postCreatedKafkaTemplate;

    @Transactional
    public void likePost(Long postId) {
        Long userId = 1l;
        log.info("user with user id {} liked the post with postId {}",postId,userId);

        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post not found with post id :"+postId));

         boolean hasAlreadyLiked = postLikeRepo.existsByUserIdAndPostId(userId,postId);
         if(hasAlreadyLiked) throw new BadRequestException("Post cannot be liked again");

         PostLikes postLikes = new PostLikes();
         postLikes.setPostId(postId);
         postLikes.setUserId(userId);
         postLikeRepo.save(postLikes);

         PostLiked postLiked = PostLiked.builder().
                 postId(postId)
                 .likedByUserId(userId)
                 .owenerUserId(post.getUserId())
                 .build();
         postCreatedKafkaTemplate.send("post_liked_topic",postLiked);

         // send notification to the users

    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId = 1l;

        log.info("user with user id {} unliked the post with postId {}",postId,userId);

        postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post not found with id: "+postId));

        boolean hasAlreadyLiked = postLikeRepo.existsByUserIdAndPostId(userId,postId);
        if(!hasAlreadyLiked) throw new BadRequestException("Post cannot be disliked without liking it");

        postLikeRepo.deleteByUserIdAndPostId(userId,postId);

    }
}
