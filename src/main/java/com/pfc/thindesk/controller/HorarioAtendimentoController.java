package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/ajustes-horarios")
public class HorarioAtendimentoController {

    private final HorarioAtendimentoService horarioAtendimentoService;

    public HorarioAtendimentoController(HorarioAtendimentoService horarioAtendimentoService) {
        this.horarioAtendimentoService = horarioAtendimentoService;
    }

    @GetMapping
    public String listarHorarios(Model model) {
        return exibirPagina(model, new HorarioAtendimento(), null);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        return exibirPagina(model, horarioAtendimentoService.buscarPorId(id), null);
    }

    @PostMapping("/salvar")
    public String salvarHorario(@ModelAttribute HorarioAtendimento horario, Model model) {
        String erro = validar(horario);
        if (erro != null) {
            return exibirPagina(model, horario, erro);
        }

        if (StringUtils.hasText(horario.getId())) {
            horarioAtendimentoService.atualizar(horario.getId(), horario);
        } else {
            horarioAtendimentoService.salvar(horario);
        }
        return "redirect:/ajustes-horarios";
    }

    @PostMapping("/deletar/{id}")
    public String deletarHorario(@PathVariable String id) {
        horarioAtendimentoService.deletar(id);
        return "redirect:/ajustes-horarios";
    }

    private String exibirPagina(Model model, HorarioAtendimento horario, String erro) {
        model.addAttribute("horarios", horarioAtendimentoService.listarTodos());
        model.addAttribute("novoHorario", horario);
        model.addAttribute("erro", erro);
        return "ajustes-horarios";
    }

    private String validar(HorarioAtendimento horario) {
        if (horario == null || !StringUtils.hasText(horario.getSetor())
                || !StringUtils.hasText(horario.getDiaSemana())
                || !StringUtils.hasText(horario.getHorarioInicio())
                || !StringUtils.hasText(horario.getHorarioFim())) {
            return "Preencha todos os campos do horário.";
        }
        try {
            if (!LocalTime.parse(horario.getHorarioInicio()).isBefore(LocalTime.parse(horario.getHorarioFim()))) {
                return "O horário de início deve ser anterior ao horário de fim.";
            }
        } catch (DateTimeParseException exception) {
            return "Informe horários válidos.";
        }
        return null;
    }
}
