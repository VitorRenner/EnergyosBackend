package dev.backend.energyos.controller;

import dev.backend.energyos.dto.MedicaoRequest;
import dev.backend.energyos.dto.MedicaoResponse;
import dev.backend.energyos.service.MicroControladorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/microcontroladores")
public class MicroControladorController {

    private final MicroControladorService service;

    public MicroControladorController(MicroControladorService service) {
        this.service = service;
    }

    @PostMapping("/{id}/medicao")
    public void salvarMedicao(
            @PathVariable UUID id,
            @RequestBody MedicaoRequest request
    ) {
        service.salvarMedicao(
                id,
                request.getTensao(),
                request.getCorrente(),
                request.getPotencia(),
                request.getEnergia(),
                request.getFrequencia()
        );
    }
    @GetMapping("/{id}/medicoes")
    public List<MedicaoResponse> buscarMedicoes(@PathVariable UUID id) {
        return service.buscarMedicoes(id);
    }
}