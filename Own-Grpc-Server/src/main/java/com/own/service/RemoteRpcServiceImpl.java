package com.own.service;

import com.kdemo.springcloud.protos.EmptyRequest;
import com.kdemo.springcloud.protos.RemoteRpcServiceGrpc;
import com.kdemo.springcloud.protos.RpcEmployee;
import com.kdemo.springcloud.protos.RpcEmployees;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author Roylic
 * 2023/4/12
 */
@GrpcService
public class RemoteRpcServiceImpl extends RemoteRpcServiceGrpc.RemoteRpcServiceImplBase {

    @Override
    public void getEmployees(EmptyRequest request, StreamObserver<RpcEmployees> responseObserver) {
        responseObserver.onNext(
                RpcEmployees.newBuilder()
                        .addEmployee(
                                RpcEmployee.newBuilder()
                                        .setId(1)
                                        .setName("Emp1")
                                        .setSalary(4000.55F).build())
                        .addEmployee(
                                RpcEmployee.newBuilder()
                                        .setId(2)
                                        .setName("Emp2")
                                        .setSalary(3403.01F).build())
                        .build());
        responseObserver.onCompleted();
    }
}
