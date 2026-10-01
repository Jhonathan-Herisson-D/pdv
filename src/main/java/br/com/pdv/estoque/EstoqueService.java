package br.com.pdv.estoque;

import br.com.pdv.produto.Produto;
import br.com.pdv.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            ProdutoRepository produtoRepository) {

        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    public MovimentacaoEstoque registrarEntrada(
            Long produtoId,
            BigDecimal quantidade,
            String descricao) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado"
                        )
                );

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero"
            );
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setProduto(produto);
        movimentacao.setTipo(TipoMovimentacaoEstoque.ENTRADA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDescricao(descricao);
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    public MovimentacaoEstoque registrarSaida(
            Long produtoId,
            BigDecimal quantidade,
            String descricao) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado"
                        )
                );

        if (quantidade == null ||
                quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero"
            );
        }

        BigDecimal saldoAtual =
                movimentacaoEstoqueRepository.calcularSaldo(produtoId);

        if (quantidade.compareTo(saldoAtual) > 0) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente. Saldo disponível: "
                            + saldoAtual
            );
        }

        MovimentacaoEstoque movimentacao =
                new MovimentacaoEstoque();

        movimentacao.setProduto(produto);
        movimentacao.setTipo(
                TipoMovimentacaoEstoque.SAIDA
        );
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDescricao(descricao);
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    public BigDecimal consultarSaldo(Long produtoId) {

        if (!produtoRepository.existsById(produtoId)) {
            throw new IllegalArgumentException(
                    "Produto não encontrado"
            );
        }

        return movimentacaoEstoqueRepository.calcularSaldo(produtoId);
    }

    public MovimentacaoEstoque registrarAjuste(
            Long produtoId,
            BigDecimal quantidade,
            String descricao) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado"
                        )
                );

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException(
                    "A quantidade do ajuste não pode ser zero"
            );
        }

        BigDecimal saldoAtual = movimentacaoEstoqueRepository.calcularSaldo(produtoId);
        BigDecimal novoSaldo = saldoAtual.add(quantidade);

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O ajuste deixaria o estoque negativo. Saldo disponível: "
                            + saldoAtual
            );
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setProduto(produto);
        movimentacao.setTipo(TipoMovimentacaoEstoque.AJUSTE);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDescricao(descricao);
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    public List<MovimentacaoEstoque> listarMovimentacoes(
            Long produtoId) {

        if (!produtoRepository.existsById(produtoId)) {
            throw new IllegalArgumentException(
                    "Produto não encontrado"
            );
        }

        return movimentacaoEstoqueRepository.findByProdutoIdOrderByDataHoraDesc(produtoId);
    }
}
