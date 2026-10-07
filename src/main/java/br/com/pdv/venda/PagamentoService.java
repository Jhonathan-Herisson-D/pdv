package br.com.pdv.venda;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final VendaRepository vendaRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            VendaRepository vendaRepository) {

        this.pagamentoRepository = pagamentoRepository;
        this.vendaRepository = vendaRepository;
    }

    public Pagamento registrar(
            Long vendaId,
            TipoPagamento tipo,
            BigDecimal valor,
            BigDecimal valorRecebido) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        )
                );

        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new IllegalArgumentException(
                    "Só é possível registrar pagamento em uma venda aberta"
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo de pagamento é obrigatório"
            );
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor do pagamento deve ser maior que zero"
            );
        }

        BigDecimal totalPago =
                pagamentoRepository.somarPagamentosPorVenda(vendaId);

        BigDecimal valorRestante =
                venda.getTotal().subtract(totalPago);

        if (valorRestante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A venda já está totalmente paga"
            );
        }

        if (valor.compareTo(valorRestante) > 0) {
            throw new IllegalArgumentException(
                    "O pagamento não pode ser maior que o valor restante da venda"
            );
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setVenda(venda);
        pagamento.setTipo(tipo);
        pagamento.setValor(valor);
        pagamento.setDataHora(LocalDateTime.now());

        if (tipo == TipoPagamento.DINHEIRO) {

            if (valorRecebido == null ||
                    valorRecebido.compareTo(valor) < 0) {

                throw new IllegalArgumentException(
                        "O valor recebido deve ser igual ou maior que o valor do pagamento"
                );
            }

            BigDecimal troco =
                    valorRecebido.subtract(valor);

            pagamento.setValorRecebido(valorRecebido);
            pagamento.setTroco(troco);

        } else {

            pagamento.setValorRecebido(null);
            pagamento.setTroco(null);
        }

        return pagamentoRepository.save(pagamento);
    }

    public List<Pagamento> listaPorVenda(Long vendaId) {

        if (!vendaRepository.existsById(vendaId)) {
            throw new IllegalArgumentException(
                    "Venda não encontrada"
            );
        }

        return pagamentoRepository.findByVendaIdOrderByIdAsc(vendaId);
    }
}
