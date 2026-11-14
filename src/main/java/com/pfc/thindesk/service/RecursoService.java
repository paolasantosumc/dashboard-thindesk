package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Recurso;
import com.pfc.thindesk.repository.RecursoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoService(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    public List<Recurso> listarTodos() {
        return recursoRepository.findAll();
    }

    public Recurso buscarPorId(String id) {
        return recursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado."));
    }

    public Recurso salvar(Recurso recurso) {
        validar(recurso);
        return recursoRepository.save(recurso);
    }

    public Recurso atualizar(String id, Recurso dadosAtualizados) {
        validar(dadosAtualizados);
        Recurso recurso = buscarPorId(id);
        recurso.setNome(dadosAtualizados.getNome());
        recurso.setDescricao(dadosAtualizados.getDescricao());
        recurso.setQuantidade(dadosAtualizados.getQuantidade());
        return recursoRepository.save(recurso);
    }

    public void deletar(String id) {
        recursoRepository.delete(buscarPorId(id));
    }

    private void validar(Recurso recurso) {
        if (recurso == null || !StringUtils.hasText(recurso.getNome())) {
            throw new IllegalArgumentException("O nome do recurso é obrigatório.");
        }
        if (recurso.getQuantidade() == null || recurso.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade deve ser zero ou maior.");
        }
    }
}
