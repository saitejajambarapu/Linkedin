package com.linkedinproject.client;

import com.linkedinproject.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "connections-service", url = "${CONNECTIONS-SERVICE_URI}")
public interface ConnectionServiceClient {

    @GetMapping("/connections/core/first-degree/{userId}")
    List<PersonDto> getFirstDegreeConnections(@PathVariable Long userId);
}
