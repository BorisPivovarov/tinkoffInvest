package ru.boris.tinkoffInvestBot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.boris.tinkoffInvestBot.service.CurrencyRedisService;
import ru.boris.tinkoffInvestBot.service.TinkoffService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private static final String GET_EUR_COMMAND = "курс евро";
    private static final String GET_WITHDRAW_LIMITS = "остаток на счёте";
    private static final String CET_USD_COMMAND = "курс доллара";
    private static final String GET_DAYS_LIST = "время работы биржи";

    private final String TOKEN;
    private final String USER_NAME;

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
            Stickers s = new Stickers();
            String responseText;

            if (message.getText().equals("/start")) {
                SendSticker stick = SendSticker.builder().chatId(String.valueOf(update.getMessage()
                                .getChatId()))
                        .sticker(new InputFile(s.whatsUpCat)).build();
                responseText = "Привет, " + message.getFrom().getFirstName() +
                        "!\n Я бот, работающий с Тинькофф Инвестициями.";
                try {
                    execute(stick);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }


            } else if (message.getText().equals(GET_EUR_COMMAND)) {
                try {
                    responseText = getMOEXPriceMessage() +
                            tinkoffService.getEURCurrency().toString() + " RUB";
                } catch (IndexOutOfBoundsException ioe) {
                    ioe.printStackTrace();
                    BigDecimal eur = currencyRedisService.getCurrency("eur").getPrice();
                    log.info(getRedisMessage() + eur.toString());
                    responseText = eur + " RUB\n" + getTodayLastPriceMessage();
                }
            } else if (message.getText().equals(GET_WITHDRAW_LIMITS)) {
                responseText = String.valueOf((tinkoffService.getWithdrawLimits()));

            } else if (message.getText().equals(GET_DAYS_LIST)) {
                responseText = tinkoffService.getDaysList();
            } else if (message.getText().equals(CET_USD_COMMAND)) {
                try {
                    responseText = getMOEXPriceMessage() +
                            tinkoffService.getUSDCurrency().toString() + " RUB";
                } catch (IndexOutOfBoundsException ioe) {
                    BigDecimal usd = currencyRedisService.getCurrency("usd").getPrice();
                    log.info(getRedisMessage() + usd.toString());
                    responseText = usd + " RUB\n" + getTodayLastPriceMessage();
                }
            } else {
                responseText = "Данная команда не распознана, попробуйте что-нибудь ещё.\n" +
                        "Для начала можно использовать команду:\n /start";

            }
            sendResponse(responseBuilder, responseText);
        }
    }

    private ReplyKeyboard getKeyboard() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(GET_WITHDRAW_LIMITS);
        keyboardRow.add(GET_EUR_COMMAND);
        keyboardRow.add(CET_USD_COMMAND);
//        keyboardRow.add(GET_DAYS_LIST);
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

    private String getMOEXPriceMessage() {
        return "В данный момент курс на московской бирже:\n";
    }

    private String getRedisMessage() {
        return "Данные загружаются из Redis кэша ";
    }

    private String getTodayLastPriceMessage() {
        return "Курс актуальный на момент закрытия биржи";
    }
}
