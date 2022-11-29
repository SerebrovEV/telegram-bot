package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.AnswerForUser;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    @Value("${telegram.bot.admin}")
    private Long adminId;

    //add logger
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    //injection TGBot
    private final TelegramBot telegramBot;
    private final NotificationService notificationService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
    }

    private final Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /*method that needs to be executed after dependency injection is done to perform any initialization.
    send message about start bot
    */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
        telegramBot.execute(new SendMessage(adminId, AnswerForUser.START));
    }

    // send message about close bot
    @PreDestroy
    public void destroy() {
        telegramBot.execute(new SendMessage(adminId, AnswerForUser.END));
    }

    // Handling incoming messages
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            // Process updates here

            /*logger for get all information
              logger.info("Processing update: {}", update);

            Handling incoming messages in simple form*/
            try {
                logger.info("User: {}. Text message: {}.", update.message().from().id(), update.message().text());
                validationMessage(update);
            } catch (NullPointerException ignored) {
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    // check incoming message
    private void validationMessage(Update update) {

        String textMessage = update.message().text();
        User user = update.message().from();
        Long userId = user.id();
        if (textMessage == null) {
            telegramBot.execute(new SendMessage(userId, AnswerForUser.MISTAKE));
            telegramBot.execute(new SendMessage(userId, AnswerForUser.EXAMPLE));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.MISTAKE, userId);
        } else {
            processingMessage(textMessage, userId);
        }
    }

    //parsing messages after checking
    private void processingMessage(String textMessage, Long userId) {

        Matcher matcher = pattern.matcher(textMessage);
        if (textMessage.equals("/start")) {

            telegramBot.execute(new SendMessage(userId, AnswerForUser.HELLO));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.HELLO, userId);

        } else if (matcher.matches()) {

            String date = matcher.group(1);
            String eventText = matcher.group(3);

            try {
                LocalDateTime dateTime = LocalDateTime.parse(date, dateTimeFormatter);
                notificationService.addNotification(userId, eventText, dateTime);
                telegramBot.execute(new SendMessage(userId, AnswerForUser.DONE + ": " + textMessage));
                logger.info("Send message: <{}>, to user: {}.", AnswerForUser.DONE, userId);
            } catch (DateTimeParseException e) {
                telegramBot.execute(new SendMessage(userId, AnswerForUser.MISTAKE));
                telegramBot.execute(new SendMessage(userId, AnswerForUser.EXAMPLE));
                logger.info("Send message: <{}>, to user: {}.", AnswerForUser.MISTAKE, userId);
            }

        } else {
            telegramBot.execute(new SendMessage(userId, AnswerForUser.MISTAKE));
            telegramBot.execute(new SendMessage(userId, AnswerForUser.EXAMPLE));
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
