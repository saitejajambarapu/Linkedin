package com.linkedinproject.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreated {

    private Long owneruserId;

    private Long postId;

    private Long userId;

    private String content;
}
