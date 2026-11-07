package dev.backend.energyos.controller;


import dev.backend.energyos.dto.AuthUsuarioRequest;
import dev.backend.energyos.dto.UsuarioRequest;
import dev.backend.energyos.dto.VincUsuarioMicroRequest;
import dev.backend.energyos.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioService service;

    public UsuarioController(UsuarioService service)
    {
        this.service = service;
    }

    @PostMapping("/novo_usuario")
    public ResponseEntity<String> salvarUsuario(@RequestBody UsuarioRequest request) {
        return service.salvarUsuario(request);
    }

    @PostMapping("/auth_usuario")
    public ResponseEntity<String> authUsuario(@RequestBody AuthUsuarioRequest request) {
        return service.authUsuario(request);
    }

    @PutMapping("/vincular_microcontrolador")
    public ResponseEntity<String> vincUsuarioComMicro(
            @RequestBody VincUsuarioMicroRequest request) {

        return service.vincUsuarioMicrocontrolador(request);
    }
}
