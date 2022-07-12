package com.lee.pay.utils;


import com.lee.pay.annotaion.NotAllEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class NotALLEmptyValidator implements ConstraintValidator<NotAllEmpty, Object> {
    private String[] fields;
    private String[] fieldsGroup;

    @Override
    public void initialize(NotAllEmpty notALLEmpty) {
        this.fields = notALLEmpty.fields();
        this.fieldsGroup = notALLEmpty.fieldsGroup();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (object == null) {
            return true;
        }
        boolean f = true;
        boolean g = true;

        if (fieldsGroup.length > 1) {
            try {
                for (String fieldName : fields) {
                    Object property = getField(object, fieldName);
                    if (property != null && !"".equals(property)) {
                        break;
                    }
                    f = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if (!f) {
                try {
                    for (String fieldName : fieldsGroup) {
                        Object property = getField(object, fieldName);
                        if (property == null || "".equals(property)) {
                            g = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return g;


        }
        try {
            for (String fieldName : fields) {
                Object property = getField(object, fieldName);
                if (property != null && !"".equals(property)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    private Object getField(Object object, String fieldName) throws IllegalAccessException {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field.get(object);
            }
        }
        return null;
    }
}