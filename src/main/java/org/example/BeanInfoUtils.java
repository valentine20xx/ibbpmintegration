package org.example;

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.lang.reflect.Method;

public final class BeanInfoUtils {
    public static MethodDescriptor getMethodDescription(Class beanClass,
                                                        String methodName,
                                                        String parameters[],
                                                        Class classes[]) throws NoSuchMethodException {
        MethodDescriptor methodDescriptor = null;
        Method method = beanClass.getMethod(methodName, classes);

        if (method != null) {
            ParameterDescriptor paramDescriptors[] = new ParameterDescriptor[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                ParameterDescriptor param = new ParameterDescriptor();
                param.setShortDescription(parameters[i]);
                param.setDisplayName(parameters[i]);
                paramDescriptors[i] = param;
            }
            methodDescriptor = new MethodDescriptor(method, paramDescriptors);
        }

        return methodDescriptor;
    }
}
