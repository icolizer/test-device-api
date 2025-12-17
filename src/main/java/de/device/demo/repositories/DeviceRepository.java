package de.device.demo.repositories;

import de.device.demo.entities.Device;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<@NonNull Device, @NonNull Long> {
}
