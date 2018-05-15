package net.bcnnm.notifications.slack.format;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SlackFormatter {

    public static final String NET_BCNNM_NOTIFICATIONS = "net.bcnnm.notifications";

    public static String format(Object obj) throws SlackFormatterException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(SlackIgnore.class)) {

                String valueString;

                try {
                    Object value;
                    value = PropertyUtils.getProperty(obj, field.getName());
                    valueString = value.getClass().isAnnotationPresent(SlackFormatted.class) ?
                            "{" + SlackFormatter.format(value) + "}":
                            ((field.isAnnotationPresent(SlackBold.class)) ? "*" + value + "*" : value.toString());

                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    throw new SlackFormatterException(e);
                }

                SlackName annotation = field.getAnnotation(SlackName.class);
                String name = (annotation == null) ? field.getName() : annotation.value();

                stringBuilder.append(String.format("%s: %s", name, valueString));
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

}
