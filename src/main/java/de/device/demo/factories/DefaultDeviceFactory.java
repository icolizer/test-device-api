package de.device.demo.factories;

import de.device.demo.components.DateTimeInterface;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DefaultDeviceFactory implements DeviceFactory {

    private final DateTimeInterface dateTimeInterface;

    @Autowired
    public DefaultDeviceFactory(DateTimeInterface dateTimeInterface) {
        this.dateTimeInterface = dateTimeInterface;
    }

    @Override
    public Device createDevice(String name, String brand) {
        return createDevice(UUID.randomUUID(), name, brand, DeviceState.AVAILABLE, dateTimeInterface.now());
    }

    @Override
    public Device createDevice(UUID id, String name, String brand, DeviceState deviceState, LocalDateTime creationDate) {
        var device = new Device();
        device.setId(id);
        device.setName(name);
        device.setBrand(brand);
        device.setState(deviceState);
        device.setCreationTime(creationDate);

        return device;
    }
}
