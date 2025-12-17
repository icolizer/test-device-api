package de.device.demo.integration;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceResponse;
import de.device.demo.models.DeviceState;
import de.device.demo.repositories.DeviceRepository;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceCreationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void success() throws Exception {
        var deviceTestName = "device test name";
        var brandTestName = "brand test name";
        var device = new DeviceCreateRequest(deviceTestName, brandTestName);

        var requestResult = mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        var responseBody = requestResult.getResponse().getContentAsString();
        var savedDevice = objectMapper.readValue(responseBody, DeviceResponse.class);

        var createdDevice = deviceRepository.findById(savedDevice.getId()).get();

        Assertions.assertEquals(deviceTestName, createdDevice.getName());
        Assertions.assertEquals(brandTestName, createdDevice.getBrand());
        Assertions.assertEquals(DeviceState.AVAILABLE, createdDevice.getState());
        Assertions.assertNotNull(createdDevice.getCreationTime());
    }

    @Test
    void whenNameIsBlank_returnValidationError() throws Exception {
        var device = new DeviceCreateRequest("", "brand");

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("Device name is required")))
                .andReturn();
    }

    @Test
    void whenBrandIsBlank_returnValidationError() throws Exception {
        var device = new DeviceCreateRequest("name", "");

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.brand", Is.is("Brand name is required")))
                .andReturn();
    }

}
