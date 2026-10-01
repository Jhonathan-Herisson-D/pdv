package br.com.pdv.estoque;

import br.com.pdv.produto.Produto;
import br.com.pdv.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public BigDecimal consultarSaldo(Long produtoId) {

        if (!produtoRepository.existsById(produtoId)) {
            throw new IllegalArgumentException(
                    "Produto não encontrado"
            );
        }

        return movimentacaoEstoqueRepository.calcularSaldo(produtoId);
    }
}
