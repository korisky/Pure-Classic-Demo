package com.own.nacos;

import com.alibaba.nacos.common.notify.NotifyCenter;
import io.grpc.stub.AbstractStub;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrpcStubHandler {

    public GrpcStubHandler() {
        // register listener
        NotifyCenter.registerSubscriber(new NewInstanceEventListener());
    }

}
