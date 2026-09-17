package br.com.pdv.caixa;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoCaixaService {

    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final CaixaRepository caixaRepository;

    public MovimentacaoCaixaService(
            MovimentacaoCaixaRepository movimentacaoCaixaRepository,
            CaixaRepository caixaRepository) {


        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.caixaRepository = caixaRepository;
    }

    public MovimentacaoCaixa registrarSuprimento(
            BigDecimal valor,
            String descricao) {

        Caixa caixa = caixaRepository.findByAbertoTrue()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe caixa aberto"
                        )
                );

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor do suprimento deve ser maior que zero"
            );
        }

        MovimentacaoCaixa movimentacao = new MovimentacaoCaixa();

        movimentacao.setCaixa(caixa);
        movimentacao.setTipo(TipoMovimentacaoCaixa.SUPRIMENTO);
        movimentacao.setValor(valor);
        movimentacao.setDescricao(descricao);
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoCaixaRepository.save(movimentacao);
    }

    public MovimentacaoCaixa registrarSangria(
            BigDecimal valor,
            String descricao) {

        Caixa caixa = caixaRepository.findByAbertoTrue()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe caixa aberto"
                        )
                );

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor da sangria deve ser maior que zero"
            );

        }

        MovimentacaoCaixa movimentacao = new MovimentacaoCaixa();

        movimentacao.setCaixa(caixa);
        movimentacao.setTipo(TipoMovimentacaoCaixa.SANGRIA);
        movimentacao.setValor(valor);
        movimentacao.setDescricao(descricao);
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoCaixaRepository.save(movimentacao);
    }

    public List<MovimentacaoCaixa> listarMovimentacoesCaixaAberto() {

        Caixa caixa = caixaRepository.findByAbertoTrue()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe caixa aberto"
                        )
                );

        return movimentacaoCaixaRepository
                .findByCaixaIdOrderByDataHora(caixa.getId());
    }
}
