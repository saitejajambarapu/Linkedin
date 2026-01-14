package com.linkedinproject.service;

import com.linkedinproject.entity.Person;
import com.linkedinproject.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public void createperson(Long userId, String name){
        log.info("creating person with person name : {} and userId: {}",name,userId);
        Person person = Person.builder().
                name(name).
                userId(userId).
                build();
        personRepository.save(person);


    }


}
