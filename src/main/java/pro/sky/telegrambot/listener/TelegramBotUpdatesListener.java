package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.AnswerForUser;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    //add logger
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    //injection TGBot
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationService notificationService;

    /*method that needs to be executed after dependency injection is done to perform any initialization.
    send message about start bot
    */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
         telegramBot.execute(new SendMessage(527248474, AnswerForUser.START));
    }

    // send message about close bot
    @PreDestroy
    public void destroy() {
          telegramBot.execute(new SendMessage(527248474, AnswerForUser.END));
    }

    // Handling incoming messages
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            // Process updates here

            /*logger for get all information
              logger.info("Processing update: {}", update);

            Handling incoming messages in simple form*/
            logger.info("User: {}. Text message: {}.", update.message().from().id(), update.message().text());
            checkMessage(update);

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    // check text in incoming message
    private void checkMessage(Update update) {
        Message message = update.message();
        String textMessage = update.message().text();
        User user = update.message().from();
        Long userId = user.id();
        Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(textMessage);

        if (textMessage.equals("/start")) {

            telegramBot.execute(new SendMessage(userId, AnswerForUser.HELLO));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.HELLO, userId);

        } else if (matcher.matches()) {

            String date = textMessage.substring(0, 16);
            String eventText = textMessage.substring(17);
            LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            notificationService.addNotification(userId, eventText, dateTime);

            telegramBot.execute(new SendMessage(userId, AnswerForUser.DONE));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.DONE, userId);

        } else {

            telegramBot.execute(new SendMessage(userId, AnswerForUser.MISTAKE));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.MISTAKE, userId);
        }
    }

    // every minute start search for event
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendMessageFromDataBase() {
        logger.info("Method for searching event is working");
        List<Notification> notificationList = notificationService.checkingReminders();
        notificationList
                .forEach(notification -> {
                    telegramBot.execute(new SendMessage(notification.getUserId(), notification.getEvent()));
                    logger.info("Send message: <{}>, to user: {}.", notification.getEvent(), notification.getUserId());
                });
    }

}
