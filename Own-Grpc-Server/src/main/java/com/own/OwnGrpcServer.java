package com.own;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OwnGrpcServer {
    public static void main(String[] args) {
        SpringApplication.run(OwnGrpcServer.class, args);
    }
}
