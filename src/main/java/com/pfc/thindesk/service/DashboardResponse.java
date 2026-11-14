package com.pfc.thindesk.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        LocalDate inicio,
        LocalDate fim,
        Instant atualizadoEm,
        Indicadores indicadores,
        List<SerieDiaria> chamadosPorDia,
        List<Distribuicao> chamadosPorStatus,
        List<Distribuicao> tiposMaisAtendidos,
        List<String> statuses,
        List<String> tipos,
        List<String> tecnicos,
        long chamadosSemData,
        long chamadosFinalizadosSemDataAtendimento,
        List<ChamadoRecente> chamadosRecentes) {

    public record Indicadores(
            long total, long abertos, long emAndamento, long atendidos, long cancelados) {
    }

    public record SerieDiaria(LocalDate data, long recebidos, long atendidos) {
    }

    public record Distribuicao(String nome, long quantidade) {
    }

    public record ChamadoRecente(
            String id,
            String descricao,
            String status,
            String tipo,
            String tecnico,
            String consideracoesTecnicas,
            Instant dataCriacao) {
    }
}
