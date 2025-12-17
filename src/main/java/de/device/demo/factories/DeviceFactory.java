package de.device.demo.factories;

import de.device.demo.entities.Device;

public interface DeviceFactory {
    Device createDevice(String name, String brand);
}
