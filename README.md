# ThinDesk

O Dashboard administrativo do ThinDesk apresenta uma visão consolidada dos chamados técnicos. A interface permite acompanhar indicadores, comparar chamados recebidos e atendidos por dia, visualizar a distribuição por situação e tipo, consultar registros recentes e aplicar filtros por período, status, tipo e técnico.

A implementação analisada utiliza Spring Boot no backend, Spring Data MongoDB para acesso aos chamados, Thymeleaf para renderização da página e JavaScript para consumir a API e atualizar os componentes visuais. O endpoint específico do painel é GET /api/dashboard.

## Tecnologias

- Java 23
- Spring Boot 3.4
- Spring MVC e Thymeleaf
- Spring Security
- Spring Data MongoDB
- Maven Wrapper

## Pré-requisitos

- JDK 23 instalado e disponível no `PATH`
- MongoDB acessível pela aplicação

Por padrão, o perfil `local` usa `mongodb://localhost:27017/thindesk`. Inicie o MongoDB local antes de executar a aplicação. Para usar outra instância, configure a variável de ambiente `SPRING_DATA_MONGODB_URI` com a URI apropriada.

## Executar

Na raiz do projeto, use o comando correspondente ao seu sistema:

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

Após a inicialização, acesse <http://localhost:8080>. A aplicação encaminha usuários não autenticados para `/login`. As credenciais são definidas pela configuração do Spring Security; revise e altere os valores configurados antes de disponibilizar o sistema a outras pessoas.

Para gerar o pacote:

```powershell
.\mvnw.cmd clean package
```

O arquivo WAR é gerado em `target/`.

## Funcionalidades

- Dashboard de chamados por período, com indicadores, gráficos, filtros e chamados recentes.
- Consulta e gerenciamento de chamados e clientes.
- Cadastro e manutenção de técnicos, tipos de chamado, recursos e horários de atendimento.
- API REST para consulta do dashboard, chamados, clientes e técnicos.
- Criação automática das coleções do MongoDB e inclusão de dados iniciais quando as coleções correspondentes estão vazias.

## Endpoints principais

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/dashboard` | Dados do dashboard |
| `GET`, `POST` | `/api/chamados` | Consultar e criar chamados |
| `PUT` | `/api/chamados/{id}` | Atualizar um chamado |
| `GET`, `POST` | `/api/clientes` | Consultar e criar clientes |
| `GET`, `POST` | `/api/tecnicos` | Consultar e criar técnicos |
| `GET`, `PUT`, `DELETE` | `/api/tecnicos/{id}` | Consultar, atualizar e excluir um técnico |

O dashboard aceita os filtros opcionais `inicio` e `fim` (formato `AAAA-MM-DD`), `status`, `tipo` e `tecnico`. Os endpoints protegidos exigem autenticação.

## Estrutura do projeto

```text
src/main/java/com/pfc/                 Aplicação, configuração e código Java
src/main/java/com/pfc/thindesk/        Controllers, entidades, repositórios e serviços
src/main/resources/templates/          Páginas Thymeleaf
src/main/resources/static/             CSS, JavaScript e imagens
src/main/resources/application*.properties
                                        Configuração da aplicação e do MongoDB
```

## Dashboard

Requisitos Funcionais:

- RF01 — Indicadores gerais de chamados técnicos
- RF02 — Análise de chamados atendidos por período (gráficos)
- RF03 — Situação dos chamados
- RF04 — Tipos de Chamados mais atendidos
- RF05 — Filtros de consulta 
- RF06 — Integração com a API 
- RF07 — Atualização e tratamento de dados 

Requisitos não funcionais:
- RNF01 — Desempenho
- RNF02 — Usabilidade
- RNF03 — Compatibilidade
- RNF04 — Segurança
- RNF05 — Integridade e consistência
- RNF06 — Tratamento de falhas
- RNF07 — Manutenibilidade
- RNF08 — Atualização
