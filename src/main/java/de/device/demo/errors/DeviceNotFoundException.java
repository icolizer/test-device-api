package de.device.demo.errors;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(UUID id) {
        super(
                String.format(
                        "%s: Device with provided id %s wasn't found",
                        Errors.DEVICE_ID_NOT_FOUND.getErrorCode(),
                        id
                )
        );
    }
}
