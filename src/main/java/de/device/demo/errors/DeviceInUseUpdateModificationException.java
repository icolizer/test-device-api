package de.device.demo.errors;

import java.util.UUID;

public class DeviceInUseUpdateModificationException extends DeviceModificationException {
    public DeviceInUseUpdateModificationException(UUID id) {
        super(
                String.format(
                        "%s: name or brand fields cannot be updated due to IN_USE state of device with id %s",
                        Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(),
                        id
                )
        );
    }
}
