package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Tecnico;
import com.pfc.thindesk.service.TecnicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public String listar(Model model) {
        return exibirPagina(model, new Tecnico(), null);
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return exibirPagina(model, new Tecnico(), null);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        return exibirPagina(model, tecnicoService.buscarPorId(id), null);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Tecnico tecnico, Model model) {
        if (!StringUtils.hasText(tecnico.getNome())) {
            return exibirPagina(model, tecnico, "O nome do técnico é obrigatório.");
        }

        if (StringUtils.hasText(tecnico.getId())) {
            tecnicoService.atualizar(tecnico.getId(), tecnico);
        } else {
            tecnicoService.salvar(tecnico);
        }
        return "redirect:/tecnicos";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable String id) {
        tecnicoService.deletar(id);
        return "redirect:/tecnicos";
    }

    private String exibirPagina(Model model, Tecnico tecnico, String erro) {
        model.addAttribute("tecnicos", tecnicoService.listarTodos());
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("erro", erro);
        return "tecnicos";
    }
}
