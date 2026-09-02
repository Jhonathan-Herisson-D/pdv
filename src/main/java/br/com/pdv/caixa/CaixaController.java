package br.com.pdv.caixa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
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
}
