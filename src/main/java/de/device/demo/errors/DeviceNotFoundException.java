package de.device.demo.errors;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(Long id) {
        super(
                String.format(
                        "%s: Device with provided id %d wasn't found",
                        Errors.DEVICE_ID_NOT_FOUND.getInternalSematic(),
                        id
                )
        );
    }
}
