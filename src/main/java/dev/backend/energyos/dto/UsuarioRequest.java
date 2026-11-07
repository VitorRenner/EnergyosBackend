package dev.backend.energyos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String nome;
    private String email;
    private String senha;
    private String numeroCasa;
    private String cep;
}
