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
            BigDecimal valor) {

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

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor do pagamento deve ser maior que zero"
            );
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setVenda(venda);
        pagamento.setTipo(tipo);
        pagamento.setValor(valor);
        pagamento.setDataHora(LocalDateTime.now());

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
