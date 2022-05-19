package ru.boris.tinkoffInvestBot.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.boris.tinkoffInvestBot.model.Currency;

public interface CurrencyRedisRepository extends KeyValueRepository<Currency, String>  {

}
