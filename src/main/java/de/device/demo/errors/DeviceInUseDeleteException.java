package de.device.demo.errors;

public class DeviceInUseDeleteException extends RuntimeException {
    public DeviceInUseDeleteException(Long id) {
        super(
                String.format(
                        "%s: device cannot be deleted due to IN_USE state, device id %d",
                        Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(),
                        id
                )
        );
    }
}
