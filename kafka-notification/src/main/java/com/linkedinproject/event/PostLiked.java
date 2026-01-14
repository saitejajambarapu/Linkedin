package com.linkedinproject.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLiked {

    private Long postId;

    private Long likedByUserId;

    private Long owenerUserId;


}
