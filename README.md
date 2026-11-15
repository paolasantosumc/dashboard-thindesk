# ThinDesk

O Dashboard administrativo do ThinDesk apresenta uma visão consolidada dos chamados técnicos. A interface permite acompanhar indicadores, comparar chamados recebidos e atendidos por dia, visualizar a distribuição por situação e tipo, consultar registros recentes e aplicar filtros por período, status, tipo e técnico.

A implementação analisada utiliza Spring Boot no backend, Spring Data MongoDB para acesso aos chamados, Thymeleaf para renderização da página e JavaScript para consumir a API e atualizar os componentes visuais. O endpoint específico do painel é GET /api/dashboard.

## API

Exemplos de consumo da API

- http://localhost:8080/api/dashboard?inicio=2026-11-10&fim=2026-11-14
- http://localhost:8080/api/dashboard?inicio=2026-11-10&fim=2026-11-14&status=ABERTO
- http://localhost:8080/api/dashboard?inicio=2026-11-10&fim=2026-11-14&status=ABERTO&tipo=Acesso
- http://localhost:8080/api/dashboard?inicio=2026-11-10&fim=2026-11-14&tipo=Acesso
- http://localhost:8080/api/dashboard?inicio=2026-11-10&fim=2026-11-14&status=ABERTO&tipo=Acesso&tecnico=Marina
