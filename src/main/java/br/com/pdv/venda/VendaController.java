package br.com.pdv.venda;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<Venda> iniciarVenda() {

        Venda venda = vendaService.iniciarVenda();

        return ResponseEntity.status(201).body(venda);
    }

    @PostMapping("/{vendaId}/itens")
    public ResponseEntity<ItemVenda> adicionarItem(
            @PathVariable Long vendaId,
            @RequestBody Map<String, Object> body) {

        Long produtoId = Long.valueOf(body.get("produtoId").toString());

        BigDecimal quantidade = new BigDecimal(body.get("quantidade").toString());

        ItemVenda item = vendaService.adicionarItem(
                vendaId,
                produtoId,
                quantidade
        );

        return ResponseEntity.status(201).body(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {

        return vendaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{vendaId}/itens")
    public ResponseEntity<List<ItemVenda>> listarItens(
            @PathVariable Long vendaId) {

        return ResponseEntity.ok(vendaService.listarItens(vendaId));
    }

    @PutMapping("/{vendaId}/itens/{itemId}/quantidade")
    public ResponseEntity<ItemVenda> alterarQuantidadeItem(
            @PathVariable Long vendaId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> body) {

        BigDecimal quantidade = new BigDecimal(body.get("quantidade").toString());

        ItemVenda item = vendaService.alterarQuantidadeItem(
                vendaId,
                itemId,
                quantidade
        );

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{vendaId}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable Long vendaId,
            @PathVariable Long itemId) {

        vendaService.removerItem(vendaId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Venda> finalizar(
            @PathVariable Long id) {

        Venda venda = vendaService.finalizarVenda(id);

        return ResponseEntity.ok(venda);
    }
}