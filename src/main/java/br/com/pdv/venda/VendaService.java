package br.com.pdv.venda;

import br.com.pdv.caixa.Caixa;
import br.com.pdv.caixa.CaixaRepository;
import br.com.pdv.produto.Produto;
import br.com.pdv.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final CaixaRepository caixaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(
            VendaRepository vendaRepository,
            CaixaRepository caixaRepository,
            ItemVendaRepository itemVendaRepository,
            ProdutoRepository produtoRepository) {

        this.vendaRepository = vendaRepository;
        this.caixaRepository = caixaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda iniciarVenda() {

        Caixa caixa = caixaRepository.findByAbertoTrue()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe caixa aberto"
                        )
                );

        Venda venda = new Venda();

        venda.setCaixa(caixa);
        venda.setDataHora(LocalDateTime.now());
        venda.setStatus(StatusVenda.ABERTA);
        venda.setTotal(BigDecimal.ZERO);

        return vendaRepository.save(venda);
    }

    public ItemVenda adicionarItem(
            Long vendaId,
            Long produtoId,
            BigDecimal quantidade) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        ));

        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new IllegalArgumentException(
                    "Só é possivel adicionar itens em uma venda aberta"
            );
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado"
                        )
                );

        if (!produto.getAtivo()) {
            throw new IllegalArgumentException(
                    "Produto está inativo"
            );
        }

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero"
            );
        }

        BigDecimal precoUnitario = produto.getPrecoVenda();

        BigDecimal subtotal = precoUnitario.multiply(quantidade);

        ItemVenda item = new ItemVenda();

        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        item.setSubtotal(subtotal);

        ItemVenda itemSalvo = itemVendaRepository.save(item);

        venda.setTotal(venda.getTotal().add(subtotal));

        vendaRepository.save(venda);

        return itemSalvo;

    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }

    public List<ItemVenda> listarItens(Long vendaId) {

        if (!vendaRepository.existsById(vendaId)) {
            throw new IllegalArgumentException(
                    "Venda não encontrada"
            );
        }

        return itemVendaRepository.findByVendaIdOrderByIdAsc(vendaId);
    }
}
