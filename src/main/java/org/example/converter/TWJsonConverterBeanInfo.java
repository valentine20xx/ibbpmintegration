package org.example.converter;

import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;
import org.example.BeanInfoUtils;

/**
 * Класс-описание {@link TWJsonConverter} для отображения в IBM Process Designer.
 */
public final class TWJsonConverterBeanInfo extends SimpleBeanInfo {
    private Class beanClass = TWJsonConverter.class;

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        try {
            MethodDescriptor convertTWObjectToJSONDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "convertTWObjectToJSON",
                    new String[]{
                            "TW Object"
                    },
                    new Class[]{
                            com.lombardisoftware.core.TWObject.class
                    });

            MethodDescriptor convertJSONToTWObjectDescription = BeanInfoUtils.getMethodDescription(beanClass,
                    "convertJSONToTWObject",
                    new String[]{
                            "JSON String"
                    },
                    new Class[]{
                            String.class
                    });


            return new MethodDescriptor[]{convertTWObjectToJSONDescription, convertJSONToTWObjectDescription};
        } catch (Exception e) {
            e.printStackTrace(System.out);

            return super.getMethodDescriptors();
        }
    }
}
