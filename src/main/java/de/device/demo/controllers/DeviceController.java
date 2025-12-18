package de.device.demo.controllers;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceResponse;
import de.device.demo.dtos.DeviceUpdateRequest;
import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import de.device.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> device(@PathVariable("id") Long id) {
        return new ResponseEntity<>(
                new DeviceResponse(deviceService.getById(id)),
                HttpStatus.OK
        );
    }

    /***
     * List devices
     *
     * @param pageable default 3 only used as an example to simplify tests
     * @return Response with devices limited by pages
     */
    @GetMapping
    public Page<@NonNull DeviceResponse> devices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String state,
            @PageableDefault(size = 3) Pageable pageable
    ) {
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

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeviceUpdateRequest deviceUpdateRequest
    ) {
        var device = deviceService.update(id, deviceUpdateRequest);

        return new ResponseEntity<>(new DeviceResponse(device), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull DeviceResponse> delete(@PathVariable("id") Long id) {
        deviceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
