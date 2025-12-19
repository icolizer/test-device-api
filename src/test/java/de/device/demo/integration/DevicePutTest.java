package de.device.demo.integration;

import de.device.demo.dtos.DevicePutRequest;
import de.device.demo.dtos.DeviceResponse;
import de.device.demo.dtos.DevicePatchRequest;
import de.device.demo.entities.Device;
import de.device.demo.errors.Errors;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DevicePutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @AfterAll
    void tearDown() {
        deviceRepository.deleteAll();
    }

    @Test
    void updateDevice_isOk() throws Exception {
        var newDevice = new Device(UUID.randomUUID(), "name", "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        var newName = "device test name";
        var newBrand = "brand test name";
        var newState = DeviceState.INACTIVE;
        var deviceUpdateRequest = new DevicePutRequest(
                newName,
                newBrand,
                newState.name(),
                null
        );

        var requestResult = mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var savedDevice = objectMapper.readValue(responseBody, DeviceResponse.class);

        var updatedDevice = deviceRepository.findById(savedDevice.getId()).get();

        Assertions.assertEquals(newName, updatedDevice.getName());
        Assertions.assertEquals(newBrand, updatedDevice.getBrand());
        Assertions.assertEquals(newState, updatedDevice.getState());
        Assertions.assertEquals(newDevice.getCreationTime(), updatedDevice.getCreationTime());
    }

    @Test
    void updateDeviceWithCreationDate_isConflictResponse() throws Exception {
        var oldName = "old name";
        var newDevice = new Device(UUID.randomUUID(), oldName, "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        var newName = "device test name";
        var newBrand = "brand test name";
        var newState = DeviceState.INACTIVE;
        var deviceUpdateRequest = new DevicePutRequest(
                newName,
                newBrand,
                newState.name(),
                LocalDateTime.now()
        );

         mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceUpdateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", startsWith(Errors.DEVICE_NOT_MODIFIABLE.getErrorCode())));

        var storedDevice = deviceRepository.findById(newDevice.getId()).get();
        Assertions.assertEquals(oldName, storedDevice.getName());
    }

    @Test
    void updateDeviceWhichIsInUseState_isConflictResponse() throws Exception {
        var oldName = "old name";
        var newDevice = new Device(UUID.randomUUID(), oldName, "brand", DeviceState.IN_USE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        var newName = "device test name";
        var newBrand = "brand test name";
        var newState = DeviceState.INACTIVE;
        var deviceUpdateRequest = new DevicePutRequest(
                newName,
                newBrand,
                newState.name(),
                null
        );

        mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceUpdateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", startsWith(Errors.DEVICE_NOT_MODIFIABLE.getErrorCode())));

        var storedDevice = deviceRepository.findById(newDevice.getId()).get();
        Assertions.assertEquals(oldName, storedDevice.getName());
    }

    @Test
    void tryUpdateWithoutRequiredParameters_isBadRequest() throws Exception {
        var newDevice = new Device(UUID.randomUUID(), "name", "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);
        var newState = DeviceState.IN_USE;

        mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"%s\"}".formatted(newState)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("Device name is required")))
                .andExpect(jsonPath("$.brand", Is.is("Device brand is required")));
    }

    @Test
    void createDevice_isOk() throws Exception {
        var newUuid = UUID.randomUUID();
        var newName = "createDevice_isOk_name";
        var newBrand = "createDevice_isOk_brand";
        var newState = DeviceState.INACTIVE;
        var now = LocalDateTime.now();
        var deviceUpdateRequest = new DevicePutRequest(
                newName,
                newBrand,
                newState.name(),
                now
        );

        var requestResult = mockMvc.perform(put("/api/devices/" + newUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceUpdateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var savedDevice = objectMapper.readValue(responseBody, DeviceResponse.class);

        var createdDevice = deviceRepository.findById(savedDevice.getId()).get();

        Assertions.assertEquals(newUuid, createdDevice.getId());
        Assertions.assertEquals(newName, createdDevice.getName());
        Assertions.assertEquals(newBrand, createdDevice.getBrand());
        Assertions.assertEquals(newState, createdDevice.getState());
        Assertions.assertEquals(now.truncatedTo(ChronoUnit.SECONDS), createdDevice.getCreationTime().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void createDeviceWithoutCreationTime_isBadRequest() throws Exception {
        var newUuid = UUID.randomUUID();
        var testText = "createDeviceWithoutCreationTime_isBadRequest";
        var newState = DeviceState.INACTIVE;
        var deviceUpdateRequest = new DevicePutRequest(
                testText,
                testText,
                newState.name(),
                null
        );

        mockMvc.perform(put("/api/devices/" + newUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Is.is("A required field 'creation_time' is missing")));
    }
}
