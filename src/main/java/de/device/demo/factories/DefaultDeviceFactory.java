package de.device.demo.factories;

import de.device.demo.components.DateTimeInterface;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDeviceFactory implements DeviceFactory {

    private final DateTimeInterface dateTimeInterface;

    @Autowired
    public DefaultDeviceFactory(DateTimeInterface dateTimeInterface) {
        this.dateTimeInterface = dateTimeInterface;
    }

    @Override
    public Device createDevice(String name, String brand) {
        var device = new Device();
        device.setName(name);
        device.setBrand(brand);
        device.setState(DeviceState.AVAILABLE);
        device.setCreationTime(dateTimeInterface.now());

        return device;
    }
}
