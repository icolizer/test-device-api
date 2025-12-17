package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.entities.Device;

public interface DeviceService {
    Device create(DeviceCreateRequest req);
}
