package ru.boris.tinkoffbot.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.boris.tinkoffbot.model.Currency;

public interface CurrencyRedisRepository extends KeyValueRepository<Currency, String>  {

}
