package de.device.demo.integration;

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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceDeletionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeviceRepository deviceRepository;

    @AfterAll
    void tearDown() {
        deviceRepository.deleteAll();
    }

    @Test
    void success() throws Exception {
        var tempDevice = new Device("name", "brand", DeviceState.AVAILABLE, LocalDateTime.now());
        deviceRepository.save(tempDevice);

        mockMvc.perform(delete("/api/devices/" + tempDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        var device = deviceRepository.findById(tempDevice.getId());

        Assertions.assertTrue(device.isEmpty());
    }

    @Test
    void deleteDeviceInUse_getConflictError() throws Exception {
        var tempDevice = new Device("name", "brand", DeviceState.IN_USE, LocalDateTime.now());
        deviceRepository.save(tempDevice);

        mockMvc.perform(delete("/api/devices/" + tempDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Is.is(
                        "%s: device cannot be deleted due to IN_USE state, device id %d"
                                .formatted(Errors.DEVICE_NOT_MODIFIABLE.getErrorCode(), tempDevice.getId())
                )))
                .andExpect(status().isConflict());

        var device = deviceRepository.findById(tempDevice.getId());

        Assertions.assertTrue(device.isPresent());
    }

    @Test
    void deleteDeviceDoesNotExists_getNotFoundResponse() throws Exception {
        mockMvc.perform(delete("/api/devices/-100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
