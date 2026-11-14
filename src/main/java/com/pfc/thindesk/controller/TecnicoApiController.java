package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Tecnico;
import com.pfc.thindesk.service.TecnicoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoApiController {

    private final TecnicoService tecnicoService;

    public TecnicoApiController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public List<Tecnico> listar() {
        return tecnicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Tecnico buscar(@PathVariable String id) {
        return tecnicoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Tecnico> criar(@RequestBody Tecnico tecnico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tecnicoService.salvar(tecnico));
    }

    @PutMapping("/{id}")
    public Tecnico atualizar(@PathVariable String id, @RequestBody Tecnico tecnico) {
        return tecnicoService.atualizar(id, tecnico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        tecnicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarDadosInvalidos(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
