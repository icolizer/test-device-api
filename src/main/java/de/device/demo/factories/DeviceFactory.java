package de.device.demo.factories;

import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DeviceFactory {
    Device createDevice(String name, String brand);
    Device createDevice(UUID id, String name, String brand, DeviceState deviceState, LocalDateTime creationDate);
}
