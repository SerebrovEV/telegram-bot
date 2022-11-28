package pro.sky.telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.NotificationRepository;

@Service
public class NotificationService{

    //injection repository
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    //add logger
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void addNotification() {
        notificationRepository.save();
    }

}
