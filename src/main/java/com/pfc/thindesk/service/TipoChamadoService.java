package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TipoChamadoService {

    private final TipoChamadoRepository tipoChamadoRepository;

    public TipoChamadoService(TipoChamadoRepository tipoChamadoRepository) {
        this.tipoChamadoRepository = tipoChamadoRepository;
    }

    public List<TipoChamado> listarTodos() {
        return tipoChamadoRepository.findAll();
    }

    public TipoChamado buscarPorId(String id) {
        return tipoChamadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de chamado não encontrado."));
    }

    public TipoChamado salvar(TipoChamado tipoChamado) {
        validar(tipoChamado);
        return tipoChamadoRepository.save(tipoChamado);
    }

    public TipoChamado atualizar(String id, TipoChamado dadosAtualizados) {
        validar(dadosAtualizados);
        TipoChamado tipoChamado = buscarPorId(id);
        tipoChamado.setNome(dadosAtualizados.getNome());
        tipoChamado.setDescricao(dadosAtualizados.getDescricao());
        return tipoChamadoRepository.save(tipoChamado);
    }

    public void deletar(String id) {
        tipoChamadoRepository.delete(buscarPorId(id));
    }

    private void validar(TipoChamado tipoChamado) {
        if (tipoChamado == null || !StringUtils.hasText(tipoChamado.getNome())) {
            throw new IllegalArgumentException("O nome do tipo de chamado é obrigatório.");
        }
    }
}
