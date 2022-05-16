package ru.boris.tinkoffbot.model;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@RedisHash(value = "currency", timeToLive = 24 * 60 * 60)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Getter
@EqualsAndHashCode
public class Currency {

    @Id
    String name;
    BigDecimal price;
}
