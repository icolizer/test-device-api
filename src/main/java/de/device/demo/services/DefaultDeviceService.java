package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.entities.Device;
import de.device.demo.factories.DeviceFactory;
import de.device.demo.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultDeviceService implements DeviceService {

    private DeviceFactory deviceFactory;
    private DeviceRepository deviceRepository;

    @Autowired
    public DefaultDeviceService(DeviceFactory deviceFactory, DeviceRepository deviceRepository) {
        this.deviceFactory = deviceFactory;
        this.deviceRepository = deviceRepository;
    }


    @Override
    @Transactional
    public Device create(DeviceCreateRequest deviceCreateRequest) {
        var device = deviceFactory.createDevice(deviceCreateRequest.name(), deviceCreateRequest.brand());
        return deviceRepository.save(device);
    }
}
