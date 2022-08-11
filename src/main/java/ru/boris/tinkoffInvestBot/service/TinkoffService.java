package ru.boris.tinkoffInvestBot.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Trade;
import ru.tinkoff.piapi.contract.v1.TradingDay;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;

@Service
@Slf4j
public class TinkoffService {

    private final String TOKEN;

    public TinkoffService(@Value("${tinkoff.token}") String token) {
        this.TOKEN = token;
    }

    private BigDecimal getValue(List<Trade> trades) {
        Trade trade = trades.get(trades.size() - 1);
        BigDecimal value = new BigDecimal(trade.getPrice().getUnits() + "." +
                trade.getPrice().getNano()).stripTrailingZeros();
        log.info("работа метода getValue " + value + " RUB");
        return value;
    }

    public BigDecimal getEURCurrency() throws IndexOutOfBoundsException {
        InvestApi api = InvestApi.create(TOKEN);
        List<Trade> trades = api.getMarketDataService().getLastTradesSync("BBG0013HJJ31");
        return getValue(trades);
    }

    public String getAccessLevel() {
        InvestApi api = InvestApi.create(TOKEN);
        return api.getUserService().getAccountsSync().get(2).getAccessLevel().toString();
    }

    public BigDecimal getUSDCurrency() throws IndexOutOfBoundsException {
        InvestApi api = InvestApi.create(TOKEN);
        List<Trade> trades = api.getMarketDataService().getLastTradesSync("BBG0013HGFT4");
        return getValue(trades);
    }

    public String getWithdrawLimits() {
        InvestApi api = InvestApi.create(TOKEN);
        var accounts = api.getUserService().getAccountsSync();
        var mainAccount = accounts.get(2).getId();

        var withdrawLimits = api.getOperationsService().getWithdrawLimitsSync(mainAccount);
        var money = withdrawLimits.getMoney();
        for (Money moneyValue : money) {
            BigDecimal limits = moneyValue.getValue().stripTrailingZeros();
            log.info("Остаток баланса:" + limits + " " + moneyValue.getCurrency());
            return "Остаток на брокерском счёте:\n" + limits + " " + moneyValue.getCurrency();
        }
        return mainAccount;
    }

    public String getDaysList() {
        InvestApi api = InvestApi.create(TOKEN);
        var getDaysListMessage = "";
        var tradingSchedules =
                api.getInstrumentsService().getTradingScheduleSync("moex", Instant.now(), Instant.now().plus(0, ChronoUnit.DAYS));
        for (
                TradingDay tradingDay : tradingSchedules.getDaysList()) {
            var date = timestampToString(tradingDay.getDate());
            var startDate = timestampToString(tradingDay.getStartTime());
            var endDate = timestampToString(tradingDay.getEndTime());
            if (tradingDay.getIsTradingDay()) {
                log.info("расписание торгов для площадки MOEX. Дата: {},  открытие: {}, закрытие: {}", date, startDate, endDate);
                getDaysListMessage = "Расписание торгов для площадки MOEX на сегодня\n" + "открытие: "
                        + startDate.replaceAll("[T,Z]", " ") + "\nзакрытие: " + endDate.replaceAll("[T,Z]", " ");
            } else {
                log.info("расписание торгов для площадки MOEX. Дата: {}. Выходной день", date);
                getDaysListMessage = "Расписание торгов для площадки MOEX:\n Дата: " + date.replaceAll("[T,Z]", " ") + " выходной день";
            }
        }
        return getDaysListMessage;
    }
}
