package dev.backend.energyos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class VincUsuarioMicroRequest {
    private UUID idUsuario;
    private UUID idMicroControlador;
}
