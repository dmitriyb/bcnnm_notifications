package net.bcnnm.notifications.slack.format;

import org.apache.commons.beanutils.PropertyUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SlackFormatter {
    // TODO: can we get clazz field just from Object?
    public static String format(Object obj, Class clazz) throws SlackFormatterException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(SlackIgnore.class)) {

                Object value;
                try {
                    value = PropertyUtils.getProperty(obj, field.getName());
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    throw new SlackFormatterException(e);
                }

                SlackName annotation = field.getAnnotation(SlackName.class);
                String name = (annotation == null) ? field.getName() : annotation.value();

                value = (field.isAnnotationPresent(SlackBold.class)) ? "*" + value + "*" : value;

                stringBuilder.append(String.format("%s: %s", name, value));
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

}
