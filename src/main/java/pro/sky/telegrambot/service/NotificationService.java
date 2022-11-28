package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {

    //injection repository
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // add logger
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // add new notification to database
    public void addNotification(Long userId, String event, LocalDateTime date) {
        logger.info("Add notification work");
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setEvent(event);
        notification.setDate(date);
        notificationRepository.save(notification);
    }

    // search event at the moment, every minute
    public List<Notification> checkingReminders() {
        logger.info("checkingReminders work");
        LocalDateTime dateTimeNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return List.copyOf(notificationRepository.findByDate(dateTimeNow));
    }

}
