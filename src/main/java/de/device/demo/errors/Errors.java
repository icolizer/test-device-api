package de.device.demo.errors;

public enum Errors {
    DEVICE_ID_NOT_FOUND("E00001");

    private final String internalSematic;

    Errors(String sematic) {
        internalSematic = sematic;
    }

    public String getInternalSematic() {
        return internalSematic;
    }
}
