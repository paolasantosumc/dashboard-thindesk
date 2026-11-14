package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Tecnico;
import com.pfc.thindesk.repository.TecnicoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAll();
    }

    public Tecnico buscarPorId(String id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Técnico não encontrado."));
    }

    public Tecnico salvar(Tecnico tecnico) {
        if (tecnico == null || !StringUtils.hasText(tecnico.getNome())) {
            throw new IllegalArgumentException("O nome do técnico é obrigatório.");
        }
        return tecnicoRepository.save(tecnico);
    }

    public Tecnico atualizar(String id, Tecnico dadosAtualizados) {
        if (dadosAtualizados == null || !StringUtils.hasText(dadosAtualizados.getNome())) {
            throw new IllegalArgumentException("O nome do técnico é obrigatório.");
        }

        Tecnico tecnico = buscarPorId(id);
        tecnico.setNome(dadosAtualizados.getNome());
        tecnico.setTelefone(dadosAtualizados.getTelefone());
        tecnico.setEmail(dadosAtualizados.getEmail());
        tecnico.setEspecialidade(dadosAtualizados.getEspecialidade());
        return tecnicoRepository.save(tecnico);
    }

    public void deletar(String id) {
        tecnicoRepository.delete(buscarPorId(id));
    }
}
