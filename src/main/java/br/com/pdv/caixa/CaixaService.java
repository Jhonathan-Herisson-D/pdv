package br.com.pdv.caixa;

import br.com.pdv.venda.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final PagamentoRepository pagamentoRepository;

    public CaixaService(
            CaixaRepository caixaRepository,
            MovimentacaoCaixaRepository movimentacaoCaixaRepository,
            PagamentoRepository pagamentoRepository) {

        this.caixaRepository = caixaRepository;
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public Caixa abrir(BigDecimal saldoInicial) {

        if (caixaRepository.findByAbertoTrue().isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe um caixa aberto"
            );
        }

        Caixa caixa = new Caixa();
        caixa.setSaldoInicial(saldoInicial);
        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setAberto(true);

        return caixaRepository.save(caixa);
    }

    public Optional<Caixa> buscarCaixaAberto() {
        return caixaRepository.findByAbertoTrue();
    }

    public Optional<Caixa> fecharCaixaAberto() {

        Optional<Caixa> caixaAberto = caixaRepository.findByAbertoTrue();

        if (caixaAberto.isEmpty()) {
            return Optional.empty();
        }

        Caixa caixa = caixaAberto.get();

        caixa.setAberto(false);
        caixa.setDataFechamento(LocalDateTime.now());

        Caixa caixaFechado = caixaRepository.save(caixa);

        return Optional.of(caixaFechado);
    }

    public BigDecimal calcularSaldoEsperado() {

        Caixa caixa = caixaRepository.findByAbertoTrue()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe caixa aberto"
                        )
                );

        BigDecimal suprimento =
                movimentacaoCaixaRepository.somarPorCaixaETipo(
                        caixa.getId(),
                        TipoMovimentacaoCaixa.SUPRIMENTO
                );

        BigDecimal sangrias =
                movimentacaoCaixaRepository.somarPorCaixaETipo(
                        caixa.getId(),
                        TipoMovimentacaoCaixa.SANGRIA
                );

        BigDecimal totalDinheiro =
                pagamentoRepository.somarDinheiroPorCaixa(caixa.getId());


        return caixa.getSaldoInicial()
                .add(suprimento)
                .add(totalDinheiro)
                .subtract(sangrias);
    }
}
