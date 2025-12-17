package de.device.demo.controllers;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceResponse;
import de.device.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<@NonNull DeviceResponse> createDevice(
            @Valid @RequestBody DeviceCreateRequest deviceCreateRequest
    ) {
        var newDevice = deviceService.create(deviceCreateRequest);
        var responseDto = new DeviceResponse(newDevice);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
