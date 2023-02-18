package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для работы с базой данных напоминаий.
 */

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDate(LocalDateTime dateTime);

}
