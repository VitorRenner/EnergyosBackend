package dev.backend.energyos.service;

import com.influxdb.v3.client.InfluxDBClient;
import dev.backend.energyos.dto.MedicaoResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class MicroControladorService {

    private final InfluxDBClient influxDBClient;

    public MicroControladorService(InfluxDBClient influxDBClient){
        this.influxDBClient = influxDBClient;
    }

    // errar uma virgula irá quebrar a query de insert no banco, cuidado!
    public void salvarMedicao(UUID microControlID,
                              double tensao,
                              double corrente,
                              double potencia,
                              double energia,
                              double frequencia) {

        String line = String.format(Locale.US,
                "microcontrolador_data,microControlID=%s tensao=%f,corrente=%f,potencia=%f,energia=%f,frequencia=%f",
                microControlID, tensao, corrente, potencia, energia, frequencia
        );

        influxDBClient.writeRecord(line);
    }
    public List<MedicaoResponse> buscarMedicoes(UUID microControlID) {

        // `microControlID` foi gravado como TAG no line protocol em salvarMedicao(...)
        // Então a consulta precisa filtrar a TAG corretamente.
        String query = String.format(
                "SELECT time, tensao, corrente, potencia, energia, frequencia " +
                        "FROM microcontrolador_data " +
                        "WHERE microControlID = '%s'",
                microControlID
        );


        var result = influxDBClient.query(query);

        List<MedicaoResponse> data = new ArrayList<>();

        result.forEach(rowObj -> {

            MedicaoResponse dto = new MedicaoResponse();
            dto.time = ((Object[]) rowObj)[0].toString();
            dto.tensao = ((Number) ((Object[]) rowObj)[1]).doubleValue();
            dto.corrente = ((Number) ((Object[]) rowObj)[2]).doubleValue();
            dto.potencia = ((Number) ((Object[]) rowObj)[3]).doubleValue();
            dto.energia = ((Number) ((Object[]) rowObj)[4]).doubleValue();
            dto.frequencia = ((Number) ((Object[]) rowObj)[5]).doubleValue();

            data.add(dto);
        });

        return data;
    }
}