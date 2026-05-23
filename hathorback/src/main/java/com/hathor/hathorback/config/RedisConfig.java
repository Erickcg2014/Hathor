package com.hathor.hathorback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${upstash.redis.host}")
    private String host;

    @Value("${upstash.redis.port}")
    private int port;

    @Value("${upstash.redis.password}")
    private String password;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);

        return new JedisPool(config, host, port, 2000, password, true);
    }
}