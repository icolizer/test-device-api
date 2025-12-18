package de.device.demo.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        for (Field field : value.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object fieldValue = field.get(value);
                if (fieldValue != null) {
                    return true;
                }
            } catch (IllegalAccessException ignored) {
            }
        }

        return false;
    }
}
