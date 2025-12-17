package de.device.demo.integration;

import de.device.demo.dtos.DeviceResponse;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import de.device.demo.utils.PageableModelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceFetchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void success() throws Exception {
        var deviceNameOne = "Device One";
        var deviceNameTwo = "Device Two";

        var devices = new ArrayList<Device>();
        devices.add(new Device(deviceNameOne, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device(deviceNameTwo, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device("Dummy 1", "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device("Dummy 2", "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));

        deviceRepository.saveAll(devices);

        var requestResult = mockMvc.perform(get("/api/devices")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var devicesList = objectMapper.readValue(
                responseBody,
                new TypeReference<PageableModelTest<DeviceResponse>>() {}
        );

        Assertions.assertEquals(3, devicesList.size());
        Assertions.assertEquals(2, devicesList.totalPages());
        Assertions.assertEquals(4, devicesList.totalElements());
        Assertions.assertEquals(deviceNameOne, devicesList.content().get(0).getName());
        Assertions.assertEquals(deviceNameTwo, devicesList.content().get(1).getName());
    }
}
