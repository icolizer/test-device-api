package de.device.demo.integration;

import de.device.demo.dtos.DeviceResponse;
import de.device.demo.dtos.DeviceUpdateRequest;
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

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceUpdateTest {

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
    void updatedAllFields_isOk() throws Exception {
        var newDevice = new Device("name", "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        var newName = "device test name";
        var newBrand = "brand test name";
        var newState = DeviceState.INACTIVE;
        var deviceUpdateRequest = new DeviceUpdateRequest(
                newName,
                newBrand,
                newState.name()
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
    void partialUpdateOnlyState_isOk() throws Exception {
        var newDevice = new Device("name", "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);
        var newState = DeviceState.IN_USE;

        var requestResult = mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"%s\"}".formatted(newState)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var savedDevice = objectMapper.readValue(responseBody, DeviceResponse.class);

        var updatedDevice = deviceRepository.findById(savedDevice.getId()).get();

        Assertions.assertEquals(newDevice.getName(), updatedDevice.getName());
        Assertions.assertEquals(newDevice.getBrand(), updatedDevice.getBrand());
        Assertions.assertEquals(newState, updatedDevice.getState());
        Assertions.assertEquals(newDevice.getCreationTime(), updatedDevice.getCreationTime());
    }

    @Test
    void updatedStateWhichDoesNotExists_isBadRequest() throws Exception {
        mockMvc.perform(put("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"__broken_value_test__\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state", Is.is("Invalid Device state type")))
                .andExpect(jsonPath("$.brand").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist());
    }

    @Test
    void updatedDeviceWhichDoesNotExists_isNotFound() throws Exception {
        mockMvc.perform(put("/api/devices/" + Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"something\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", startsWith(Errors.DEVICE_ID_NOT_FOUND.getErrorCode())));
    }

    @Test
    void updatedDeviceEmptyRequest_isBadRequest() throws Exception {
        mockMvc.perform(put("/api/devices/" + Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Is.is("At least one field must be provided")));
    }

    @Test
    void updatedDeviceNameInUseState_isConflict() throws Exception {
        var newDevice = new Device("name", "brand", DeviceState.IN_USE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"something\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Is.is(
                        "%s: name or brand fields cannot be updated due to IN_USE state of device with id %d"
                                .formatted(Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(), newDevice.getId())
                )));
    }

    @Test
    void updatedDeviceBrandInUseState_isConflict() throws Exception {
        var newDevice = new Device("name", "brand", DeviceState.IN_USE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);

        mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brand\":\"something\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Is.is(
                        "%s: name or brand fields cannot be updated due to IN_USE state of device with id %d"
                                .formatted(Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(), newDevice.getId())
                )));
    }

    @Test
    void updatedDeviceStateInUseState_isOk() throws Exception {
        var newDevice = new Device("name", "brand", DeviceState.IN_USE, LocalDateTime.now());
        newDevice = deviceRepository.save(newDevice);
        var newState = DeviceState.INACTIVE;

        mockMvc.perform(put("/api/devices/" + newDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"%s\"}".formatted(newState)))
                .andExpect(status().isOk());

        var updatedDevice = deviceRepository.findById(newDevice.getId()).get();

        Assertions.assertEquals(newState, updatedDevice.getState());
        Assertions.assertEquals(newDevice.getName(), updatedDevice.getName());
        Assertions.assertEquals(newDevice.getBrand(), updatedDevice.getBrand());
    }
}
