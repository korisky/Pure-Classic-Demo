package com.own.nacos;

import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import io.grpc.stub.AbstractStub;

import java.util.List;
import java.util.Map;

public class NewInstanceEventListener extends Subscriber<InstancesChangeEvent> {

    private Map<String, List<? extends AbstractStub<?>>> grpcServiceMap;

    public NewInstanceEventListener(Map<String, List<? extends AbstractStub<?>>> grpcServiceMap) {
        this.grpcServiceMap = grpcServiceMap;
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }

    @Override
    public void onEvent(InstancesChangeEvent newInstanceEventListener) {
        if (grpcServiceMap.containsKey(newInstanceEventListener.getServiceName())) {
            // TODO update the grpc stub
        }
    }


}
