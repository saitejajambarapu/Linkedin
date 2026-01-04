package com.linkedinProject.ConnectionsService.service;

import com.linkedinProject.ConnectionsService.entity.Person;
import com.linkedinProject.ConnectionsService.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ConnectionsService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId){
        log.info("Getting first degree connections of user with ID: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

}
