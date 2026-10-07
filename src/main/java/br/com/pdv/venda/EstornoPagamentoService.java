package br.com.pdv.venda;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EstornoPagamentoService {

    private final EstornoPagamentoRepository estornoRepository;
    private final PagamentoRepository pagamentoRepository;

    public EstornoPagamentoService(EstornoPagamentoRepository estornoRepository, PagamentoRepository pagamentoRepository) {
        this.estornoRepository = estornoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public EstornoPagamento solicitarEstorno(
            Long pagamentoId,
            String motivo) {

        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pagamento não encontrado"));

        if (pagamento.getVenda().getStatus() != StatusVenda.FINALIZADA) {
            throw new IllegalArgumentException("Somente pagamentos de vendas finalizadas podem ser estornadas");
        }

        if (estornoRepository.existsByPagamentoId(pagamentoId)) {
            throw new IllegalArgumentException("Este pagamento já possui um estorno");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo do estorno é obrigatório");
        }

        EstornoPagamento estorno = new EstornoPagamento();

        estorno.setPagamento(pagamento);
        estorno.setValor(pagamento.getValor());
        estorno.setStatus(StatusEstorno.PENDENTE);
        estorno.setDataSolicitacao(LocalDateTime.now());
        estorno.setMotivo(motivo);

        return estornoRepository.save(estorno);

    }

    @Transactional
    public EstornoPagamento concluirEstorno(Long estornoId) {

        EstornoPagamento estorno = estornoRepository.findById(estornoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Estorno não encontrado"));

        if (estorno.getStatus() == StatusEstorno.CONCLUIDO) {
            throw new IllegalArgumentException("Este estorno já foi concluido");
        }

        estorno.setStatus(StatusEstorno.CONCLUIDO);
        estorno.setDataConclusao(LocalDateTime.now());

        return estornoRepository.save(estorno);
    }
}
