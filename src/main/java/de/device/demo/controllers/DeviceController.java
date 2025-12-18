package de.device.demo.controllers;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceResponse;
import de.device.demo.dtos.DeviceUpdateRequest;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import de.device.demo.services.DeviceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Device REST API", description = "Manage Devices entries")
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Tag(name = "create", description = "Create new Device entry with default state")
    @PostMapping
    public ResponseEntity<@NonNull DeviceResponse> createDevice(
            @Valid @RequestBody DeviceCreateRequest deviceCreateRequest
    ) {
        log.info("Create request");

        var newDevice = deviceService.create(deviceCreateRequest);
        var responseDto = new DeviceResponse(newDevice);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Tag(name = "find", description = "Find device by device id provided as path variable")
    @GetMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> device(@PathVariable("id") Long id) {
        log.info("Find device request id {}", id);

        return new ResponseEntity<>(
                new DeviceResponse(deviceService.getById(id)),
                HttpStatus.OK
        );
    }

    @Tag(name = "find all", description = "Find all devices or also search by brand name or state")
    @GetMapping
    public Page<@NonNull DeviceResponse> devices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String state,
            @ParameterObject @PageableDefault(size = 100) Pageable pageable
    ) {
        log.info("Find devices request brand {} and state {}", brand, state);

        Page<Device> devices;

        if (brand == null && state == null) {
            devices = deviceService.getDevices(pageable);
        } else if (brand != null) {
            devices = deviceService.getDevicesByBrand(brand, pageable);
        } else {
            try {
                var stateValue = DeviceState.valueOf(state);
                devices = deviceService.getDevicesByState(stateValue, pageable);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Device state value is incorrect use: " + DeviceState.getValuesDescription());
            }
        }

        var devicesResponse = devices.stream().parallel()
                .map(DeviceResponse::new)
                .toList();

        return new PageImpl<>(devicesResponse, pageable, devices.getTotalElements());
    }

    @Tag(name = "update", description = "Update device by id and DeviceUpdateRequest payload")
    @PutMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeviceUpdateRequest deviceUpdateRequest
    ) {
        log.info("Update device id {}", id);

        var device = deviceService.update(id, deviceUpdateRequest);

        return new ResponseEntity<>(new DeviceResponse(device), HttpStatus.OK);
    }

    @Tag(name = "delete", description = "Delete device by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> delete(@PathVariable("id") Long id) {
        log.info("Delete device id {}", id);

        deviceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
