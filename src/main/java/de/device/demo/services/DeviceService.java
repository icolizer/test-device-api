package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DevicePatchRequest;
import de.device.demo.dtos.DevicePutRequest;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import de.device.demo.models.UpsertDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeviceService {
    // Mod
    Device create(DeviceCreateRequest req);
    Device update(UUID id, DevicePatchRequest deviceCreateRequest);
    UpsertDevice upsert(UUID id, DevicePutRequest devicePutRequest);
    void delete(UUID id);

    // Fetch
    Page<Device> getDevices(Pageable pageable);
    Device getById(UUID id);
    Page<Device> getDevicesByBrand(String brand, Pageable pageable);
    Page<Device> getDevicesByState(DeviceState state, Pageable pageable);
}
