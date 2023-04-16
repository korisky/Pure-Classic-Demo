package com.own.nacos;

import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.listener.Subscriber;

public class NewInstanceEventListener extends Subscriber<InstancesChangeEvent> {

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }

    @Override
    public void onEvent(InstancesChangeEvent instancesChangeEvent) {
        System.out.println(instancesChangeEvent.getServiceName());
    }


}
