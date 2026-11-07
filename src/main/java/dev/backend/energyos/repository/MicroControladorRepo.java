package dev.backend.energyos.repository;

import dev.backend.energyos.entity.MicroControlador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MicroControladorRepo extends JpaRepository<MicroControlador, UUID> {
}
