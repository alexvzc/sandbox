/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.lang.reflect.Field;

/**
 * Various test utilities.
 * @author alexv
 */
public class TestUtils {
    
    /**
     * Retrieves the value of an unaccessible field of a class.
     * @param <T> the type of the field to be retrieved.
     * @param that the object instance whose field needs to be retrieved.
     * @param fieldName the field name.
     * @return the field value
     */
    public static <T> T getFieldValue(Object that, String fieldName)  {
        try {
            Field field = that.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(that);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }
}
