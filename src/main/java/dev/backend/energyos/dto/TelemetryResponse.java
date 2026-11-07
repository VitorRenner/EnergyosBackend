package dev.backend.energyos.dto;

import java.util.List;
import java.util.UUID;

/**
 * Objeto compatível com o frontend (web/src/services/mockEnergyService.js).
 * Mantém a estrutura `system/metrics/device/series/alerts/insights`.
 */
public class TelemetryResponse {

    public SystemTelemetry system;
    public MetricsTelemetry metrics;
    public DeviceTelemetry device;
    public SeriesTelemetry series;
    public List<Alert> alerts;
    public List<Insight> insights;

    public static class SystemTelemetry {
        public String status;
        public String mqtt;
        public String esp32;
        public String uptime;
        public String lastReading;
    }

    public static class MetricsTelemetry {
        public double power;
        public double voltage;
        public double current;
        public double frequency;
        public double totalEnergy;
        public double dailyEnergy;
        public double monthlyEnergy;
        public double monthlyCost;
        public double efficiency;
    }

    public static class DeviceTelemetry {
        public String name;
        public double wifi;
        public String ip;
        public double mqttLatency;
        public String hardwareUptime;
        public String refreshRate;
        public double temperature;
        public String firmware;
    }

    public static class SeriesTelemetry {
        public List<Point> power;
        public List<Point> consumption;
        public List<Point> comparison;
        public List<Point> daily;
    }

    public static class Point {
        public String label;
        public double value;
        public String unit;
    }

    public static class Alert {
        public int id;
        public String title;
        public String description;
        public String priority;
        public String status;
        public String time;
    }

    public static class Insight {
        public String label;
        public String value;
        public String detail;
        public String trend;
    }
}

