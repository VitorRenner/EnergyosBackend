package dev.backend.energyos.controller;

import com.influxdb.v3.client.InfluxDBClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoint debug para localizar microControlID existente no Influx.
 *
 * A ideia é rodar:
 *   GET /api/influx/debug/microcontroladores
 * para receber uma lista de IDs distintos encontrados no bucket/tabela.
 */
@RestController
@RequestMapping("/api/influx/debug")
public class InfluxDebugController {

    private final InfluxDBClient influxDBClient;

    @Value("${influx.bucket:energyosDB}")
    private String bucket;

    public InfluxDebugController(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @GetMapping("/microcontroladores")
    public ResponseEntity<List<String>> microcontroladores() {
        // Query buscando tags/colunas onde microControlID foi gravado.
        // Dependendo da forma como o Influx representa os dados, isso pode variar.
        // Tentamos a forma mais comum: distinct values no campo/tags.
        // microControlID foi gravado como TAG
        String query = "SELECT DISTINCT microControlID FROM microcontrolador_data LIMIT 50";


        var result = influxDBClient.query(query);
        java.util.LinkedHashSet<String> ids = new java.util.LinkedHashSet<>();

        result.forEach(rowObj -> {
            try {
                Object[] arr = (Object[]) rowObj;
                if (arr != null && arr.length > 0 && arr[0] != null) {
                    ids.add(arr[0].toString());
                }
            } catch (Exception ignored) {
            }
        });

        return ResponseEntity.ok(new java.util.ArrayList<>(ids));
    }
}

