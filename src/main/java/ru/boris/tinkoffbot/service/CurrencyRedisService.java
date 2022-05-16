package ru.boris.tinkoffbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.boris.tinkoffbot.model.Currency;
import ru.boris.tinkoffbot.repository.CurrencyRedisRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyRedisService {

    private final TinkoffService tinkoffService;
    private final CurrencyRedisRepository currencyRedisRepository;

    @Scheduled(cron = "0 50 18 * * ?")
    public void saveAllCurrencies() {
        log.info("Start fill redis");
        List<Currency> currencies = List.of(
                Currency.builder().name("usd").price(tinkoffService.getUSDCurrency()).build(),
                Currency.builder().name("eur").price(tinkoffService.getEURCurrency()).build()
        );
        currencyRedisRepository.saveAll(currencies);
    }

    public Currency getCurrency(String name) {

        return currencyRedisRepository.findById(name).
                orElseThrow(() -> new RuntimeException("Currency not found in redis"));
        //создать своё исключение
    }
}
