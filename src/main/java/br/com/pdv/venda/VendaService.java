package br.com.pdv.venda;

import br.com.pdv.caixa.Caixa;
import br.com.pdv.caixa.CaixaRepository;
import br.com.pdv.estoque.EstoqueService;
import br.com.pdv.produto.Produto;
import br.com.pdv.produto.ProdutoRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final PagamentoRepository pagamentoRepository;
    private final EstoqueService estoqueService;

    public VendaService(
            VendaRepository vendaRepository,
            CaixaRepository caixaRepository,
            ItemVendaRepository itemVendaRepository,
            ProdutoRepository produtoRepository,
            PagamentoRepository pagamentoRepository,
            EstoqueService estoqueService) {

        this.vendaRepository = vendaRepository;
        this.caixaRepository = caixaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.produtoRepository = produtoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.estoqueService = estoqueService;

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

    public ItemVenda alterarQuantidadeItem(
            Long vendaId,
            Long itemId,
            BigDecimal novaQuantidade) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        )
                );

        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new IllegalArgumentException(
                    "Só é possivel alterar itens de uma venda aberta"
            );
        }

        ItemVenda item = itemVendaRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item não encontrado"
                        )
                );

        if (!item.getVenda().getId().equals(vendaId)) {
            throw new IllegalArgumentException(
                    "O item não pertece a esta venda"
            );
        }

        if (novaQuantidade == null || novaQuantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero"
            );
        }

        item.setQuantidade(novaQuantidade);

        BigDecimal novoSubtotal = item.getPrecoUnitario().multiply(novaQuantidade);

        item.setSubtotal(novoSubtotal);

        ItemVenda itemAtualizado = itemVendaRepository.save(item);

        BigDecimal novoTotal = itemVendaRepository.somarSubtotalPorVenda(vendaId);

        venda.setTotal(novoTotal);
        vendaRepository.save(venda);

        return itemAtualizado;
    }

    public void removerItem(Long vendaId, Long itemId) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        )
                );

        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new IllegalArgumentException(
                    "Só é possível remover itens de uma venda aberta"
            );
        }

        ItemVenda item = itemVendaRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item não encontrado"
                        )
                );

        if (!item.getVenda().getId().equals(vendaId)) {
            throw new IllegalArgumentException(
                    "O item não pertence a essa venda"
            );
        }

        itemVendaRepository.delete(item);

        BigDecimal novoTotal = itemVendaRepository.somarSubtotalPorVenda(vendaId);

        venda.setTotal(novoTotal);

        vendaRepository.save(venda);
    }

    @Transactional
    public Venda finalizarVenda(Long vendaId) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        )
                );
        if (venda.getStatus() !=StatusVenda.ABERTA) {
            throw new IllegalArgumentException(
                    "A venda não está aberta"
            );
        }

        if (venda.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Não é possível finalizar uma venda sem itens"
            );
        }

        BigDecimal totalPago = pagamentoRepository.somarPagamentosPorVenda(vendaId);

        if (totalPago.compareTo(venda.getTotal()) <0) {

            BigDecimal valorFaltante = venda.getTotal().subtract(totalPago);

            throw new IllegalArgumentException(
                    "Pagamento insuficiente. Faltam R$ " + valorFaltante
            );
        }

        List<ItemVenda> itens = itemVendaRepository.findByVendaIdOrderByIdAsc(vendaId);

        for (ItemVenda item : itens) {

            BigDecimal saldoAtual= estoqueService.consultarSaldo(item.getProduto().getId());

            if (item.getQuantidade().compareTo(saldoAtual) > 0) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: "
                                + item.getProduto().getNome()
                                + ". Disponível: "
                                +saldoAtual
                );
            }
        }

        for (ItemVenda item : itens) {

            estoqueService.registrarSaida(
                    item.getProduto().getId(),
                    item.getQuantidade(),
                    "Venda #" + venda.getId()
            );
        }

        venda.setStatus(StatusVenda.FINALIZADA);

        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda cancelarVenda(Long vendaId) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venda não encontrada"
                        )
                );

        // Uma venda já cancelada não pode ser cancelada novamente
        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new IllegalArgumentException(
                    "A venda já está cancelada"
            );
        }

        if (venda.getStatus() == StatusVenda.FINALIZADA) {
            throw new IllegalArgumentException(
                    "Cancelamento de venda finalizada exige estorno financeiro"
            );
        }

        // Se a venda já foi finalizada, o estoque já foi baixado
        // Portanto, precisamos devolver od produtos ao estoque
        if (venda.getStatus() == StatusVenda.FINALIZADA) {

            List<ItemVenda> itens = itemVendaRepository.findByVendaIdOrderByIdAsc(vendaId);

            for (ItemVenda item : itens) {

                estoqueService.registrarEntrada(
                        item.getProduto().getId(),
                        item.getQuantidade(),
                        "Cancelamento da venda #" + vendaId
                );
            }
        }
        venda.setStatus(StatusVenda.CANCELADA);

        return vendaRepository.save(venda);
    }

    public List<ResumoVenda> listarHistorico() {

        return vendaRepository
                .findAllByOrderByDataHoraDesc()
                .stream()
                .map(venda -> {

                    ResumoVenda resumo = new ResumoVenda();

                    resumo.setVendaId(venda.getId());
                    resumo.setDataHora(venda.getDataHora());
                    resumo.setStatus(venda.getStatus());
                    resumo.setTotal(venda.getTotal());
                    resumo.setCaixaId(venda.getCaixa().getId());

                    return resumo;
                })
                .toList();
    }

    public DetalheVenda buscarDetalhe(Long vendaId) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Venda não encontrada")
                );

        List<ItemVenda> itens = itemVendaRepository.findByVendaIdOrderByIdAsc(vendaId);
        List<DetalheItemVenda> detalheItens =
                itens.stream()
                        .map(item -> {
                            DetalheItemVenda detalhe = new DetalheItemVenda();

                            detalhe.setItemId(item.getId());
                            detalhe.setProdutoId(item.getProduto().getId());
                            detalhe.setProdutoNome(item.getProduto().getNome());
                            detalhe.setQuantidade(item.getQuantidade());
                            detalhe.setPrecoUnitario(item.getPrecoUnitario());
                            detalhe.setSubtotal(item.getSubtotal());

                            return detalhe;

                        })
                        .toList();

        List<Pagamento> pagamentos = pagamentoRepository.findByVendaIdOrderByIdAsc(vendaId);

        List<DetalhePagamentoVenda> detalhesPagamentos =
                pagamentos.stream()
                        .map(pagamento -> {

                            DetalhePagamentoVenda detalhe =
                                    new DetalhePagamentoVenda();

                            detalhe.setPagamentoId(pagamento.getId());
                            detalhe.setDataHora(pagamento.getDataHora());
                            detalhe.setTipo(pagamento.getTipo());
                            detalhe.setValor(pagamento.getValor());
                            detalhe.setValorRecebido(pagamento.getValorRecebido());
                            detalhe.setTroco(pagamento.getTroco());

                            return detalhe;
                        })
                        .toList();

        DetalheVenda detalheVenda = new DetalheVenda();

        detalheVenda.setVendaId(venda.getId());
        detalheVenda.setCaixaId(venda.getCaixa().getId());
        detalheVenda.setDataHora(venda.getDataHora());
        detalheVenda.setStatusVenda(venda.getStatus());
        detalheVenda.setTotal(venda.getTotal());

        detalheVenda.setItens(detalheItens);
        detalheVenda.setPagamentos(detalhesPagamentos);

        return detalheVenda;
    }
}
