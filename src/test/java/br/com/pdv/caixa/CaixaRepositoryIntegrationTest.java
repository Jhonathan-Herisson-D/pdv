package br.com.pdv.caixa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CaixaRepositoryIntegrationTest {

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private EntityManager entityManager;

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

    @Test
    void deveBuscarCaixaSalvoDiretamenteDoBanco() {

        // 1. Criar um caixa
        Caixa caixa = new Caixa();
        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setSaldoInicial(new BigDecimal("350.00"));
        caixa.setAberto(true);

        // 2. Salvar no PostgreSQL
        Caixa caixaSalvo = caixaRepository.save(caixa);

        Long id = caixaSalvo.getId();

        // 3. Enviar alterações pendentes ao banco
        entityManager.flush();

        // 4. Limpar a memória do Hibernate
        entityManager.clear();

        // 5. Consultar novamente pelo ID
        Optional<Caixa> resultado = caixaRepository.findById(id);

        // 6. Verificar os dados
        assertTrue(resultado.isPresent());

        assertEquals(
                0,
                resultado.get().getSaldoInicial()
                        .compareTo(new BigDecimal("350.00"))
        );

        assertTrue(resultado.get().getAberto());

        assertNotNull(resultado.get().getDataAbertura());
    }

    @Test
    void deveEncontrarCaixaAbertoNoBanco() {

        // 1. Criar um caixa aberto
        Caixa caixa = new Caixa();

        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setSaldoInicial(new BigDecimal("500.00"));
        caixa.setAberto(true);

        // 2. Salvar no banco de testes
        Caixa caixaSalvo = caixaRepository.save(caixa);

        // 3. Sincronizar e limpar a memória
        entityManager.flush();
        entityManager.clear();

        // 4. Buscar o caixa aberto
        Optional<Caixa> resultado =
                caixaRepository.findByAbertoTrue();

        // 5. Verificar o resultado
        assertTrue(resultado.isPresent());

        assertEquals(
                caixaSalvo.getId(),
                resultado.get().getId()
        );

        assertTrue(resultado.get().getAberto());
    }

    @Test
    void naoDeveEncontrarCaixaAbertoQuandoTodosEstaoFechados() {

        // 1. Criar um caixa fechado
        Caixa caixa = new Caixa();

        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setDataFechamento(LocalDateTime.now());
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setSaldoFinal(new BigDecimal("200.00"));
        caixa.setAberto(false);

        // 2. Salvar no PostgreSQL de testes
        caixaRepository.save(caixa);

        // 3. Sincronizar com o banco e limpar a memória
        entityManager.flush();
        entityManager.clear();

        // 4. Procurar um caixa aberto
        Optional<Caixa> resultado =
                caixaRepository.findByAbertoTrue();

        // 5. Confirmar que nenhum caixa aberto foi encontrado
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarCaixasDoMaisRecenteParaOMaisAntigo() {

        // 1. Criar um caixa antigo
        Caixa caixaAntigo = new Caixa();
        caixaAntigo.setDataAbertura(
                LocalDateTime.of(2026, 10, 18, 8, 0)
        );
        caixaAntigo.setSaldoInicial(new BigDecimal("100.00"));
        caixaAntigo.setAberto(false);

        caixaRepository.save(caixaAntigo);

        // 2. Criar um caixa mais recente
        Caixa caixaRecente = new Caixa();
        caixaRecente.setDataAbertura(
                LocalDateTime.of(2026, 10, 20, 8, 0)
        );
        caixaRecente.setSaldoInicial(new BigDecimal("200.00"));
        caixaRecente.setAberto(false);

        caixaRepository.save(caixaRecente);

        // 3. Sincronizar e limpar a memória
        entityManager.flush();
        entityManager.clear();

        // 4. Consultar o histórico
        List<Caixa> resultado =
                caixaRepository.findAllByOrderByDataAberturaDesc();

        // 5. Verificar a ordem
        assertEquals(2, resultado.size());

        assertEquals(
                caixaRecente.getId(),
                resultado.get(0).getId()
        );

        assertEquals(
                caixaAntigo.getId(),
                resultado.get(1).getId()
        );
    }
}