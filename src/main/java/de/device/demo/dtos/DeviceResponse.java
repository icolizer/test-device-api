package de.device.demo.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.device.demo.entities.Device;

import java.time.LocalDateTime;

public class DeviceResponse {

    private Long id;

    private String name;

    private String brand;

    private String state;

    @JsonProperty("creation_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationTime;

    /**
     * Use in test object mapper
     */
    public DeviceResponse() {
    }

    public DeviceResponse(Device newDevice) {
        this.id = newDevice.getId();
        this.name = newDevice.getName();
        this.brand = newDevice.getBrand();
        this.state = newDevice.getState().name();
        this.creationTime = newDevice.getCreationTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
