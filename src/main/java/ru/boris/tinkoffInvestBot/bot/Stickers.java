package ru.boris.tinkoffInvestBot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class Stickers {
    InputFile SLY_BO = new InputFile("CAACAgIAAxkBAAEFiA5i9NepScC2e7fjrFWpag7cL2OzUgAC0A0AArCZIEhQNz2IYLvzvCkE");

    String catInBed = "CAACAgIAAxkBAAEFiJ1i9OKCv8l04VR2AAHYIWY-q07rtxwAAjkAA7ocQxI1x5oesD7XjikE";
    String happyCat = "CAACAgIAAxkBAAEEvmFigk7557QWN_FzOqMmQ7msKZ3CogACEQADuhxDEvfNcMeN9JFqJAQ";
    String sadCat = "CAACAgIAAxkBAAEFiJ9i9OKT0uNbc-cm6FsurmXGmxYVTAACGwADuhxDEr6lZryIiqOoKQQ";
    String sadCat2 = "CAACAgIAAxkBAAEFiKFi9OKk0sCfwQQTpCdzsBdXcZJDtgACDAEAAhZ8aAPEXuoz0922FykE";
    String whatsUpDog = "CAACAgEAAxkBAAEFiKdi9OMnVzIydZ3Y1lPtpNJb4fEc7gACjxIAApl_iAJDZMg5zV9DhCkE";
    String whatsUpCat = "CAACAgEAAxkBAAEFiKli9OMy5a_Pw8nvb0JTWpLjiNd8fgACeQ8AApl_iALhjByPTP6m3ykE";
    String wtfDog = "CAACAgEAAxkBAAEFiKti9ONSV-WDRa4USiJWdgWwM6L7_QAClw8AApl_iALr-dnrwBjv_CkE";
    String wtfDog2 = "CAACAgEAAxkBAAEFiK1i9ONeE6kSq1VS8WMzT-0Q1ZXBzQACmw8AApl_iAKofRA6_TTO7SkE";
    String wtfDog3 = "CAACAgEAAxkBAAEFiK9i9ONnwQGCux5NVfCF1pKkHjW2ggACyA8AApl_iAJNM9P_oFgjxykE";
    String screamingCat = "CAACAgEAAxkBAAEFiLFi9ON46czfyx2tPjUDIdJuipSb_wAC_RMAApl_iAIAAVCopN3JzaMpBA";
    String happyDog = "CAACAgEAAxkBAAEFiLNi9OOEJT6DFUpMQyaP6nw8C95plwAC5RAAApl_iAJ5q_80IaU2rCkE";
    String happyDog2 = "CAACAgEAAxkBAAEFiLVi9OOKQ35BJdMnNDuBGGMROeU-jgACoRAAApl_iAICc3HwBnAxQCkE";
    String happyDog3 = "CAACAgEAAxkBAAEFiLdi9OOYFUYTyNbIOHNFnxjTaddszQAC6w8AApl_iAJwiDaS3yYMOikE";



    public void sendSlyBo() {
        SendSticker sticker = new SendSticker();
        sticker.setSticker(SLY_BO);
    }
    public SendSticker getSendSticker(String chatId) {
        if ("".equals(chatId)) throw new IllegalArgumentException("ChatId cant be null");
        SendSticker sendSticker = getSendSticker();
        sendSticker.setChatId(chatId);
        return sendSticker;
    }
    public SendSticker getSendSticker() {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(SLY_BO);
        return sendSticker;
    }
}

