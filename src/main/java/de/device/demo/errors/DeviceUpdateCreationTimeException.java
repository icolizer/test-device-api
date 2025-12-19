package de.device.demo.errors;

import java.util.UUID;

public class DeviceUpdateCreationTimeException extends DeviceModificationException {
    public DeviceUpdateCreationTimeException(UUID id) {
        super(
                String.format(
                        "%s: device creation date update error, device id %s",
                        Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(),
                        id
                )
        );
    }
}
