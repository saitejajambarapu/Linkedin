package com.linkedinproject.controller;

import com.linkedinproject.entity.Person;
import com.linkedinproject.service.ConnectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/request/{userId}")
    public ResponseEntity<Void> sendConnectionRequest(@PathVariable Long userId) {
        connectionsService.sendConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Void> acceptConnectionRequest(@PathVariable Long userId) {
        connectionsService.acceptConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/followback/{userId}")
    public ResponseEntity<Void> acceptFollowBackConnectionRequest(@PathVariable Long userId) {
        connectionsService.acceptFollowBackConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long userId) {
        connectionsService.rejectConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

}
