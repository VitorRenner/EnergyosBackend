package dev.backend.energyos.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicaoRequest {
    private double tensao;
    private double corrente;
    private double potencia;
    private double energia;
    private double frequencia;
}