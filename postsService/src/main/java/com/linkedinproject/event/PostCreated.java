package com.linkedinproject.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreated {

    private Long owneruserId;

    private Long postId;

    private Long userId;

    private String content;
}
