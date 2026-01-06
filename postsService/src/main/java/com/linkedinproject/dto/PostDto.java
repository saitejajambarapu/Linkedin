package com.linkedinproject.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PostDto {

    private Long id;


    private String content;


    private  Long userId;


    private LocalDateTime createdAt;


    private LocalDate lastModifiedAt;

}
