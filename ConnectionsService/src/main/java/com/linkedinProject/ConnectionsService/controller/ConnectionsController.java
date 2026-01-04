package com.linkedinProject.ConnectionsService.controller;

import com.linkedinProject.ConnectionsService.entity.Person;
import com.linkedinProject.ConnectionsService.service.ConnectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
public class ConnectionsController {

    @Autowired
    private ConnectionsService connectionsService;

    @GetMapping("/first-degree/{userId}")
    public ResponseEntity<List<Person>> getFirstDegreeConnections(@PathVariable Long userId){
        List<Person> personList = connectionsService.getFirstDegreeConnectionsOfUser(userId);
        return ResponseEntity.ok(personList);
    }
}
