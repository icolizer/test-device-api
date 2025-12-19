package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceUpdateRequest;
import de.device.demo.entities.Device;
import de.device.demo.errors.DeviceInUseDeleteException;
import de.device.demo.errors.DeviceInUseUpdateModificationException;
import de.device.demo.errors.DeviceNotFoundException;
import de.device.demo.factories.DeviceFactory;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultDeviceService implements DeviceService {

    private final DeviceFactory deviceFactory;
    private final DeviceRepository deviceRepository;

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

    @Override
    @Transactional(readOnly = true)
    public Page<Device> getDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Device> getDevicesByBrand(String brand, Pageable pageable) {
        return deviceRepository.findByBrand(brand, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Device> getDevicesByState(DeviceState state, Pageable pageable) {
        return deviceRepository.findByState(state, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Device getById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    @Override
    @Transactional
    public Device update(Long id, DeviceUpdateRequest deviceUpdateRequest) {
        var device = deviceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (
                (deviceUpdateRequest.name() != null || deviceUpdateRequest.brand() != null) &&
                        device.getState() == DeviceState.IN_USE
        ) {
            throw new DeviceInUseUpdateModificationException(id);
        }

        device.setName(deviceUpdateRequest.name() != null ? deviceUpdateRequest.name() : device.getName());
        device.setBrand(deviceUpdateRequest.brand() != null ? deviceUpdateRequest.brand() : device.getBrand());
        device.setState(deviceUpdateRequest.state() != null ? DeviceState.valueOf(deviceUpdateRequest.state()) : device.getState());

        return deviceRepository.saveAndFlush(device);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var device = deviceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (device.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseDeleteException(id);
        }

        deviceRepository.deleteById(id);
    }
}
