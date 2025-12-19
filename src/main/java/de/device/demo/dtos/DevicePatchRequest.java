package de.device.demo.dtos;

import de.device.demo.models.DeviceState;
import de.device.demo.validators.AtLeastOneField;
import de.device.demo.validators.DeviceStateValid;
import jakarta.validation.constraints.Size;

@AtLeastOneField
public record DevicePatchRequest(
        @Size(min = 1, max = 255, message = "Device name should not be longer 255 characters")
        String name,

        @Size(min = 1, max = 255, message = "Brand name should not be longer 255 characters")
        String brand,

        @DeviceStateValid(enumClass = DeviceState.class, message = "Invalid Device state type")
        String state
) {
}
