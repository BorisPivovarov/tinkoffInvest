package ru.boris.tinkoffbot.bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.boris.tinkoffbot.service.TinkoffService;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private static final String GET_EUR_COMMAND = "/getEURPrice";
    private static final String GET_WITHDRAW_LIMITS = "/getWithdrawLimits";

    private final String TOKEN;
    private final String USER_NAME;

//    private final Map<String, String> responsesMap;

//    private final NonExistentCommand nonExistentCommand;
    private final TinkoffService tinkoffService;

    public Bot(@Value("${bot.token}") String token, @Value("${bot.username}") String username, TinkoffService tinkoffService) {
        this.TOKEN = token;
        this.USER_NAME = username;
//        this.responsesMap = new ConcurrentHashMap<>(getResponsesMap());
        this.tinkoffService = tinkoffService;
    }

//    private Map<String, String> getResponsesMap() {
//        return Map.of(
//                "/start", "Hello! Maybe invest a little?",
//                DO_YOU_DO, "i'm doing something"
//        );
//    }


    @Override
    public String getBotUsername() {
        return USER_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            log.debug("Hi {} {} {}", message.getFrom().getFirstName(), message.getFrom().getLastName(),
                    message.getFrom().getUserName());
            SendMessage.SendMessageBuilder responseBuilder = SendMessage.builder();
            responseBuilder.chatId(String.valueOf(message.getChatId()));
            responseBuilder.replyMarkup(getKeyboard());

//            String responseText = responsesMap.get(message.getText());
            String responseText = "";
            if (message.getText().equals("/start")) {
                responseText = "Hello! I can do something";
            } else if (message.getText().equals(GET_EUR_COMMAND)) {
                responseText = tinkoffService.getEURCurrency();
            } else if (message.getText().equals(GET_WITHDRAW_LIMITS)) {
                responseText = String.valueOf((tinkoffService.getWithdrawLimits()));
            }
//            Optional.ofNullable(responseText).ifPresent(rt -> sendResponse(responseBuilder, rt));
                sendResponse(responseBuilder, responseText);
        }

    }

    private ReplyKeyboard getKeyboard() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(GET_EUR_COMMAND);
        keyboardRow.add(GET_WITHDRAW_LIMITS);
        List<KeyboardRow> keyboardRows = List.of(keyboardRow);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyboardRows);
        return replyKeyboard;
    }

    private void sendResponse(SendMessage.SendMessageBuilder responseBuilder, String responseText) {
        responseBuilder.text(responseText);
        try {
            execute(responseBuilder.build());
        } catch (TelegramApiException apiException) {
            apiException.printStackTrace();
        }
    }
}
