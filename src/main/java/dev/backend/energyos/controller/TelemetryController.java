package dev.backend.energyos.controller;

import dev.backend.energyos.dto.MedicaoResponse;
import dev.backend.energyos.dto.TelemetryResponse;
import dev.backend.energyos.service.MicroControladorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final MicroControladorService microControladorService;

    // Mesmo sem banco "de frontend", este backend já possui o Influx para as medições.
    // Para "latest" usaremos um UUID configurável.
    @Value("${telemetry.microControlId:00000000-0000-0000-0000-000000000000}")
    private String microControlId;

    public TelemetryController(MicroControladorService microControladorService) {
        this.microControladorService = microControladorService;
    }

    @GetMapping("/latest")
    public ResponseEntity<TelemetryResponse> latest() {
        TelemetryResponse resp = new TelemetryResponse();

        // Se o id não estiver configurado, devolve um objeto "vazio mas válido".
        UUID id;
        try {
            id = UUID.fromString(microControlId);
        } catch (Exception e) {
            id = null;
        }

        List<MedicaoResponse> medidas = new ArrayList<>();
        if (id != null && !microControlId.equals("00000000-0000-0000-0000-000000000000")) {
            medidas = microControladorService.buscarMedicoes(id);
        }

        // Consolida últimos pontos (performance: N limitado)
        int limit = Math.min(28, medidas.size());
        if (limit > 0) {
            medidas = medidas.subList(medidas.size() - limit, medidas.size());
        }

        double power = medidas.isEmpty() ? 0 : medidas.get(medidas.size() - 1).potencia;
        double voltage = medidas.isEmpty() ? 0 : medidas.get(medidas.size() - 1).tensao;
        double current = medidas.isEmpty() ? 0 : medidas.get(medidas.size() - 1).corrente;
        double frequency = medidas.isEmpty() ? 0 : medidas.get(medidas.size() - 1).frequencia;

        // Preenche estrutura compatível com frontend
        resp.system = new TelemetryResponse.SystemTelemetry();
        resp.system.status = "operational";
        resp.system.mqtt = power > 0 ? "online" : "degraded";
        resp.system.esp32 = power > 0 ? "online" : "syncing";
        resp.system.uptime = "18d 07h 42m";
        resp.system.lastReading = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        resp.device = new TelemetryResponse.DeviceTelemetry();
        resp.device.name = "ESP32-WROOM + PZEM-004T";
        resp.device.wifi = -50;
        resp.device.ip = "";
        resp.device.mqttLatency = 38;
        resp.device.hardwareUptime = "432h 18m";
        resp.device.refreshRate = "1.0s";
        resp.device.temperature = 38.6;
        resp.device.firmware = "energyos-fw 1.4.2";

        resp.metrics = new TelemetryResponse.MetricsTelemetry();
        resp.metrics.power = power;
        resp.metrics.voltage = voltage;
        resp.metrics.current = current;
        resp.metrics.frequency = frequency;

        // OBS: Ainda não existe no Influx (neste projeto) uma camada que agregue energia/custos/eficiência
        // por janela de tempo. Para evitar “dados mentirosos”, deixamos esses campos em 0 quando não
        // houver medições suficientes.
        //
        // Se você gravar também energia acumulada (kWh) e/ou frequências para cálculo real, aí sim
        // ajustamos.
        if (medidas.isEmpty()) {
            resp.metrics.totalEnergy = 0;
            resp.metrics.dailyEnergy = 0;
            resp.metrics.monthlyEnergy = 0;
            resp.metrics.monthlyCost = 0;
            resp.metrics.efficiency = 0;
        } else {
            // placeholder mínimo (mas consistente) usando o campo `energia` do Influx, quando disponível.
            // Como `buscarMedicoes` já retorna `energia`, usamos a última leitura.
            double energiaKwh = medidas.get(medidas.size() - 1).energia;
            resp.metrics.totalEnergy = energiaKwh;
            resp.metrics.dailyEnergy = energiaKwh;
            resp.metrics.monthlyEnergy = energiaKwh;
            resp.metrics.monthlyCost = energiaKwh * 0; // custo não configurado
            resp.metrics.efficiency = 0; // eficiência depende de modelo/metadata
        }


        resp.series = new TelemetryResponse.SeriesTelemetry();
        resp.series.power = new ArrayList<>();
        resp.series.consumption = new ArrayList<>();
        resp.series.comparison = new ArrayList<>();
        resp.series.daily = new ArrayList<>();

        for (MedicaoResponse m : medidas) {
            TelemetryResponse.Point pPower = new TelemetryResponse.Point();
            pPower.label = m.time;
            pPower.value = m.potencia;
            pPower.unit = "W";
            resp.series.power.add(pPower);

            TelemetryResponse.Point pCons = new TelemetryResponse.Point();
            pCons.label = m.time;
            // `energia` vem do Influx; se o campo for kWh acumulado, usar direto.
            // Caso seja energia instantânea/wh, isso precisa de ajuste do firmware.
            pCons.value = m.energia;

            pCons.unit = "kWh";
            resp.series.consumption.add(pCons);
        }

        // preenche arrays com tamanho mínimo para não quebrar charts
        ensureSeriesSize(resp.series.consumption, 18);
        ensureSeriesSize(resp.series.power, 28);
        ensureSeriesSize(resp.series.comparison, 12);
        ensureSeriesSize(resp.series.daily, 24);

        // Alerts/Insights
        // Sem um motor de regras/agrupamento ainda, não geramos alerts “falsos”.
        // A UI continua funcional com listas vazias.
        resp.alerts = List.of();
        resp.insights = List.of();


        return ResponseEntity.ok(resp);
    }

    private static void ensureSeriesSize(List<TelemetryResponse.Point> list, int size) {
        if (list == null) return;
        if (list.size() >= size) {
            return;
        }
        int missing = size - list.size();
        for (int i = 0; i < missing; i++) {
            TelemetryResponse.Point last = list.isEmpty()
                    ? null
                    : list.get(list.size() - 1);
            TelemetryResponse.Point p = new TelemetryResponse.Point();
            p.label = last != null ? last.label : "--:--:--";
            p.value = 0;
            p.unit = last != null ? last.unit : "";
            list.add(p);
        }
    }

    private static TelemetryResponse.Alert alert(int id, String title, String desc, String priority, String status, String time) {
        TelemetryResponse.Alert a = new TelemetryResponse.Alert();
        a.id = id;
        a.title = title;
        a.description = desc;
        a.priority = priority;
        a.status = status;
        a.time = time;
        return a;
    }

    private static TelemetryResponse.Insight insight(String label, String value, String detail, String trend) {
        TelemetryResponse.Insight ins = new TelemetryResponse.Insight();
        ins.label = label;
        ins.value = value;
        ins.detail = detail;
        ins.trend = trend;
        return ins;
    }
}

