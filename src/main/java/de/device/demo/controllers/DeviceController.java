package de.device.demo.controllers;

import de.device.demo.dtos.DeviceCreateRequest;
import de.device.demo.dtos.DeviceResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public DeviceResponse device(@PathVariable("id") Long id) {
        return new DeviceResponse(deviceService.getById(id));
    }

    /***
     * List devices
     *
     * @param pageable default 3 only used as an example to simplify tests
     * @return Response with devices limited by pages
     */
    @GetMapping
    public Page<@NonNull DeviceResponse> devices(@PageableDefault(size = 3) Pageable pageable) {
        var devices = deviceService.getDevices(pageable);
        var devicesResponse = devices.stream().parallel()
                .map(DeviceResponse::new)
                .toList();

        return new PageImpl<>(devicesResponse, pageable, devices.getTotalElements());
    }
}
