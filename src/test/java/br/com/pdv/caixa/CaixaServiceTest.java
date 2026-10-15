package br.com.pdv.caixa;

import br.com.pdv.venda.EstornoPagamentoRepository;
import br.com.pdv.venda.PagamentoRepository;
import br.com.pdv.venda.TipoPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaixaServiceTest {

    @Test
    void deveAbrirCaixaQuandoNaoExisteOutroAberto() {

        // Cria os repositórios simulados
        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        // Cria o serviço
        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula que não existe caixa aberto
        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.empty());

        // Simula o salvamento
        when(caixaRepository.save(any(Caixa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Abre o caixa com R$ 200,00
        Caixa resultado = service.abrir(new BigDecimal("200.00"));

        // Verifica os dados
        assertEquals(new BigDecimal("200.00"),
                resultado.getSaldoInicial());

        assertTrue(resultado.getAberto());

        assertNotNull(resultado.getDataAbertura());

        // Confirma que o salvamento foi solicitado
        verify(caixaRepository).save(any(Caixa.class));
    }

    @Test
    void naoDeveAbrirCaixaQuandoJaExisteOutroAberto() {

        // Cria os repositórios simulados
        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula um caixa que já está aberto
        Caixa caixaAberto = new Caixa();
        caixaAberto.setId(1L);
        caixaAberto.setSaldoInicial(new BigDecimal("200.00"));
        caixaAberto.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixaAberto));

        // Tenta abrir outro caixa
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.abrir(new BigDecimal("100.00"))
        );

        // Verifica a mensagem de erro
        assertEquals(
                "Já existe um caixa aberto",
                excecao.getMessage()
        );

        // Confirma que nenhum novo caixa foi salvo
        verify(caixaRepository, never()).save(any(Caixa.class));
    }

    @Test
    void deveBuscarCaixaAberto() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula um caixa aberto
        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        // Executa a consulta
        Optional<Caixa> resultado = service.buscarCaixaAberto();

        // Verifica o resultado
        assertTrue(resultado.isPresent());

        assertEquals(1L, resultado.get().getId());

        assertEquals(
                new BigDecimal("200.00"),
                resultado.get().getSaldoInicial()
        );

        assertTrue(resultado.get().getAberto());

        // Confirma que o repositório foi consultado
        verify(caixaRepository).findByAbertoTrue();
    }

    @Test
    void deveRetornarVazioQuandoNaoExisteCaixaAberto() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula que não existe caixa aberto
        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.empty());

        // Consulta o caixa aberto
        Optional<Caixa> resultado = service.buscarCaixaAberto();

        // Confirma que nenhum caixa foi encontrado
        assertTrue(resultado.isEmpty());

        // Confirma que o repositório foi consultado
        verify(caixaRepository).findByAbertoTrue();
    }

    @Test
    void naoDeveFecharCaixaQuandoNaoExisteCaixaAberto() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula que não existe caixa aberto
        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.empty());

        // Tenta fechar o caixa com R$ 200,00
        Optional<Caixa> resultado =
                service.fecharCaixaAberto(new BigDecimal("200.00"));

        // Confirma que nenhum caixa foi fechado
        assertTrue(resultado.isEmpty());

        // Confirma que não houve salvamento
        verify(caixaRepository, never()).save(any(Caixa.class));
    }

    @Test
    void naoDeveFecharCaixaComSaldoNegativo() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula um caixa aberto
        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        // Tenta fechar o caixa com saldo negativo
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.fecharCaixaAberto(
                        new BigDecimal("-50.00")
                )
        );

        // Verifica a mensagem de erro
        assertEquals(
                "O saldo final não pode ser negativo",
                excecao.getMessage()
        );

        // Confirma que o caixa não foi fechado
        assertTrue(caixa.getAberto());

        // Confirma que nenhuma alteração foi salva
        verify(caixaRepository, never()).save(any(Caixa.class));
    }

    @Test
    void deveCalcularSaldoEsperadoDoCaixa() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        // Suprimentos: R$ 100,00
        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SUPRIMENTO))
                .thenReturn(new BigDecimal("100.00"));

        // Sangrias: R$ 50,00
        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SANGRIA))
                .thenReturn(new BigDecimal("50.00"));

        // Pagamentos em dinheiro: R$ 150,00
        when(pagamentoRepository.somarPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(new BigDecimal("150.00"));

        // Estornos concluídos em dinheiro: R$ 30,00
        when(estornoRepository.somarEstornosConcluidosPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(new BigDecimal("30.00"));

        // Executa o cálculo
        BigDecimal resultado = service.calcularSaldoEsperado();

        // Confirma o saldo esperado
        assertEquals(new BigDecimal("370.00"), resultado);
    }

    @Test
    void naoDeveCalcularSaldoEsperadoSemCaixaAberto() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula que não existe caixa aberto
        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.empty());

        // Tenta calcular o saldo esperado
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularSaldoEsperado()
        );

        // Confirma a mensagem de erro
        assertEquals(
                "Não existe caixa aberto",
                excecao.getMessage()
        );

        // Confirma que não foram consultadas movimentações
        verifyNoInteractions(
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );
    }

    @Test
    void deveFecharCaixaSemDiferenca() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula um caixa aberto
        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        // Simula movimentações sem suprimentos e sangrias
        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SUPRIMENTO))
                .thenReturn(BigDecimal.ZERO);

        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SANGRIA))
                .thenReturn(BigDecimal.ZERO);

        // R$ 100,00 recebidos em dinheiro
        when(pagamentoRepository.somarPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(new BigDecimal("100.00"));

        // Nenhum estorno em dinheiro
        when(estornoRepository.somarEstornosConcluidosPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(BigDecimal.ZERO);

        // Simula o salvamento
        when(caixaRepository.save(any(Caixa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Fecha o caixa informando R$ 300,00
        Optional<Caixa> resultado =
                service.fecharCaixaAberto(new BigDecimal("300.00"));

        // Verifica o fechamento
        assertTrue(resultado.isPresent());
        assertFalse(resultado.get().getAberto());

        assertEquals(
                new BigDecimal("300.00"),
                resultado.get().getSaldoFinal()
        );

        assertEquals(
                0,
                resultado.get().getDiferenca().compareTo(BigDecimal.ZERO)
        );

        assertNotNull(resultado.get().getDataFechamento());

        // Confirma que o caixa foi salvo
        verify(caixaRepository).save(caixa);
    }

    @Test
    void deveFecharCaixaComFaltaDeDinheiro() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SUPRIMENTO))
                .thenReturn(BigDecimal.ZERO);

        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SANGRIA))
                .thenReturn(BigDecimal.ZERO);

        when(pagamentoRepository.somarPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(new BigDecimal("150.00"));

        when(estornoRepository.somarEstornosConcluidosPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(BigDecimal.ZERO);

        when(caixaRepository.save(any(Caixa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Operador informa que contou R$ 330,00
        Optional<Caixa> resultado =
                service.fecharCaixaAberto(new BigDecimal("330.00"));

        assertTrue(resultado.isPresent());

        Caixa caixaFechado = resultado.get();

        // Caixa deve estar fechado
        assertFalse(caixaFechado.getAberto());

        // Saldo final informado
        assertEquals(
                new BigDecimal("330.00"),
                caixaFechado.getSaldoFinal()
        );

        // Diferença negativa de R$ 20,00
        assertEquals(
                0,
                caixaFechado.getDiferenca()
                        .compareTo(new BigDecimal("-20.00"))
        );

        // Data do fechamento registrada
        assertNotNull(caixaFechado.getDataFechamento());

        // Alteração salva
        verify(caixaRepository).save(caixa);
    }

    @Test
    void deveFecharCaixaComSobraDeDinheiro() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SUPRIMENTO))
                .thenReturn(BigDecimal.ZERO);

        when(movimentacaoRepository.somarPorCaixaETipo(
                1L, TipoMovimentacaoCaixa.SANGRIA))
                .thenReturn(BigDecimal.ZERO);

        when(pagamentoRepository.somarPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(new BigDecimal("150.00"));

        when(estornoRepository.somarEstornosConcluidosPorCaixaETipo(
                1L, TipoPagamento.DINHEIRO))
                .thenReturn(BigDecimal.ZERO);

        when(caixaRepository.save(any(Caixa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Operador contou R$ 380,00
        Optional<Caixa> resultado =
                service.fecharCaixaAberto(new BigDecimal("380.00"));

        assertTrue(resultado.isPresent());

        Caixa caixaFechado = resultado.get();

        // Confirma que o caixa foi fechado
        assertFalse(caixaFechado.getAberto());

        // Confirma o saldo informado
        assertEquals(
                new BigDecimal("380.00"),
                caixaFechado.getSaldoFinal()
        );

        // Confirma a sobra de R$ 30,00
        assertEquals(
                0,
                caixaFechado.getDiferenca()
                        .compareTo(new BigDecimal("30.00"))
        );

        // Confirma a data de fechamento
        assertNotNull(caixaFechado.getDataFechamento());

        // Confirma o salvamento
        verify(caixaRepository).save(caixa);
    }

    @Test
    void naoDeveFecharCaixaComSaldoNulo() {

        CaixaRepository caixaRepository = mock(CaixaRepository.class);

        MovimentacaoCaixaRepository movimentacaoRepository =
                mock(MovimentacaoCaixaRepository.class);

        PagamentoRepository pagamentoRepository =
                mock(PagamentoRepository.class);

        EstornoPagamentoRepository estornoRepository =
                mock(EstornoPagamentoRepository.class);

        CaixaService service = new CaixaService(
                caixaRepository,
                movimentacaoRepository,
                pagamentoRepository,
                estornoRepository
        );

        // Simula um caixa aberto
        Caixa caixa = new Caixa();
        caixa.setId(1L);
        caixa.setSaldoInicial(new BigDecimal("200.00"));
        caixa.setAberto(true);

        when(caixaRepository.findByAbertoTrue())
                .thenReturn(Optional.of(caixa));

        // Tenta fechar o caixa sem informar o saldo
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.fecharCaixaAberto(null)
        );

        // Confirma a mensagem de erro
        assertEquals(
                "O saldo final não pode ser negativo",
                excecao.getMessage()
        );

        // Confirma que o caixa continua aberto
        assertTrue(caixa.getAberto());

        // Confirma que nada foi salvo
        verify(caixaRepository, never()).save(any(Caixa.class));
    }
}