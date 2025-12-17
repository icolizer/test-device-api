package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.entities.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceService {
    Device create(DeviceCreateRequest req);
    Page<Device> getDevices(Pageable pageable);
}
