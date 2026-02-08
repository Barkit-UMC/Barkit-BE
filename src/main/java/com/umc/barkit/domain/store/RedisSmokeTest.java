package com.umc.barkit.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSmokeTest implements CommandLineRunner {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(String... args) {
        String key = "redis:test";
        stringRedisTemplate.opsForValue().set(key, "hello");
        String v = stringRedisTemplate.opsForValue().get(key);
        log.info("Redis smoke test value = {}", v);
    }
}

