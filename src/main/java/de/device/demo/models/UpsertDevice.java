package de.device.demo.models;

import de.device.demo.entities.Device;

public record UpsertDevice(
        boolean created,
        Device device
) {
}
