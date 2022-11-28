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
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.AnswerForUser;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    //add logger
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    //injection TGBot
    @Autowired
    private TelegramBot telegramBot;

    /*method that needs to be executed after dependency injection is done to perform any initialization.
    send message about start bot
    */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
        telegramBot.execute(new SendMessage(xxx,AnswerForUser.START));
    }

    // send message about close bot
    @PreDestroy
    public void destroy() {
        telegramBot.execute(new SendMessage(xxx,AnswerForUser.END));
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
        if (textMessage.equals("/start")) {
            SendMessage sendMessage = new SendMessage(userId, AnswerForUser.HELLO);
            telegramBot.execute(sendMessage);
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.HELLO, userId);
        } else {
            telegramBot.execute(new SendMessage(userId, AnswerForUser.IN_PROGRESS));
            logger.info("Send message: <{}>, to user: {}.", AnswerForUser.IN_PROGRESS, userId);
        }
    }

}
