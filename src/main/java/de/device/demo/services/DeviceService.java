package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceUpdateRequest;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceService {
    Device create(DeviceCreateRequest req);
    Page<Device> getDevices(Pageable pageable);
    Device getById(Long id);
    Device update(Long id, DeviceUpdateRequest deviceCreateRequest);
    Page<Device> getDevicesByBrand(String brand, Pageable pageable);
    Page<Device> getDevicesByState(DeviceState state, Pageable pageable);
    void delete(Long id);
}
