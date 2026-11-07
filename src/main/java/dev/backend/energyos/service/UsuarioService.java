package dev.backend.energyos.service;

import dev.backend.energyos.dto.AuthUsuarioRequest;
import dev.backend.energyos.dto.UsuarioRequest;
import dev.backend.energyos.dto.VincUsuarioMicroRequest;
import dev.backend.energyos.entity.MicroControlador;
import dev.backend.energyos.entity.Role;
import dev.backend.energyos.entity.Usuario;
import dev.backend.energyos.repository.MicroControladorRepo;
import dev.backend.energyos.repository.UsuarioRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Service
public class UsuarioService {

    private final UsuarioRepo repo;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final MicroControladorRepo microRepo;

    public UsuarioService(UsuarioRepo repo,MicroControladorRepo microRepo){
        this.repo = repo;
        this.microRepo = microRepo;
    }

    public ResponseEntity<String> salvarUsuario(UsuarioRequest request){

        if (repo.findByEmail(request.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(encoder.encode(request.getSenha()));
        usuario.setNumeroCasa(request.getNumeroCasa());
        usuario.setCep(request.getCep());
        usuario.setRole(Role.USER);

        repo.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso");
    }

    public ResponseEntity<String> authUsuario(AuthUsuarioRequest request){

        Usuario usuario = repo.findByEmail(request.getEmail());

        if (usuario == null || !encoder.matches(request.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        }

        return ResponseEntity.ok("Login realizado com sucesso");
    }

    public ResponseEntity<String> vincUsuarioMicrocontrolador(VincUsuarioMicroRequest request){

        Usuario usuario = repo.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getRole() == null || !usuario.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas ADMIN pode vincular microcontrolador");
        }

        MicroControlador micro = microRepo.findById(request.getIdMicroControlador())
                .orElse(null);

        if (micro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microcontrolador não encontrado");
        }

        if (micro.getUsuario() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Microcontrolador já está vinculado a outro usuário");
        }

        micro.setUsuario(usuario);
        microRepo.save(micro);

        return ResponseEntity.ok("Microcontrolador vinculado ao usuário com sucesso");
    }
}
