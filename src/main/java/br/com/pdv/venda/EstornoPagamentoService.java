package br.com.pdv.venda;

import br.com.pdv.caixa.Caixa;
import br.com.pdv.caixa.CaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EstornoPagamentoService {

    private final EstornoPagamentoRepository estornoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CaixaRepository caixaRepository;

    public EstornoPagamentoService(EstornoPagamentoRepository estornoRepository, PagamentoRepository pagamentoRepository, CaixaRepository caixaRepository) {
        this.estornoRepository = estornoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.caixaRepository = caixaRepository;
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
    public EstornoPagamento concluirEstorno(Long estornoId, Long caixaId) {

        EstornoPagamento estorno = estornoRepository.findById(estornoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Estorno não encontrado"));

        if (estorno.getStatus() == StatusEstorno.CONCLUIDO) {
            throw new IllegalArgumentException("Este estorno já foi concluido");
        }

        Caixa caixa = caixaRepository.findById(caixaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Caixa não encontrado"));

        if (!Boolean.TRUE.equals(caixa.getAberto())) {
            throw new IllegalArgumentException(
                    "Não é possível concluir estorno em um caixa fechado"
            );
        }

        estorno.setCaixaEstorno(caixa);
        estorno.setStatus(StatusEstorno.CONCLUIDO);
        estorno.setDataConclusao(LocalDateTime.now());

        return estornoRepository.save(estorno);
    }
}
