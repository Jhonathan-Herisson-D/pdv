package br.com.pdv.estoque;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/produtos/{produtoId}/entradas")
    public ResponseEntity<MovimentacaoEstoque> registrarEntrada(
            @PathVariable Long produtoId,
            @RequestBody Map<String, Object> body) {

        BigDecimal quantidade = new BigDecimal(body.get("quantidade").toString());
        String descricao = body.get("descricao") !=null
                                    ? body.get("descricao").toString()
                                    : null;

        MovimentacaoEstoque movimentacao =
                estoqueService.registrarEntrada(
                        produtoId,
                        quantidade,
                        descricao
                );

        return ResponseEntity.status(201).body(movimentacao);
    }

    @GetMapping("/produtos/{produtoId}/saldo")
    public ResponseEntity<Map<String, Object>> consultarSaldo(
            @PathVariable Long produtoId) {

        BigDecimal saldo = estoqueService.consultarSaldo(produtoId);

        return ResponseEntity.ok(
                Map.of(
                        "produtoId", produtoId,
                        "saldo", saldo
                )
        );
    }

    @PostMapping("/produtos/{produtoId}/saidas")
    public ResponseEntity<MovimentacaoEstoque> registrarSaida(
            @PathVariable Long produtoId,
            @RequestBody Map<String, Object> body) {

        BigDecimal quantidade = new BigDecimal(body.get("quantidade").toString());

        String descricao = body.get("descricao") != null ? body.get("descricao").toString() : null;

        MovimentacaoEstoque movimentacao =
                estoqueService.registrarSaida(
                        produtoId,
                        quantidade,
                        descricao
                );

        return ResponseEntity.status(201).body(movimentacao);
    }
}
