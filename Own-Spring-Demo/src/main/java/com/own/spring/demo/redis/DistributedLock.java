package com.own.spring.demo.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Simplest Redisson Distributed Lock Demo
 *
 * @author Roylic
 * 2023/8/18
 */
@Component
public class DistributedLock {

    @Autowired
    public RedissonClient redissonClient;


    @Async
    @Scheduled(fixedDelay = 100)
    public void lockTrigger() {

        RLock lock = redissonClient.getLock("ABC");
        try {
            // try wait for getting lock in 1s, after 10s still holding, release it
            boolean lockStatus = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (lockStatus) {
                // do the logic
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

}
