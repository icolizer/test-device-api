package de.device.demo.integration;

import de.device.demo.dtos.DeviceResponse;
import de.device.demo.entities.Device;
import de.device.demo.errors.Errors;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import de.device.demo.utils.PageableModelTest;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
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
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @AfterEach
    void tearDown() {
        deviceRepository.deleteAll();
    }

    @Test
    void fetchAllDevices_returnPaginationResult() throws Exception {
        var deviceNameOne = "Device One";
        var deviceNameTwo = "Device Two";

        var devices = new ArrayList<Device>();
        devices.add(new Device(UUID.randomUUID(), deviceNameOne, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device(UUID.randomUUID(), deviceNameTwo, "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device(UUID.randomUUID(), "Dummy 1", "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));
        devices.add(new Device(UUID.randomUUID(), "Dummy 2", "Brand", DeviceState.AVAILABLE, LocalDateTime.now()));

        deviceRepository.saveAll(devices);

        var requestResult = mockMvc.perform(get("/api/devices?size=3")
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

    @Test
    public void fetchDeviceById_resultFound() throws Exception {
        var deviceWithName = "TEST: Known ID";
        var knownDevice = new Device(UUID.randomUUID(), deviceWithName, "Brand", DeviceState.AVAILABLE, LocalDateTime.now());
        var persistedDevice = deviceRepository.save(knownDevice);

        mockMvc.perform(get("/api/devices/" + persistedDevice.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(persistedDevice.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(persistedDevice.getName())));
    }

    @Test
    public void fetchDeviceByWrongUUID_resultBadRequest() throws Exception {
        mockMvc.perform(get("/api/devices/" + Long.MAX_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Is.is("Invalid UUID format")));
    }

    @Test
    public void fetchDeviceById_resultNotFound() throws Exception {
        mockMvc.perform(get("/api/devices/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", startsWith(Errors.DEVICE_ID_NOT_FOUND.getErrorCode())));
    }

    @Test
    public void fetchDeviceByBrand_isOk() throws Exception {
        var brand = "sayHello";
        var deviceWithName1 = "testName1";
        var deviceWithName2 = "testName2";
        var knownDevice1 = new Device(UUID.randomUUID(), deviceWithName1, brand, DeviceState.AVAILABLE, LocalDateTime.now());
        var knownDevice2 = new Device(UUID.randomUUID(), deviceWithName2, brand, DeviceState.AVAILABLE, LocalDateTime.now());
        var impurity = new Device(UUID.randomUUID(), deviceWithName2, "any other brand", DeviceState.AVAILABLE, LocalDateTime.now());

        deviceRepository.save(knownDevice1);
        deviceRepository.save(knownDevice2);
        deviceRepository.save(impurity);

        var requestResult = mockMvc.perform(get("/api/devices?brand=" + brand + "&size=3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var devicesList = objectMapper.readValue(
                responseBody,
                new TypeReference<PageableModelTest<DeviceResponse>>() {}
        );

        Assertions.assertEquals(2, devicesList.totalElements());
        Assertions.assertEquals(deviceWithName1, devicesList.content().getFirst().getName());
        Assertions.assertEquals(deviceWithName2, devicesList.content().getLast().getName());
    }

    @Test
    public void fetchDeviceByState_isOk() throws Exception {
        var brand = "fetchDeviceByState_isOk";
        var deviceWithName1 = "testName1";
        var deviceWithName2 = "testName2";
        var knownDevice1 = new Device(UUID.randomUUID(), deviceWithName1, brand, DeviceState.INACTIVE, LocalDateTime.now());
        var knownDevice2 = new Device(UUID.randomUUID(), deviceWithName2, brand, DeviceState.INACTIVE, LocalDateTime.now());
        var impurity = new Device(UUID.randomUUID(), deviceWithName2, "any other brand", DeviceState.AVAILABLE, LocalDateTime.now());

        deviceRepository.save(knownDevice1);
        deviceRepository.save(knownDevice2);
        deviceRepository.save(impurity);

        var requestResult = mockMvc.perform(get("/api/devices?state=" + DeviceState.INACTIVE.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var devicesList = objectMapper.readValue(
                responseBody,
                new TypeReference<PageableModelTest<DeviceResponse>>() {}
        );

        Assertions.assertEquals(2, devicesList.totalElements());
        Assertions.assertEquals(DeviceState.INACTIVE.name(), devicesList.content().getFirst().getState());
        Assertions.assertEquals(DeviceState.INACTIVE.name(), devicesList.content().getLast().getState());
    }

    @Test
    public void fetchDeviceByState_isBadRequestStateParsingError() throws Exception {
        mockMvc.perform(get("/api/devices?state=unknown_state_test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith("Device state value is incorrect use: ")));
    }
}
