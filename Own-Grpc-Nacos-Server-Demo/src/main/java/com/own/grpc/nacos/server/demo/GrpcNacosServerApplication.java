package com.own.grpc.nacos.server.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GrpcNacosServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcNacosServerApplication.class, args);
    }
}
