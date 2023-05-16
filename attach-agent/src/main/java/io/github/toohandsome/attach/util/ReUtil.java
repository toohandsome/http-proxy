package io.github.toohandsome.attach.util;

import java.lang.reflect.Field;

/**
 * @author toohandsome
 */
public class ReUtil {

    public static Field getField(Class clazz, String fieldName) {
        Class<?> current = clazz;
        Field field = null;
        try {
            field = current.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
        }
        while (current.getSuperclass() != null) {
            current = current.getSuperclass();
            try {
                field = current.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
            }
        }
        if (field == null) {
            System.out.println(fieldName + " not in " + clazz.getName());
        }
        return field;
    }

}
