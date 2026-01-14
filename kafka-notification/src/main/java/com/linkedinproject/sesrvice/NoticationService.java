package com.linkedinproject.sesrvice;

import com.linkedinproject.entity.Notification;
import com.linkedinproject.repository.NotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoticationService {

    @Autowired
    private NotificationRepo notificationRepo;

    public void addNotification(Notification notification){
        log.info("adding notification to DB: content: {}", notification.getMessage());
        notification = notificationRepo.save(notification);
        //sendmail to user
        // firebase notification sender for mobile notification (FCM)

    }

}
