package com.own.grpc.nacos.server.demo.service;

import com.own.grpc.nacos.server.demo.GreeterGrpc;
import com.own.grpc.nacos.server.demo.HelloReply;
import com.own.grpc.nacos.server.demo.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class GreetImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello, " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
