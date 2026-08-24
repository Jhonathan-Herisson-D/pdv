package br.com.pdv.produto;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findByAtivoTrue();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<Produto> buscarPorCodigoInterno(String codigoInterno) {
        return produtoRepository.findByCodigoInternoAndAtivoTrue(codigoInterno);
    }

    public Optional<Produto> buscarPorGtin(String gtin) {
        return produtoRepository.findByGtinAndAtivoTrue(gtin);
    }

    public Optional<Produto> atualizar(Long id, Produto novosDados) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if (produtoExistente.isEmpty()) {
            return Optional.empty();
        }

        Produto produto = produtoExistente.get();

        produto.setNome(novosDados.getNome());
        produto.setCodigoInterno(novosDados.getCodigoInterno());
        produto.setGtin(novosDados.getGtin());
        produto.setPrecoVenda(novosDados.getPrecoVenda());
        produto.setAtivo(novosDados.getAtivo());

        Produto produtoAtualizado = produtoRepository.save(produto);

        return Optional.of(produtoAtualizado);
    }

    public boolean desativar(Long id) {

        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if (produtoExistente.isEmpty()) {
            return false;
        }

        Produto produto = produtoExistente.get();

        produto.setAtivo(false);

        produtoRepository.save(produto);

        return true;
    }

}
