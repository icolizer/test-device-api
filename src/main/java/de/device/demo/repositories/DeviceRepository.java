package de.device.demo.repositories;

import de.device.demo.entities.Device;
import de.device.demo.models.DeviceState;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;

@Repository
public interface DeviceRepository extends JpaRepository<@NonNull Device, @NonNull UUID> {

    @Lock(PESSIMISTIC_READ)
    @Query("select o from Device o where o.id = :id")
    Optional<Device> findByIdForUpdate(UUID id);

    Page<Device> findByBrand(String brand, Pageable pageable);

    Page<Device> findByState(DeviceState state, Pageable pageable);
}
