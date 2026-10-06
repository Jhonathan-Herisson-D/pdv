package br.com.pdv.caixa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caixas")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<Caixa> abrir(@RequestBody Map<String, BigDecimal> body) {

        BigDecimal saldoInicial = body.get("saldoInicial");

        Caixa caixa = caixaService.abrir(saldoInicial);

        return ResponseEntity.status(201).body(caixa);
    }

    @GetMapping("/aberto")
    public ResponseEntity<Caixa> buscarCaixaAberto() {

        return caixaService.buscarCaixaAberto()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/fechar")
    public ResponseEntity<Caixa> fecharCaixa(@RequestBody Map<String, BigDecimal> body) {

        BigDecimal saldoFinal = body.get("saldoFinal");

        return caixaService.fecharCaixaAberto(saldoFinal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/saldo-esperado")
    public ResponseEntity<Map<String, BigDecimal>> saldoEsperado() {

        BigDecimal saldo = caixaService.calcularSaldoEsperado();

        return ResponseEntity.ok(
                Map.of("saldoEsperado", saldo)
        );
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<ExtratoCaixa> extrato(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                caixaService.gerarExtrato(id)
        );
    }

    @GetMapping("/historico")
    public ResponseEntity<List<Caixa>> listarHistorico() {

        return ResponseEntity.ok(
                caixaService.listarHistorico()
        );
    }
}
