package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DashboardService {

    private static final ZoneId ZONE = ZoneId.systemDefault();
    private final ChamadoRepository chamadoRepository;

    public DashboardService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public DashboardResponse obterDashboard(
            LocalDate inicio, LocalDate fim, String status, String tipo, String tecnico) {
        LocalDate hoje = LocalDate.now(ZONE);
        LocalDate dataInicio = inicio == null ? hoje.minusDays(29) : inicio;
        LocalDate dataFim = fim == null ? hoje : fim;
        if (dataInicio.isAfter(dataFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data inicial não pode ser posterior à data final.");
        }

        List<Chamado> chamados = chamadoRepository.findAll();
        List<String> statuses = opcoes(chamados, Chamado::getStatus);
        List<String> tipos = opcoes(chamados, Chamado::getTipo);
        List<String> tecnicos = opcoes(chamados, Chamado::getTecnico);

        List<Chamado> comFiltros = chamados.stream()
                .filter(chamado -> corresponde(chamado.getStatus(), status))
                .filter(chamado -> corresponde(chamado.getTipo(), tipo))
                .filter(chamado -> corresponde(chamado.getTecnico(), tecnico))
                .toList();
        long semData = comFiltros.stream().filter(chamado -> chamado.getDataCriacao() == null).count();
        List<Chamado> noPeriodo = comFiltros.stream()
                .filter(chamado -> noPeriodo(chamado.getDataCriacao(), dataInicio, dataFim))
                .toList();

        long abertos = noPeriodo.stream().filter(chamado -> categoria(chamado.getStatus()).equals("aberto")).count();
        long emAndamento = noPeriodo.stream()
                .filter(chamado -> categoria(chamado.getStatus()).equals("andamento")).count();
        long atendidos = comFiltros.stream()
                .filter(chamado -> noPeriodo(chamado.getDataAtendimento(), dataInicio, dataFim))
                .count();
        long cancelados = noPeriodo.stream()
                .filter(chamado -> categoria(chamado.getStatus()).equals("cancelado")).count();
        long finalizadosSemDataAtendimento = comFiltros.stream()
                .filter(chamado -> categoria(chamado.getStatus()).equals("finalizado"))
                .filter(chamado -> chamado.getDataAtendimento() == null)
                .count();

        Map<LocalDate, Long> recebidosPorDia = noPeriodo.stream()
                .filter(chamado -> chamado.getDataCriacao() != null)
                .collect(Collectors.groupingBy(
                        chamado -> data(chamado.getDataCriacao()), TreeMap::new, Collectors.counting()));
        Map<LocalDate, Long> atendidosPorDia = comFiltros.stream()
                .filter(chamado -> chamado.getDataAtendimento() != null)
                .filter(chamado -> noPeriodo(chamado.getDataAtendimento(), dataInicio, dataFim))
                .collect(Collectors.groupingBy(
                        chamado -> data(chamado.getDataAtendimento()), TreeMap::new, Collectors.counting()));
        List<DashboardResponse.SerieDiaria> serie = new ArrayList<>();
        for (LocalDate dia = dataInicio; !dia.isAfter(dataFim); dia = dia.plusDays(1)) {
            serie.add(new DashboardResponse.SerieDiaria(
                    dia, recebidosPorDia.getOrDefault(dia, 0L), atendidosPorDia.getOrDefault(dia, 0L)));
        }

        List<DashboardResponse.Distribuicao> porStatus = distribuicao(noPeriodo, Chamado::getStatus);
        List<DashboardResponse.Distribuicao> porTipo = distribuicao(noPeriodo, Chamado::getTipo);
        List<DashboardResponse.ChamadoRecente> recentes = noPeriodo.stream()
                .sorted(Comparator.comparing(Chamado::getDataCriacao,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .map(chamado -> new DashboardResponse.ChamadoRecente(
                        chamado.getId(), chamado.getDescricao(), chamado.getStatus(), chamado.getTipo(),
                        chamado.getTecnico(), chamado.getConsideracoesTecnicas(),
                        chamado.getDataCriacao() == null ? null : chamado.getDataCriacao().toInstant()))
                .toList();

        return new DashboardResponse(
                dataInicio, dataFim, Instant.now(),
                new DashboardResponse.Indicadores(noPeriodo.size(), abertos, emAndamento, atendidos, cancelados),
                serie, porStatus, porTipo, statuses, tipos, tecnicos, semData,
                finalizadosSemDataAtendimento, recentes);
    }

    private List<String> opcoes(List<Chamado> chamados, Function<Chamado, String> valor) {
        return chamados.stream().map(valor).filter(StringUtils::hasText)
                .map(String::trim).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    private List<DashboardResponse.Distribuicao> distribuicao(
            List<Chamado> chamados, Function<Chamado, String> valor) {
        Map<String, Long> contagem = chamados.stream()
                .collect(Collectors.groupingBy(chamado -> StringUtils.hasText(valor.apply(chamado))
                                ? valor.apply(chamado).trim() : "Não informado",
                        Collectors.counting()));
        return contagem.entrySet().stream()
                .map(entry -> new DashboardResponse.Distribuicao(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(DashboardResponse.Distribuicao::quantidade).reversed()
                        .thenComparing(DashboardResponse.Distribuicao::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private boolean corresponde(String valor, String filtro) {
        return !StringUtils.hasText(filtro)
                || normalizar(valor).equals(normalizar(filtro));
    }

    private boolean noPeriodo(Date data, LocalDate inicio, LocalDate fim) {
        if (data == null) {
            return false;
        }
        LocalDate dia = data(data);
        return !dia.isBefore(inicio) && !dia.isAfter(fim);
    }

    private LocalDate data(Date data) {
        return data.toInstant().atZone(ZONE).toLocalDate();
    }

    private String categoria(String status) {
        String normalizado = normalizar(status);
        if (normalizado.contains("cancel")) {
            return "cancelado";
        }
        if (normalizado.contains("fech") || normalizado.contains("conclu")
                || normalizado.contains("resol") || normalizado.contains("finaliz")
                || normalizado.contains("atendido")) {
            return "finalizado";
        }
        if (normalizado.contains("andamento") || normalizado.contains("atendimento")) {
            return "andamento";
        }
        return "aberto";
    }

    private String normalizar(String valor) {
        if (!StringUtils.hasText(valor)) {
            return "";
        }
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
