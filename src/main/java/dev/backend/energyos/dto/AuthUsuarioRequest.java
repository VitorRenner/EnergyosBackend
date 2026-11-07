package dev.backend.energyos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUsuarioRequest {
    private String email;
    private String senha;
}
