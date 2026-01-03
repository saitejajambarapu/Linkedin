package com.project.linkedin.postsService.service;

import com.project.linkedin.postsService.entity.Post;
import com.project.linkedin.postsService.entity.PostLikes;
import com.project.linkedin.postsService.exception.BadRequestException;
import com.project.linkedin.postsService.exception.ResourceNotFoundException;
import com.project.linkedin.postsService.repository.PostLikeRepo;
import com.project.linkedin.postsService.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {


    @Autowired
    private PostLikeRepo postLikeRepo;

    @Autowired
    private PostRepository postRepository;

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
