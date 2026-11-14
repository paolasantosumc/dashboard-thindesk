package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.repository.HorarioAtendimentoRepository;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HorarioAtendimentoService {

    private final HorarioAtendimentoRepository horarioAtendimentoRepository;

    public HorarioAtendimentoService(HorarioAtendimentoRepository horarioAtendimentoRepository) {
        this.horarioAtendimentoRepository = horarioAtendimentoRepository;
    }

    public List<HorarioAtendimento> listarTodos() {
        return horarioAtendimentoRepository.findAll();
    }

    public List<HorarioAtendimento> listarPorSetor(String setor) {
        return horarioAtendimentoRepository.findBySetor(setor);
    }

    public HorarioAtendimento salvar(HorarioAtendimento horarioAtendimento) {
        validar(horarioAtendimento);
        return horarioAtendimentoRepository.save(horarioAtendimento);
    }

    public HorarioAtendimento buscarPorId(String id) {
        return horarioAtendimentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado."));
    }

    public HorarioAtendimento atualizar(String id, HorarioAtendimento dadosAtualizados) {
        validar(dadosAtualizados);
        HorarioAtendimento horario = buscarPorId(id);
        horario.setSetor(dadosAtualizados.getSetor());
        horario.setDiaSemana(dadosAtualizados.getDiaSemana());
        horario.setHorarioInicio(dadosAtualizados.getHorarioInicio());
        horario.setHorarioFim(dadosAtualizados.getHorarioFim());
        return horarioAtendimentoRepository.save(horario);
    }

    public void deletar(String id) {
        horarioAtendimentoRepository.delete(buscarPorId(id));
    }

    private void validar(HorarioAtendimento horario) {
        if (horario == null || !StringUtils.hasText(horario.getSetor())
                || !StringUtils.hasText(horario.getDiaSemana())
                || !StringUtils.hasText(horario.getHorarioInicio())
                || !StringUtils.hasText(horario.getHorarioFim())) {
            throw new IllegalArgumentException("Preencha todos os campos do horário.");
        }
        try {
            if (!LocalTime.parse(horario.getHorarioInicio()).isBefore(LocalTime.parse(horario.getHorarioFim()))) {
                throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de fim.");
            }
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Informe horários válidos.", exception);
        }
    }
}
