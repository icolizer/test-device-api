package de.device.demo.errors;

public enum Errors {
    DEVICE_ID_NOT_FOUND("E00001"),
    DEVICE_NOT_MODIFIABLE("E00101");

    private final String code;

    Errors(String code) {
        this.code = code;
    }

    public String getErrorCode() {
        return code;
    }
}
