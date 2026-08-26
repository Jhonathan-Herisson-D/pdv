package br.com.pdv.produto;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@Valid @RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvar(produto);

        return ResponseEntity.status(201).body(produtoSalvo);
    }

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/nome/{nome}")
    public List<Produto> buscarPorNome(@PathVariable String nome) {

        return produtoService.buscarPorNome(nome);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity ::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoInterno}")
    public ResponseEntity<Produto> buscarPorCodigoInterno(
            @PathVariable String codigoInterno) {

        return produtoService.buscarPorCodigoInterno(codigoInterno)
                .map(ResponseEntity ::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gtin/{gtin}")
    public ResponseEntity<Produto> buscarPorGtin(@PathVariable String gtin) {
        return produtoService.buscarPorGtin(gtin)
                .map(ResponseEntity ::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestBody Produto produto) {

        return produtoService.atualizar(id, produto)
                .map(ResponseEntity ::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {

        boolean desativado = produtoService.desativar(id);

        if (!desativado) {
            return ResponseEntity.notFound().build();

        }

        return ResponseEntity.noContent().build();
    }

}
