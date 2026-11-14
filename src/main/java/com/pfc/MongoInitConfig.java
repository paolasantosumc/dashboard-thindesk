package com.pfc;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.entity.Recurso;
import com.pfc.thindesk.entity.Tecnico;
import com.pfc.thindesk.entity.TipoChamado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoInitConfig implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (!mongoTemplate.collectionExists("chamados")) {
            mongoTemplate.createCollection("chamados");
        }
        if (!mongoTemplate.collectionExists("clientes")) {
            mongoTemplate.createCollection("clientes");
        }
        if (!mongoTemplate.collectionExists("tecnicos")) {
            mongoTemplate.createCollection("tecnicos");
        }
        if (!mongoTemplate.collectionExists("usuarios")) {
            mongoTemplate.createCollection("usuarios");
        }
        if (!mongoTemplate.collectionExists("horariosAtendimento")) {
            mongoTemplate.createCollection("horariosAtendimento");
        }
        if (!mongoTemplate.collectionExists("tiposChamado")) {
            mongoTemplate.createCollection("tiposChamado");
        }
        if (!mongoTemplate.collectionExists("recursos")) {
            mongoTemplate.createCollection("recursos");
        }

        if (mongoTemplate.count(new Query(), Cliente.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarCliente("Ana Souza", "(11) 0000-0001", "Financeiro"),
                    criarCliente("Bruno Lima", "(11) 0000-0002", "Recursos Humanos"),
                    criarCliente("Carla Mendes", "(11) 0000-0003", "Vendas"),
                    criarCliente("Diego Alves", "(11) 0000-0004", "Operações"),
                    criarCliente("Elisa Costa", "(11) 0000-0005", "Marketing")
            ));
        }

        if (mongoTemplate.count(new Query(), Chamado.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarChamado("Computador não inicia após atualização.", "ABERTO", "Hardware", "Rafael", "Ana Souza", 0, null),
                    criarChamado("Solicitação de acesso ao sistema de folha.", "EM_ANDAMENTO", "Acesso", "Marina", "Bruno Lima", 1, "Validando as permissões com o gestor do setor."),
                    criarChamado("Impressora do setor está sem conexão.", "ABERTO", "Impressora", "Rafael", "Carla Mendes", 2, null),
                    criarChamado("Instalação do pacote de escritório.", "CONCLUIDO", "Software", "Marina", "Diego Alves", 3, "Pacote instalado e validado com o usuário."),
                    criarChamado("E-mail corporativo não sincroniza.", "EM_ANDAMENTO", "Software", "Rafael", "Elisa Costa", 4, "Perfil de sincronização em análise."),
                    criarChamado("Solicitação de troca de teclado.", "ABERTO", "Hardware", "Marina", "Ana Souza", 5, null),
                    criarChamado("Conexão instável na sala de reuniões.", "CONCLUIDO", "Rede", "Rafael", "Bruno Lima", 6, "Ponto de rede testado; conexão restabelecida."),
                    criarChamado("Configuração de acesso à pasta compartilhada.", "CONCLUIDO", "Acesso", "Marina", "Carla Mendes", 7, "Permissão concedida conforme solicitação."),
                    criarChamado("Atualização do sistema de vendas.", "ABERTO", "Software", "Rafael", "Diego Alves", 8, null),
                    criarChamado("Monitor com imagem intermitente.", "EM_ANDAMENTO", "Hardware", "Marina", "Elisa Costa", 9, "Equipamento em diagnóstico."),
                    criarChamado("Instalação de impressora no financeiro.", "CONCLUIDO", "Impressora", "Rafael", "Ana Souza", 10, "Impressora instalada e página de teste aprovada."),
                    criarChamado("Solicitação de criação de usuário.", "ABERTO", "Acesso", "Marina", "Bruno Lima", 12, null)
            ));
        }

        if (mongoTemplate.count(new Query(), Tecnico.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarTecnico("Rafael", "(11) 4000-0101", "rafael@exemplo.com.br", "Hardware e redes"),
                    criarTecnico("Marina", "(11) 4000-0102", "marina@exemplo.com.br", "Sistemas e acessos"),
                    criarTecnico("Felipe", "(11) 4000-0103", "felipe@exemplo.com.br", "Suporte ao usuário")
            ));
        }

        if (mongoTemplate.count(new Query(), TipoChamado.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarTipoChamado("Hardware", "Computadores, periféricos e equipamentos."),
                    criarTipoChamado("Software", "Instalação, atualização e suporte a sistemas."),
                    criarTipoChamado("Acesso", "Contas, permissões e pastas compartilhadas."),
                    criarTipoChamado("Impressora", "Instalação e manutenção de impressoras."),
                    criarTipoChamado("Rede", "Conectividade, Wi-Fi e pontos de rede.")
            ));
        }

        if (mongoTemplate.count(new Query(), HorarioAtendimento.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarHorario("Suporte geral", "Segunda-feira", "08:00", "18:00"),
                    criarHorario("Suporte geral", "Terça-feira", "08:00", "18:00"),
                    criarHorario("Suporte geral", "Quarta-feira", "08:00", "18:00"),
                    criarHorario("Suporte geral", "Quinta-feira", "08:00", "18:00"),
                    criarHorario("Suporte geral", "Sexta-feira", "08:00", "17:00")
            ));
        }

        if (mongoTemplate.count(new Query(), Recurso.class) == 0) {
            mongoTemplate.insertAll(List.of(
                    criarRecurso("Notebook de empréstimo", "Equipamento disponível para empréstimo temporário.", 4),
                    criarRecurso("Monitor 24 polegadas", "Monitor reserva para substituição.", 6),
                    criarRecurso("Teclado USB", "Periférico padrão para estações de trabalho.", 12),
                    criarRecurso("Mouse USB", "Periférico padrão para estações de trabalho.", 10),
                    criarRecurso("Headset", "Headset para atendimento e reuniões.", 8)
            ));
        }

        preencherDatasDeChamadosSemData();
    }

    private Cliente criarCliente(String nome, String telefone, String setor) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setSetor(setor);
        return cliente;
    }

    private Chamado criarChamado(String descricao, String status, String tipo, String tecnico,
            String usuario, int diasAtras, String consideracoesTecnicas) {
        Chamado chamado = new Chamado();
        chamado.setDescricao(descricao);
        chamado.setStatus(status);
        chamado.setTipo(tipo);
        chamado.setTecnico(tecnico);
        chamado.setUsuario(usuario);
        chamado.setDataCriacao(dataDeApresentacao(diasAtras, 9 + diasAtras % 8));
        if (status.equals("CONCLUIDO")) {
            chamado.setDataAtendimento(dataDeApresentacao(Math.max(0, diasAtras - 1), 14));
        }
        chamado.setConsideracoesTecnicas(consideracoesTecnicas);
        return chamado;
    }

    private void preencherDatasDeChamadosSemData() {
        List<Chamado> chamadosSemData = mongoTemplate.find(
                Query.query(Criteria.where("dataCriacao").is(null)), Chamado.class);
        for (int indice = 0; indice < chamadosSemData.size(); indice++) {
            Chamado chamado = chamadosSemData.get(indice);
            Date dataCriacao = dataDeApresentacao(indice % 14, 9 + indice % 8);
            Update atualizacao = new Update().set("dataCriacao", dataCriacao);
            if (chamado.getStatus() != null && chamado.getStatus().equalsIgnoreCase("CONCLUIDO")
                    && chamado.getDataAtendimento() == null) {
                atualizacao.set("dataAtendimento", dataDeApresentacao(indice % 14, 14));
            }
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(chamado.getId())
                            .and("dataCriacao").is(null)),
                    atualizacao,
                    Chamado.class);
        }
    }

    private Date dataDeApresentacao(int diasAtras, int hora) {
        return Date.from(LocalDate.now().minusDays(diasAtras).atTime(LocalTime.of(hora, 0))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    private Tecnico criarTecnico(String nome, String telefone, String email, String especialidade) {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome(nome);
        tecnico.setTelefone(telefone);
        tecnico.setEmail(email);
        tecnico.setEspecialidade(especialidade);
        return tecnico;
    }

    private TipoChamado criarTipoChamado(String nome, String descricao) {
        TipoChamado tipo = new TipoChamado();
        tipo.setNome(nome);
        tipo.setDescricao(descricao);
        return tipo;
    }

    private HorarioAtendimento criarHorario(String setor, String diaSemana, String inicio, String fim) {
        HorarioAtendimento horario = new HorarioAtendimento();
        horario.setSetor(setor);
        horario.setDiaSemana(diaSemana);
        horario.setHorarioInicio(inicio);
        horario.setHorarioFim(fim);
        return horario;
    }

    private Recurso criarRecurso(String nome, String descricao, int quantidade) {
        Recurso recurso = new Recurso();
        recurso.setNome(nome);
        recurso.setDescricao(descricao);
        recurso.setQuantidade(quantidade);
        return recurso;
    }
}
