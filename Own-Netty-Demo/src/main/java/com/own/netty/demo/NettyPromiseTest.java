package com.own.netty.demo;

import io.netty.util.concurrent.*;

/**
 * Netty Promise 运作查看
 *
 * @author Roylic
 * 2024/1/21
 */
public class NettyPromiseTest {

    public static void main(String[] args) {


        // netty's 默认线程池（继承SingleThreadEventExecutor单线程池）
        final DefaultEventExecutor eventExecutors = new DefaultEventExecutor();

        // netty's Promise类
        final Promise promise = new DefaultPromise<>(eventExecutors);

        // 尝试添加Promise的listener
        promise.addListener(
                        (GenericFutureListener<Future<Integer>>) future -> {
                            if (future.isSuccess()) {
                                System.out.println("<<< Task Success with result: " + future.get());
                            } else {
                                System.out.println("<<< Task Failed with result: " + future.cause());
                            }
                        })
                .addListener(
                        (GenericFutureListener<Future<Integer>>) future -> System.out.println("<<< Finished haha"))
        ;

        // 任务提交到线程池
        eventExecutors.submit(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(10086);
        });

        try {
            // 主线程阻塞等待执行结果
            // 意思类似 -> 我这条线程任务需要等待这个promise的结果, 所以我这里调用.sync()
            promise.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
