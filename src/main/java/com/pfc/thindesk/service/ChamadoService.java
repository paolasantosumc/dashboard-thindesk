package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Date;
import com.pfc.thindesk.repository.ChamadoRepository;

@Service
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    public Chamado criarChamado(Chamado chamado) {
        Date criadoEm = new Date();
        chamado.setDataCriacao(criadoEm);
        chamado.setDataAtendimento(isFinalizado(chamado.getStatus()) ? criadoEm : null);
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamados() {
        return chamadoRepository.findAll();
    }

    public Chamado atualizarChamado(String id, Chamado chamadoAtualizado) {
        return chamadoRepository.findById(id)
                .map(chamado -> {
                    boolean jaFinalizado = isFinalizado(chamado.getStatus());
                    boolean seraFinalizado = isFinalizado(chamadoAtualizado.getStatus());
                    chamado.setDescricao(chamadoAtualizado.getDescricao());
                    chamado.setStatus(chamadoAtualizado.getStatus());
                    chamado.setTipo(chamadoAtualizado.getTipo());
                    chamado.setTecnico(chamadoAtualizado.getTecnico());
                    chamado.setUsuario(chamadoAtualizado.getUsuario());
                    chamado.setConsideracoesTecnicas(chamadoAtualizado.getConsideracoesTecnicas());
                    if (seraFinalizado && !jaFinalizado) {
                        chamado.setDataAtendimento(new Date());
                    } else if (!seraFinalizado) {
                        chamado.setDataAtendimento(null);
                    }
                    return chamadoRepository.save(chamado);
                })
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado com id: " + id));
    }

    private boolean isFinalizado(String status) {
        if (status == null) {
            return false;
        }
        String normalizado = java.text.Normalizer.normalize(
                status.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
        return normalizado.contains("fech") || normalizado.contains("conclu")
                || normalizado.contains("resol") || normalizado.contains("finaliz")
                || normalizado.contains("atendido");
    }

}
