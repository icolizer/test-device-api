package de.device.demo.validators;

import de.device.demo.models.DeviceState;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DeviceStateValidator implements ConstraintValidator<DeviceStateValid, String> {

    private boolean acceptNull = true;

    @Override
    public void initialize(DeviceStateValid constraintAnnotation) {
        this.acceptNull = constraintAnnotation.acceptNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return acceptNull;
        }

        try {
            DeviceState.valueOf(value);

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
