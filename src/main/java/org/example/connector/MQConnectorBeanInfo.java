package org.example.connector;


import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;
import org.example.BeanInfoUtils;

/**
 * Класс-описание {@link MQConnector} для отображения в IBM Process Designer.
 */
public final class MQConnectorBeanInfo extends SimpleBeanInfo {
    private Class beanClass = MQConnector.class;

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        try {

            MethodDescriptor callMQSyncDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "callMQSync",
                    new String[]{
                            "Host name (String)",
                            "Port (Integer)",
                            "Queue manager (String)",
                            "Channel (String)",
                            "Send queue (String)",
                            "Receive queue (String)",
                            "Message content (String)",
                            "Timeout (sec) (Integer)"
                    },
                    new Class[]{
                            String.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            Integer.class});

            MethodDescriptor sendMQMessageDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "sendMQMessage",
                    new String[]{
                            "Host name (String)",
                            "Port (Integer)",
                            "Queue manager (String)",
                            "Channel (String)",
                            "Send queue (String)",
                            "Receive queue (String)",
                            "Message content (String)"
                    },
                    new Class[]{
                            String.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class
                    });

            MethodDescriptor waitForMQMessageDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "waitForMQMessage",
                    new String[]{
                            "Host name (String)",
                            "Port (Integer)",
                            "Queue manager (String)",
                            "Channel (String)",
                            "Receive queue (String)",
                            "Correlation Key (String)",
                            "Timeout (sec) (Integer)"
                    },
                    new Class[]{
                            String.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            Integer.class
                    });

            return new MethodDescriptor[]{callMQSyncDescription, sendMQMessageDescription, waitForMQMessageDescription};
        } catch (Exception e) {
            e.printStackTrace(System.out);

            return super.getMethodDescriptors();
        }
    }
}
