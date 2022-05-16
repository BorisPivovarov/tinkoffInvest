package ru.boris.tinkoffbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.boris.tinkoffbot.service.CurrencyRedisService;
import ru.boris.tinkoffbot.service.TinkoffService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    //    private static final String GET_DAYS = "/getDaysList";
    private static final String GET_EUR_COMMAND = "курс евро";
    private static final String GET_WITHDRAW_LIMITS = "остаток на счёте";
    private static final String CET_USD_COMMAND = "курс доллара";

    private final String TOKEN;
    private final String USER_NAME;
    private final String sticker1 = "CAACAgIAAxkBAAEEvmFigk7557QWN_FzOqMmQ7msKZ3CogACEQADuhxDEvfNcMeN9JFqJAQ";
    private final String a = "";

    private final CurrencyRedisService currencyRedisService;
    private final TinkoffService tinkoffService;

    public Bot(@Value("${bot.token}") String token, @Value("${bot.username}") String username,
               CurrencyRedisService currencyRedisService, TinkoffService tinkoffService) {
        this.TOKEN = token;
        this.USER_NAME = username;
        this.currencyRedisService = currencyRedisService;
        this.tinkoffService = tinkoffService;
    }

    @Override
    public String getBotUsername() {
        return USER_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) {
        return super.executeAsync(sendSticker);
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

            String responseText;
            if (message.getText().equals("/start")) {
                responseText = "Привет, " + message.getFrom().getFirstName() +
                        "!\n Я бот, работающий с Tinkoff Invest API.";
            } else if (message.getText().equals(GET_EUR_COMMAND)) {
                try {
                    responseText = "В данный момент курс на московской бирже:\n"  +
                            tinkoffService.getEURCurrency().toString() + "RUB";
                } catch (IndexOutOfBoundsException ioe) {
                    ioe.printStackTrace();
                    BigDecimal eur = currencyRedisService.getCurrency("eur").getPrice();
                    log.info("Данные загружаются из Redis " + eur.toString());
                    responseText = eur + " RUB\n" + "Курс актуальный на момент закрытия биржи";
                }
            } else if (message.getText().equals(GET_WITHDRAW_LIMITS)) {
                responseText = String.valueOf((tinkoffService.getWithdrawLimits()));

            } else if (message.getText().equals(CET_USD_COMMAND)) {
                try {
                    responseText = "В данный момент курс на московской бирже:\n" +
                            tinkoffService.getUSDCurrency().toString() + "RUB";
                } catch (IndexOutOfBoundsException ioe) {
                    BigDecimal usd = currencyRedisService.getCurrency("usd").getPrice();
                    log.info("Данные загружаются из Redis " + usd.toString());
                    responseText = usd + " RUB\n" + "Курс актуальный на момент закрытия биржи";
                }
            } else {
                responseText = "Данная команда не распознана, попробуйте что-нибудь ещё.";

            }
            sendResponse(responseBuilder, responseText);
        }
    }
    private InlineKeyboardButton getKeyboardInline() {

        return null;
    }
    private ReplyKeyboard getKeyboard() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(GET_WITHDRAW_LIMITS);
        keyboardRow.add(GET_EUR_COMMAND);
        keyboardRow.add(CET_USD_COMMAND);
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
