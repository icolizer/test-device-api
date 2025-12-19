package de.device.demo.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.device.demo.models.DeviceState;
import de.device.demo.validators.DeviceStateValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record DevicePutRequest(
        @NotBlank(message = "Device name is required")
        @Size(min = 1, max = 255, message = "Device name should not be longer 255 characters")
        String name,

        @NotBlank(message = "Device brand is required")
        @Size(min = 1, max = 255, message = "Brand name should not be longer 255 characters")
        String brand,

        @NotBlank(message = "Device state is required")
        @DeviceStateValid(enumClass = DeviceState.class, message = "Invalid Device state type")
        String state,

        @JsonProperty("creation_time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime creationTime
) {
}
