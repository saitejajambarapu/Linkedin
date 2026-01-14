package com.linkedinproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public NewTopic userCreatedTopic(){
        return new NewTopic("user_created_topic",3, (short) 1);
    }
}
