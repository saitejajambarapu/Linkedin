package com.linkedinproject.consumer;

import com.linkedinproject.entity.Notification;
import com.linkedinproject.event.PostCreated;
import com.linkedinproject.event.PostLiked;
import com.linkedinproject.sesrvice.NoticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {

    @Autowired
    private NoticationService notificationService;

    @KafkaListener(topics = "post_created_topic")
    public void  handlePostCreated(PostCreated postCreated){
        log.info("handlePostCreated {}",postCreated);
        String message = String.format("Your connection with id: " + postCreated.getOwneruserId() +" has created this post: "+
                postCreated.getContent());
        Notification notification = Notification.builder()
                .message(message).userId(postCreated.getUserId())
        .build();
        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_liked_topic")
    public void  handlePostLiked(PostLiked postLiked) {
        log.info("handlePostLiked {}", postLiked);
        String message = String.format("User with id: %d liked with postId: %d",
                postLiked.getLikedByUserId(),postLiked.getPostId());

        Notification notification = Notification.builder()
                .message(message)
                .userId(postLiked.getOwenerUserId())
                .build();
        notificationService.addNotification(notification);
    }
    }
