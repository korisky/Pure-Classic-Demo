package com.own.nacos;

import com.alibaba.nacos.common.notify.NotifyCenter;
import io.grpc.stub.AbstractStub;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrpcStubHandler {

    // <serviceName, List<stub>>
    private Map<String, List<? extends AbstractStub<?>>> grpcServiceMap;

    public GrpcStubHandler() {
        grpcServiceMap = new HashMap<>();
        // register listener
        NotifyCenter.registerSubscriber(new NewInstanceEventListener(grpcServiceMap));
    }

}
