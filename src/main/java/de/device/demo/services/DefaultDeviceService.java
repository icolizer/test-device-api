package de.device.demo.services;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DevicePatchRequest;
import de.device.demo.dtos.DevicePutRequest;
import de.device.demo.entities.Device;
import de.device.demo.errors.DeviceInUseDeleteException;
import de.device.demo.errors.DeviceInUseUpdateModificationException;
import de.device.demo.errors.DeviceNotFoundException;
import de.device.demo.errors.DeviceUpdateCreationTimeException;
import de.device.demo.factories.DeviceFactory;
import de.device.demo.models.DeviceState;
import de.device.demo.models.UpsertDevice;
import de.device.demo.repositories.DeviceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DefaultDeviceService implements DeviceService {

    private final DeviceFactory deviceFactory;
    private final DeviceRepository deviceRepository;
    private final EntityManager entityManager;

    @Autowired
    public DefaultDeviceService(
            DeviceFactory deviceFactory,
            DeviceRepository deviceRepository,
            EntityManager entityManager
    ) {
        this.deviceFactory = deviceFactory;
        this.deviceRepository = deviceRepository;
        this.entityManager = entityManager;
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
    public Device getById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    @Override
    @Transactional
    public Device update(UUID id, DevicePatchRequest devicePatchRequest) {
        var device = deviceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (
                (devicePatchRequest.name() != null || devicePatchRequest.brand() != null) &&
                        device.getState() == DeviceState.IN_USE
        ) {
            throw new DeviceInUseUpdateModificationException(id);
        }

        device.setName(devicePatchRequest.name() != null ? devicePatchRequest.name() : device.getName());
        device.setBrand(devicePatchRequest.brand() != null ? devicePatchRequest.brand() : device.getBrand());
        device.setState(devicePatchRequest.state() != null ? DeviceState.valueOf(devicePatchRequest.state()) : device.getState());

        return deviceRepository.saveAndFlush(device);
    }

    @Override
    @Transactional
    public UpsertDevice upsert(UUID id, DevicePutRequest devicePutRequest) {
        boolean created;
        Device device;

        var deviceEntity = deviceRepository.findByIdForUpdate(id);
        if (deviceEntity.isPresent()) {
            device = updateExisting(deviceEntity.get(), devicePutRequest);
            created = false;
        } else {
            device = createNew(id, devicePutRequest);
            created = true;
        }

        var saved = deviceRepository.save(device);

        return new UpsertDevice(created, saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        var device = deviceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (device.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseDeleteException(id);
        }

        deviceRepository.deleteById(id);
    }

    private Device updateExisting(Device device, DevicePutRequest putRequest) {
        if (putRequest.creationTime() != null) {
            throw new DeviceUpdateCreationTimeException(device.getId());
        }

        boolean nameOrBrandChanged = !putRequest.name().equals(device.getName()) || !putRequest.brand().equals(device.getBrand());

        if (nameOrBrandChanged && device.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseUpdateModificationException(device.getId());
        }

        device.setName(putRequest.name());
        device.setBrand(putRequest.brand());
        device.setState(DeviceState.valueOf(putRequest.state()));

        return device;
    }

    private Device createNew(UUID id, DevicePutRequest putRequest) {
        if (putRequest.creationTime() == null) {
            throw new IllegalArgumentException("A required field 'creation_time' is missing");
        }

        return deviceFactory.createDevice(
                id,
                putRequest.name(),
                putRequest.brand(),
                DeviceState.valueOf(putRequest.state()),
                putRequest.creationTime()
        );
    }
}
