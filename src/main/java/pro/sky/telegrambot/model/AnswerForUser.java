package pro.sky.telegrambot.model;

/**
 * Enum со стандартными ответами бота.
 */
public enum AnswerForUser {


    HELLO("Hello! My name is HelperBot for busy people. I can remind you of important things."+
                  " You must send me the date, time and event:"),

    MISTAKE("Uncorrected format for day, time or event. You must write: "),

    EXAMPLE("01.01.2001 12:34 Мероприятие"),

    START("TGBot start work"),

    END("TGBot end work"),

    DONE("Event add to calendar");

    private String answer;

    AnswerForUser(String answer) {
        this.answer = answer;
    }
    @Override
    public String toString() {
        return answer;
    }

}
