package dev.backend.energyos.repository;

import dev.backend.energyos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, UUID> {
    Usuario findByEmail(String email);
}
