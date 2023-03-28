package com.own.spring.demo.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Roylic
 * 2023/3/28
 */
@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void postConstruct() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloom");
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(99999, 0.0001);
        }
    }


}
