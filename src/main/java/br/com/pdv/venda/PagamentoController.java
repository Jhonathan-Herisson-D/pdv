package br.com.pdv.venda;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendas/{vendaId}/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<Pagamento> registrar(
            @PathVariable Long vendaId,
            @RequestBody Map<String, Object> body) {

        TipoPagamento tipo = TipoPagamento.valueOf(body.get("tipo").toString());
        BigDecimal valor = new  BigDecimal(body.get("valor").toString());
        Pagamento pagamento = pagamentoService.registrar(
                vendaId,
                tipo,
                valor
        );

        return ResponseEntity.status(201).body(pagamento);
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> listar(
            @PathVariable Long vendaId) {

        return ResponseEntity.ok(pagamentoService.listaPorVenda(vendaId));
    }

}
