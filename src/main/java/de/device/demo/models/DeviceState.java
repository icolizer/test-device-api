package de.device.demo.models;

public enum DeviceState {
    AVAILABLE,
    IN_USE,
    INACTIVE,
    ;

    private static final String valuesDescription;

    static {
        var builder = new StringBuilder();
        builder.append("[ ");

        for (DeviceState val : values()) {
            builder.append(val.name());
            builder.append(" ");
        }

        builder.append("]");
        valuesDescription = builder.toString();
    }

    public static String getValuesDescription() {
        return valuesDescription;
    }
}
