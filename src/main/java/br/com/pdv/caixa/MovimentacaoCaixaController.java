package br.com.pdv.caixa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caixas/movimentacoes")
public class MovimentacaoCaixaController {

    private final MovimentacaoCaixaService movimentacaoCaixaService;

    public MovimentacaoCaixaController(
            MovimentacaoCaixaService movimentacaoCaixaService) {

        this.movimentacaoCaixaService = movimentacaoCaixaService;
    }

    @PostMapping("/suprimento")
    public ResponseEntity<MovimentacaoCaixa> registrarSuprimento(
            @RequestBody Map<String, Object> body) {

        BigDecimal valor =
                new BigDecimal(body.get("valor").toString());

        String descricao =
                body.get("descricao") != null
                        ? body.get("descricao").toString()
                        : null;

        MovimentacaoCaixa movimentacao =
                movimentacaoCaixaService.registrarSuprimento(valor, descricao);

        return ResponseEntity.status(201).body(movimentacao);
    }

    @PostMapping("/sangria")
    public ResponseEntity<MovimentacaoCaixa> registrarSangria(
            @RequestBody Map<String, Object> body) {

        BigDecimal valor =
                new BigDecimal(body.get("valor").toString());

        String descricao =
                body.get("descricao") !=null
                        ? body.get("descricao").toString()
                        : null;

        MovimentacaoCaixa movimentacao =
                movimentacaoCaixaService.registrarSangria(valor, descricao);

        return ResponseEntity.status(201).body(movimentacao);
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoCaixa>> listarMovimentacoes() {

        List<MovimentacaoCaixa> movimentacoes =
                movimentacaoCaixaService.listarMovimentacoesCaixaAberto();

        return ResponseEntity.ok(movimentacoes);
    }

}
