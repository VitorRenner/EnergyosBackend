# EnergyOS — Back-end

API REST em **Java + Spring Boot** do **EnergyOS**, um sistema IoT que monitora o consumo de energia de uma casa em tempo real.

Um ESP32 com sensor PZEM-004T mede tensão, corrente, potência, energia e frequência e envia as leituras para esta API. Ela guarda as medições numa base de séries temporais (InfluxDB), cuida dos usuários e dos microcontroladores (PostgreSQL) e entrega os dados para o [dashboard web](https://github.com/VitorRenner/Energyos-Front).

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![InfluxDB](https://img.shields.io/badge/InfluxDB-22ADF6?style=flat-square&logo=influxdb&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

---

## Arquitetura

```text
ESP32 + PZEM-004T ──▶ API Spring Boot ──▶ InfluxDB      (medições: V, A, W, kWh, Hz)
                           │
                           ├──▶ PostgreSQL               (usuários e microcontroladores)
                           │
                           └──▶ Dashboard React          (GET /api/telemetry/latest)
```

**Por que dois bancos?** As medições chegam o tempo todo e são consultadas por período, então ficam num banco de séries temporais (InfluxDB). Usuários e dispositivos são dados relacionais, então ficam no PostgreSQL com JPA.

O código segue camadas separadas: `controller` → `service` → `repository`, com DTOs para entrada e saída.

## Funcionalidades

- **Medições:** recebe leituras do ESP32 e consulta o histórico de cada microcontrolador
- **Telemetria em tempo real:** última leitura pronta para o dashboard
- **Usuários:** cadastro e login, com senha criptografada com **BCrypt**
- **Dispositivos:** vínculo entre usuário e microcontrolador, com datas de troca e manutenção do módulo
- **Perfis de acesso:** `ADMIN` e `USER`
- **Health check** com Spring Actuator

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/microcontroladores/{id}/medicao` | Grava uma medição (tensão, corrente, potência, energia, frequência) |
| `GET` | `/microcontroladores/{id}/medicoes` | Lista as medições de um microcontrolador |
| `GET` | `/api/telemetry/latest` | Última leitura, usada pelo dashboard |
| `POST` | `/usuarios/novo_usuario` | Cadastra um usuário |
| `POST` | `/usuarios/auth_usuario` | Autentica um usuário |
| `PUT` | `/usuarios/vincular_microcontrolador` | Vincula um usuário a um microcontrolador |
| `GET` | `/actuator/health` | Status da aplicação |

A referência completa da API está em [`EnergyOS_API_Reference.pdf`](EnergyOS_API_Reference.pdf).

## Tecnologias

| Camada | Ferramentas |
| --- | --- |
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Actuator) |
| Bancos | PostgreSQL, InfluxDB 3 (`influxdb3-java`) |
| Segurança | Spring Security Crypto (BCrypt) |
| Produtividade | Lombok |
| Build | Maven (wrapper incluso) |

## Como rodar

Precisa de **Java 17**, um **PostgreSQL** local com o banco `energyos` e um bucket no **InfluxDB**.

1. Configure as variáveis de ambiente:

```bash
export DB_PASSWORD=sua_senha_do_postgres
export INFLUX_TOKEN=seu_token_do_influx
```

2. Suba a API:

```bash
./mvnw spring-boot:run
```

A API fica em `http://localhost:8080`.

### Testes

```bash
./mvnw test
```

## Estrutura

```text
src/main/java/dev/backend/energyos/
├── controller/       # rotas da API
├── service/          # regras de negócio
├── repository/       # acesso ao PostgreSQL (JPA)
├── entity/           # Usuario, MicroControlador, Role
├── dto/              # objetos de entrada e saída
├── config/           # CORS
└── infrastructure/   # conexão com o InfluxDB
```

## Próximos passos

- [ ] Login com token JWT protegendo as rotas
- [ ] Testes de integração dos controllers
- [ ] Docker Compose com PostgreSQL e API
- [ ] Deploy público com link

## Projeto em equipe

O EnergyOS foi feito em equipe, dividido em hardware, back-end e front-end.
Front-end: [Energyos-Front](https://github.com/VitorRenner/Energyos-Front)

**Vitor Rogge Renner** · [LinkedIn](https://www.linkedin.com/in/vitor-rogge-renner-5367b82b8) · [GitHub](https://github.com/VitorRenner)
