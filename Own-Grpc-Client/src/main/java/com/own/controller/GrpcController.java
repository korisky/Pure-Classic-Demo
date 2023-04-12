package com.own.controller;

import com.kdemo.springcloud.protos.EmptyRequest;
import com.kdemo.springcloud.protos.RemoteRpcServiceGrpc;
import com.kdemo.springcloud.protos.RpcEmployees;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在SpringBoot中endpoint调用nacos中的grpc服务
 *
 * @author Roylic
 * 2022/5/13
 */
@RestController
@RequestMapping("/grpc")
public class GrpcController {

    @GrpcClient("grpc-server")
    private RemoteRpcServiceGrpc.RemoteRpcServiceBlockingStub stub;

    @GetMapping("/employee")
    public String getEmployees() {
        RpcEmployees employees = stub.getEmployees(EmptyRequest.newBuilder().build());
        return employees.toString();
    }

}
