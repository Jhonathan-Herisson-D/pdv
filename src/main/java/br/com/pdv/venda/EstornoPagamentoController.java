package br.com.pdv.venda;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/estornos")
public class EstornoPagamentoController {

    private final EstornoPagamentoService estornoService;

    public EstornoPagamentoController(EstornoPagamentoService estornoService) {
        this.estornoService = estornoService;
    }

    @PostMapping("/pagamentos/{pagamentoId}")
    public ResponseEntity<EstornoPagamento> solicitar(
            @PathVariable Long pagamentoId,
            @RequestBody Map<String, String> body) {

        String motivo = body.get("motivo");

        EstornoPagamento estorno =
                estornoService.solicitarEstorno(pagamentoId,motivo);

        return ResponseEntity.status(201).body(estorno);
    }

    @PutMapping("/{estornoId}/concluir")
    public ResponseEntity<EstornoPagamento> concluir(
            @PathVariable Long estornoId) {

        EstornoPagamento estorno = estornoService.concluirEstorno(estornoId);

        return ResponseEntity.ok(estorno);
    }
}
