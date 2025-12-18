package de.device.demo.errors;

public class DeviceInUseModificationException extends RuntimeException {
    public DeviceInUseModificationException(Long id) {
        super(
                String.format(
                        "%s: name or brand fields cannot be updated due to IN_USE state of device with id %d",
                        Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(),
                        id
                )
        );
    }
}
