package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Recurso;
import com.pfc.thindesk.service.RecursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recursos")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @GetMapping
    public String listar(Model model) {
        Recurso recurso = new Recurso();
        recurso.setQuantidade(0);
        return exibirPagina(model, recurso, null);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        return exibirPagina(model, recursoService.buscarPorId(id), null);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Recurso recurso, Model model) {
        if (!StringUtils.hasText(recurso.getNome())) {
            return exibirPagina(model, recurso, "O nome do recurso é obrigatório.");
        }
        if (recurso.getQuantidade() == null || recurso.getQuantidade() < 0) {
            return exibirPagina(model, recurso, "A quantidade deve ser zero ou maior.");
        }

        if (StringUtils.hasText(recurso.getId())) {
            recursoService.atualizar(recurso.getId(), recurso);
        } else {
            recursoService.salvar(recurso);
        }
        return "redirect:/recursos";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable String id) {
        recursoService.deletar(id);
        return "redirect:/recursos";
    }

    private String exibirPagina(Model model, Recurso recurso, String erro) {
        model.addAttribute("recursos", recursoService.listarTodos());
        model.addAttribute("recurso", recurso);
        model.addAttribute("erro", erro);
        return "recursos";
    }
}
