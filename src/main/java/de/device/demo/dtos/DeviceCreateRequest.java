package de.device.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceCreateRequest(
    @NotBlank(message = "Device name is required")
    @Size(max = 255, message = "Device name should not be longer 255 characters")
    String name,

    @NotBlank(message = "Brand name is required")
    @Size(max = 255, message = "Brand name should not be longer 255 characters")
    String brand
) {}
