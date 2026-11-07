package dev.backend.energyos.infrastructure;


import com.influxdb.v3.client.InfluxDBClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Value("${influx.token}")
    private char[] token;

    @Value("${influx.bucket}")
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        String hostUrl = "https://us-east-1-1.aws.cloud2.influxdata.com";
        return InfluxDBClient.getInstance(hostUrl, token, bucket);
    }
}
