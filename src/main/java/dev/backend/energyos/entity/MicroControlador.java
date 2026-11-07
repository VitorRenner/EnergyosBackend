package dev.backend.energyos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "micro_controlador")
@Getter
@Setter
public class MicroControlador {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID idEsp32;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String nomeModulo;
    private LocalDateTime dataUltimaTrocaModulo;

    private LocalDateTime dataSaidaManutencao;

    private LocalDateTime dataTrocado;

    private LocalDateTime dataRetornoManutencao;
}
