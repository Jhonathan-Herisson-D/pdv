package br.com.pdv.caixa;

import br.com.pdv.venda.Pagamento;
import br.com.pdv.venda.PagamentoRepository;
import br.com.pdv.venda.TipoPagamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    public Optional<Caixa> fecharCaixaAberto(BigDecimal saldoFinal) {

        Optional<Caixa> caixaAberto = caixaRepository.findByAbertoTrue();

        if (caixaAberto.isEmpty()) {
            return Optional.empty();
        }

        if (saldoFinal == null || saldoFinal.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "O saldo final não pode ser negativo"
            );
        }

        Caixa caixa = caixaAberto.get();
        BigDecimal saldoEsperado = calcularSaldoEsperado();
        BigDecimal diferenca = saldoFinal.subtract(saldoEsperado);

        caixa.setSaldoFinal(saldoFinal);
        caixa.setDiferenca(diferenca);

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
                pagamentoRepository.somarPorCaixaETipo(
                        caixa.getId(),
                        TipoPagamento.DINHEIRO);


        return caixa.getSaldoInicial()
                .add(suprimento)
                .add(totalDinheiro)
                .subtract(sangrias);
    }

    public ExtratoCaixa gerarExtrato(Long caixaId) {

        Caixa caixa = caixaRepository.findById(caixaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Caixa não encontrado")
                );

        // Totais por forma de pagamento
        BigDecimal totalDinheiro =
                pagamentoRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.DINHEIRO
                );

        BigDecimal totalPix =
                pagamentoRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.PIX
                );

        BigDecimal totalDebito =
                pagamentoRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.CARTAO_DEBITO
                );

        BigDecimal totalCredito =
                pagamentoRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.CARTAO_CREDITO
                );

        // Quantidade de pagamento por forma
        Long quantidadeDinheiro  =
                pagamentoRepository.contarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.DINHEIRO
                );

        Long quantidadePix =
                pagamentoRepository.contarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.PIX
                );

        Long quantidadeDebito =
                pagamentoRepository.contarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.CARTAO_DEBITO
                );

        Long quantidadeCredito =
                pagamentoRepository.contarPorCaixaETipo(
                        caixaId,
                        TipoPagamento.CARTAO_CREDITO
                );

        // Movimentações de dinheiro do caixa
        BigDecimal suprimentos =
                movimentacaoCaixaRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoMovimentacaoCaixa.SUPRIMENTO
                );

        BigDecimal sangrias =
                movimentacaoCaixaRepository.somarPorCaixaETipo(
                        caixaId,
                        TipoMovimentacaoCaixa.SANGRIA
                );

        // Total geral recebido em vendas
        BigDecimal totalVendas =
                totalDinheiro
                        .add(totalPix)
                        .add(totalDebito)
                        .add(totalCredito);

        // Saldo físico esperado na gaveta.
        // PIX, débito e crédito não entram no dinheiro físico.
        BigDecimal saldoEsperado =
                caixa.getSaldoInicial()
                        .add(suprimentos)
                        .add(totalDinheiro)
                        .subtract(sangrias);

        //Busca os pagamentos para montar os detalhes do extrato
        List<Pagamento> pagamentos =
                pagamentoRepository.buscarPagamentosPorCaixa(caixaId);

        List<DetalhePagamentoCaixa> detalhes =
                pagamentos.stream()
                        .map(pagamento -> {

                            DetalhePagamentoCaixa detalhe =
                                    new DetalhePagamentoCaixa();
                            detalhe.setPagamentoId(pagamento.getId());
                            detalhe.setVendaId(pagamento.getVenda().getId());
                            detalhe.setDataHora(pagamento.getDataHora());
                            detalhe.setTipo(pagamento.getTipo());
                            detalhe.setValor(pagamento.getValor());
                            detalhe.setValorRecebido(pagamento.getValorRecebido());
                            detalhe.setTroco(pagamento.getTroco());

                            return detalhe;
                        })
                        .toList();
        // Montagem do extrato
        ExtratoCaixa extrato = new ExtratoCaixa();

        extrato.setCaixaId(caixa.getId());
        extrato.setAberto(caixa.getAberto());
        extrato.setDataAbertura(caixa.getDataAbertura());
        extrato.setDataFechamento(caixa.getDataFechamento());

        extrato.setQuantidadeDinheiro(quantidadeDinheiro);
        extrato.setTotalDinheiro(totalDinheiro);

        extrato.setQuantidadePix(quantidadePix);
        extrato.setTotalPix(totalPix);

        extrato.setQuantidadeCartaoDebito(quantidadeDebito);
        extrato.setTotalCartaoDebito(totalDebito);

        extrato.setQuantidadeCartaoCredito(quantidadeCredito);
        extrato.setTotalCartaoCredito(totalCredito);

        extrato.setTotalVendas(totalVendas);

        extrato.setSaldoInicial(caixa.getSaldoInicial());
        extrato.setTotalSuprimentos(suprimentos);
        extrato.setTotalSangrias(sangrias);
        extrato.setSaldoEsperado(saldoEsperado);
        extrato.setSaldoFinal(caixa.getSaldoFinal());
        extrato.setDiferenca(caixa.getDiferenca());

        extrato.setPagamentos(detalhes);

        return extrato;
    }

    public List<Caixa> listarHistorico() {
        return caixaRepository.findAllByOrderByDataAberturaDesc();
    }
}
