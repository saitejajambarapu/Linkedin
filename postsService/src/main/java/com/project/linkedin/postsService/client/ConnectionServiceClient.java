package com.project.linkedin.postsService.client;

import com.project.linkedin.postsService.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "connections-service")
public interface ConnectionServiceClient {

    @GetMapping("/connections/core/first-degree/{userId}")
    List<PersonDto> getFirstDegreeConnections(@PathVariable Long userId);
}
