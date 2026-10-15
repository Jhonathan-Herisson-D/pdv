package br.com.pdv.caixa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CaixaRepositoryIntegrationTest {

    @Autowired
    private CaixaRepository caixaRepository;

    @Test
    void deveSalvarEConsultarCaixaAberto() {

        // 1. Criar um caixa
        Caixa caixa = new Caixa();

        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        // 2. Salvar no PostgreSQL de testes
        Caixa caixaSalvo = caixaRepository.save(caixa);

        // 3. Verificar se recebeu um ID
        assertNotNull(caixaSalvo.getId());

        // 4. Consultar o caixa pelo ID
        Optional<Caixa> resultado =
                caixaRepository.findById(caixaSalvo.getId());

        // 5. Verificar os dados encontrados
        assertTrue(resultado.isPresent());

        assertEquals(
                0,
                resultado.get().getSaldoInicial()
                        .compareTo(new BigDecimal("200.00"))
        );

        assertTrue(resultado.get().getAberto());
    }
}