package ru.boris.tinkoffbot.service;


import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Trade;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Money;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TinkoffService {
    private static final String TOKEN = "";
    InvestApi api = InvestApi.create(TOKEN);

    public String getEURCurrency() {
        List<Trade> trades = api.getMarketDataService().getLastTradesSync("BBG0013HJJ31");
        Trade trade = trades.get(trades.size() - 1);
        BigDecimal unit = BigDecimal.valueOf(trade.getPrice().getUnits());
        BigDecimal nano = BigDecimal.valueOf(trade.getPrice().getNano());
        return unit + "," + nano + "RUB";
    }

    public String getWithdrawLimits() {
        var accounts = api.getUserService().getAccountsSync();
        var mainAccount = accounts.get(2).getId();

        var withdrawLimits = api.getOperationsService().getWithdrawLimitsSync(mainAccount);
        var money = withdrawLimits.getMoney();
        for (Money moneyValue : money) {
            return "amount: " + moneyValue.getValue() + " " + moneyValue.getCurrency();
        }
        return mainAccount;
    }

}
