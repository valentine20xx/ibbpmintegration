package org.example.connector;


import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;
import org.example.BeanInfoUtils;

/**
 * Класс-описание {@link JMSConnector} для отображения в IBM Process Designer.
 */
public final class JMSConnectorBeanInfo extends SimpleBeanInfo {
    private Class beanClass = JMSConnector.class;

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        try {
            MethodDescriptor callJMSSyncDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "callJMSSync",
                    new String[]{
                            "Connection factory (String)",
                            "Send queue (String)",
                            "Receive queue (String)",
                            "Message content (String)",
                            "Timeout (sec) (Integer)"
                    },
                    new Class[]{
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            Integer.class});

            MethodDescriptor sendJMSMessageDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "sendJMSMessage",
                    new String[]{
                            "Connection factory (String)",
                            "Send queue (String)",
                            "Receive queue (String)",
                            "Message content (String)"
                    },
                    new Class[]{
                            String.class,
                            String.class,
                            String.class,
                            String.class});

            MethodDescriptor waitForJMSMessageDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "waitForJMSMessage",
                    new String[]{
                            "Connection factory (String)",
                            "Receive queue (String)",
                            "Correlation Key (String)",
                            "Timeout (sec) (Integer)"
                    },
                    new Class[]{
                            String.class,
                            String.class,
                            String.class,
                            Integer.class});

            return new MethodDescriptor[]{callJMSSyncDescription, sendJMSMessageDescription, waitForJMSMessageDescription};
        } catch (Exception e) {
            e.printStackTrace(System.out);

            return super.getMethodDescriptors();
        }
    }
}
