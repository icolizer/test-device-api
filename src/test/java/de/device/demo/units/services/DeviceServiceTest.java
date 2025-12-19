package de.device.demo.units.services;

import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import de.device.demo.services.DefaultDeviceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringJUnitConfig
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DefaultDeviceService deviceService;

    @Autowired
    public DeviceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        var deviceNameOne = "Device One";
        var deviceNameTwo = "Device Two";

        var devices = new ArrayList<Device>();
        devices.add(new Device(UUID.randomUUID(), deviceNameOne, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device(UUID.randomUUID(), deviceNameTwo, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));

        var devicePage = new PageImpl<>(devices);
        var pageable = Pageable.unpaged();

        when(deviceRepository.findAll(pageable)).thenReturn(devicePage);

        var result = deviceService.getDevices(pageable);

        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(deviceNameOne, result.getContent().get(0).getName());
        Assertions.assertEquals(deviceNameTwo, result.getContent().get(1).getName());
    }
}
