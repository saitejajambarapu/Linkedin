package com.linkedinproject.consumer;

import com.linkedinproject.event.UserCreatedEvent;
import com.linkedinproject.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceConsumer {

    @Autowired
    private PersonService personService;

    @KafkaListener(topics = "user_created_topic")
    public void handlePersonCreated(UserCreatedEvent userCreatedEvent){
        log.info("creating person with id: {} and name: {}",userCreatedEvent.getUserId(),userCreatedEvent.getName());
        personService.createperson(userCreatedEvent.getUserId(),userCreatedEvent.getName());

    }
}
