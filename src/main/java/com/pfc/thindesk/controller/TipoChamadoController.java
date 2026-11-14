package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.service.TipoChamadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tipo-chamado")
public class TipoChamadoController {

    private final TipoChamadoService tipoChamadoService;

    public TipoChamadoController(TipoChamadoService tipoChamadoService) {
        this.tipoChamadoService = tipoChamadoService;
    }

    @GetMapping
    public String listar(Model model) {
        return exibirPagina(model, new TipoChamado(), null);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        return exibirPagina(model, tipoChamadoService.buscarPorId(id), null);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute TipoChamado tipoChamado, Model model) {
        if (!StringUtils.hasText(tipoChamado.getNome())) {
            return exibirPagina(model, tipoChamado, "O nome do tipo de chamado é obrigatório.");
        }

        if (StringUtils.hasText(tipoChamado.getId())) {
            tipoChamadoService.atualizar(tipoChamado.getId(), tipoChamado);
        } else {
            tipoChamadoService.salvar(tipoChamado);
        }
        return "redirect:/tipo-chamado";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable String id) {
        tipoChamadoService.deletar(id);
        return "redirect:/tipo-chamado";
    }

    private String exibirPagina(Model model, TipoChamado tipoChamado, String erro) {
        model.addAttribute("tiposChamado", tipoChamadoService.listarTodos());
        model.addAttribute("tipoChamado", tipoChamado);
        model.addAttribute("erro", erro);
        return "tipo-chamado";
    }
}
