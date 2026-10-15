package br.com.pdv.produto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Test
    void naoDeveAceitarGtinDuplicado() {

        // Simula o repositório
        ProdutoRepository repository = mock(ProdutoRepository.class);

        // Cria o serviço usando o repositório simulado
        ProdutoService service = new ProdutoService(repository);

        // Cria um produto
        Produto produto = new Produto();
        produto.setGtin("7891234567890");

        // Simula que esse GTIN já existe
        when(repository.existsByGtin("7891234567890"))
                .thenReturn(true);

        // Verifica se o serviço rejeita o cadastro
        assertThrows(
                IllegalArgumentException.class,
                () -> service.salvar(produto)
        );
    }

    @Test
    void naoDeveAceitarCodigoInternoDuplicado() {

        ProdutoRepository repository = mock(ProdutoRepository.class);

        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setCodigoInterno("PROD001");

        when(repository.existsByCodigoInterno("PROD001"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.salvar(produto)
        );
    }

    @Test
    void deveSalvarProdutoComGtinDisponivel() {

        ProdutoRepository repository = mock(ProdutoRepository.class);

        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setNome("Arroz 5 kg");
        produto.setGtin("7891234567890");
        produto.setPrecoVenda(new BigDecimal("29.90"));

        when(repository.existsByGtin("7891234567890"))
                .thenReturn(false);

        when(repository.save(produto))
                .thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals(produto, resultado);
        verify(repository).save(produto);
    }

    @Test
    void deveDesativarProdutoExistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz 5kg");
        produto.setAtivo(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(produto));

        boolean resultado = service.desativar(1L);

        assertTrue(resultado);
        assertFalse(produto.getAtivo());

        verify(repository).save(produto);
    }

    @Test
    void deveReativarProdutoExistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz 5kg");
        produto.setAtivo(false);

        when(repository.findById(1L))
                .thenReturn(Optional.of(produto));

        boolean resultado = service.reativar(1L);

        assertTrue(resultado);
        assertTrue(produto.getAtivo());

        verify(repository).save(produto);
    }

    @Test
    void naoDeveReativarProdutoInexistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Simula que o produto não foi encontrado
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Tenta reativar o produto
        boolean resultado = service.reativar(999L);

        // A operação deve retornar false
        assertFalse(resultado);

        // Confirma que nenhum produto foi salvo
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void naoDeveDesativarProdutoInexistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Simula que o produto não existe
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Tenta desativar o produto
        boolean resultado = service.desativar(999L);

        // Deve retornar false
        assertFalse(resultado);

        // Não deve tentar salvar
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void deveContarProdutoAtivos() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Simula que existem 5 produtos ativos
        when(repository.countByAtivoTrue())
                .thenReturn(5L);

        // Exexuta o método do serviço
        long resultado = service.contarAtivos();

        // Confirma que a quantidade retornada é 5
        assertEquals(5L, resultado);

        // Confirma que o repositório foi consultado
        verify(repository).countByAtivoTrue();
    }

    @Test
    void deveBuscarProdutoPorId() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Cria um produto fictício
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz 5kg");

        // Simula que o banco encontrou o produto
        when(repository.findById(1L))
                .thenReturn(Optional.of(produto));

        // Executa a busca
        Optional<Produto> resultado = service.buscarPorId(1L);

        // Confirma que o produto foi encontrado
        assertTrue(resultado.isPresent());

        // Confirma que é o produto esperado
        assertEquals("Arroz 5kg", resultado.get().getNome());

        // Confirma que o repositório foi consultado
        verify(repository).findById(1L);
    }

    @Test
    void deveRetornarVazioQuandoProdutoNaoExiste() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Simula que o produto não foi encontrado
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Executa a busca
        Optional<Produto> resultado = service.buscarPorId(999L);

        // Confirma que nenhum produto foi encontrado
        assertTrue(resultado.isEmpty());

        // Confirma que o repositório foi consultado
        verify(repository).findById(999L);
    }

    @Test
    void deveBuscarProdutoAtivoPorGtin() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Coca-Cola 2L");
        produto.setGtin("7891234567890");
        produto.setAtivo(true);

        when(repository.findByGtinAndAtivoTrue("7891234567890"))
                .thenReturn(Optional.of(produto));

        Optional<Produto> resultado =
                service.buscarPorGtin("7891234567890");

        assertTrue(resultado.isPresent());
        assertEquals("Coca-Cola 2L", resultado.get().getNome());

        verify(repository)
                .findByGtinAndAtivoTrue("7891234567890");
    }

    @Test
    void deveBuscarProdutoAtivoPorCodigoInterno() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setId(2L);
        produto.setNome("Feijão 1kg");
        produto.setCodigoInterno("FEI001");
        produto.setAtivo(true);

        when(repository.findByCodigoInternoAndAtivoTrue("FEI001"))
                .thenReturn(Optional.of(produto));

        Optional<Produto> resultado =
                service.buscarPorCodigoInterno("FEI001");

        assertTrue(resultado.isPresent());
        assertEquals("Feijão 1kg", resultado.get().getNome());

        verify(repository)
                .findByCodigoInternoAndAtivoTrue("FEI001");
    }

    @Test
    void deveBuscarProdutosAtivosPorNome() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz Branco 5kg");
        produto.setAtivo(true);

        // Simula o resultado da pesquisa
        when(repository.findByNomeContainingIgnoreCaseAndAtivoTrue("arroz"))
                .thenReturn(List.of(produto));

        // Executa a pesquisa
        List<Produto> resultado = service.buscarPorNome("arroz");

        // Verifica o resultado
        assertEquals(1, resultado.size());
        assertEquals("Arroz Branco 5kg", resultado.get(0).getNome());

        // Confirma que o método correto foi chamado
        verify(repository)
                .findByNomeContainingIgnoreCaseAndAtivoTrue("arroz");
    }

    @Test
    void deveListarProdutosAtivos() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto arroz = new Produto();
        arroz.setId(1L);
        arroz.setNome("Arroz 5kg");
        arroz.setAtivo(true);

        Produto feijao = new Produto();
        feijao.setId(2L);
        feijao.setNome("Feijão 1kg");
        feijao.setAtivo(true);

        // Simula o retorno do repositório
        when(repository.findByAtivoTrue())
                .thenReturn(List.of(arroz, feijao));

        // Executa a listagem
        List<Produto> resultado = service.listarTodos();

        // Verifica os produtos retornados
        assertEquals(2, resultado.size());
        assertEquals("Arroz 5kg", resultado.get(0).getNome());
        assertEquals("Feijão 1kg", resultado.get(1).getNome());

        // Confirma a consulta utilizada
        verify(repository).findByAtivoTrue();
    }

    @Test
    void naoDeveAtualizarProdutoInexistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto novosDados = new Produto();
        novosDados.setNome("Arroz Integral 5kg");
        novosDados.setPrecoVenda(new BigDecimal("35.90"));

        // Simula que o produto não existe
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Tenta atualizar o produto
        Optional<Produto> resultado =
                service.atualizar(999L, novosDados);

        // Confirma que nenhum produto foi encontrado
        assertTrue(resultado.isEmpty());

        // Confirma que nenhuma alteração foi salva
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void naoDeveAtualizarProdutoComGtinDuplicado() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Produto que já existe
        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Arroz 5kg");

        // Dados recebidos para atualização
        Produto novosDados = new Produto();
        novosDados.setNome("Arroz Integral 5kg");
        novosDados.setGtin("7891234567890");

        when(repository.findById(1L))
                .thenReturn(Optional.of(produtoExistente));

        // Simula que outro produto já utiliza esse GTIN
        when(repository.existsByGtinAndIdNot("7891234567890", 1L))
                .thenReturn(true);

        // A atualização deve ser rejeitada
        assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(1L, novosDados)
        );

        // Nenhuma alteração deve ser salva
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void naoDeveAtualizarProdutoComCodigoInternoDuplicado() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Arroz 5kg");

        Produto novosDados = new Produto();
        novosDados.setNome("Arroz Integral 5kg");
        novosDados.setCodigoInterno("PROD002");

        when(repository.findById(1L))
                .thenReturn(Optional.of(produtoExistente));

        // Simula que outro produto já utiliza esse código
        when(repository.existsByCodigoInternoAndIdNot("PROD002", 1L))
                .thenReturn(true);

        // A atualização deve ser rejeitada
        assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(1L, novosDados)
        );

        // Nenhuma alteração deve ser salva
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void deveAtualizarProdutoExistente() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        // Produto cadastrado
        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Arroz 5kg");
        produtoExistente.setPrecoVenda(new BigDecimal("25.00"));

        // Novos dados
        Produto novosDados = new Produto();
        novosDados.setNome("Arroz Integral 5kg");
        novosDados.setCodigoInterno("ARR001");
        novosDados.setGtin("7891234567890");
        novosDados.setPrecoVenda(new BigDecimal("32.90"));
        novosDados.setAtivo(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(produtoExistente));

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Executa a atualização
        Optional<Produto> resultado =
                service.atualizar(1L, novosDados);

        // Verifica os novos dados
        assertTrue(resultado.isPresent());
        assertEquals("Arroz Integral 5kg", resultado.get().getNome());
        assertEquals(new BigDecimal("32.90"),
                resultado.get().getPrecoVenda());
        assertEquals("ARR001", resultado.get().getCodigoInterno());

        // Confirma que o produto foi salvo
        verify(repository).save(produtoExistente);
    }

    @Test
    void devePermitirAtualizarProdutoMantendoMesmoGtin() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Arroz 5kg");
        produtoExistente.setGtin("7891234567890");
        produtoExistente.setPrecoVenda(new BigDecimal("25.00"));

        Produto novosDados = new Produto();
        novosDados.setNome("Arroz 5kg");
        novosDados.setGtin("7891234567890");
        novosDados.setPrecoVenda(new BigDecimal("29.90"));
        novosDados.setAtivo(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(produtoExistente));

        // Nenhum OUTRO produto possui esse GTIN
        when(repository.existsByGtinAndIdNot("7891234567890", 1L))
                .thenReturn(false);

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Produto> resultado =
                service.atualizar(1L, novosDados);

        assertTrue(resultado.isPresent());

        assertEquals("7891234567890",
                resultado.get().getGtin());

        assertEquals(new BigDecimal("29.90"),
                resultado.get().getPrecoVenda());

        verify(repository).save(produtoExistente);
    }

    @Test
    void devePermitirAtualizarProdutoMantendoMesmoCodigoInterno() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Feijão 1kg");
        produtoExistente.setCodigoInterno("FEI001");
        produtoExistente.setPrecoVenda(new BigDecimal("12.50"));

        Produto novosDados = new Produto();
        novosDados.setNome("Feijão Carioca 1kg");
        novosDados.setCodigoInterno("FEI001");
        novosDados.setPrecoVenda(new BigDecimal("14.90"));
        novosDados.setAtivo(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(produtoExistente));

        // Nenhum outro produto utiliza esse código interno
        when(repository.existsByCodigoInternoAndIdNot("FEI001", 1L))
                .thenReturn(false);

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Produto> resultado =
                service.atualizar(1L, novosDados);

        assertTrue(resultado.isPresent());

        assertEquals("FEI001",
                resultado.get().getCodigoInterno());

        assertEquals("Feijão Carioca 1kg",
                resultado.get().getNome());

        assertEquals(new BigDecimal("14.90"),
                resultado.get().getPrecoVenda());

        verify(repository).save(produtoExistente);
    }

    @Test
    void deveSalvarProdutoSemGtin() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setNome("Bolo Caseiro");
        produto.setCodigoInterno("BOLO001");
        produto.setPrecoVenda(new BigDecimal("15.00"));
        produto.setAtivo(true);

        // Não informamos o GTIN
        produto.setGtin(null);

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = service.salvar(produto);

        assertEquals("Bolo Caseiro", resultado.getNome());
        assertEquals("BOLO001", resultado.getCodigoInterno());
        assertNull(resultado.getGtin());

        verify(repository).save(produto);

        // Não deve consultar duplicidade de GTIN nulo
        verify(repository, never()).existsByGtin(anyString());
    }

    @Test
    void deveSalvarProdutoSemCodigoInterno() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setNome("Refrigerante 2L");
        produto.setGtin("7891234567890");
        produto.setCodigoInterno(null);
        produto.setPrecoVenda(new BigDecimal("12.90"));
        produto.setAtivo(true);

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o cadastro
        Produto resultado = service.salvar(produto);

        // Verifica os dados
        assertEquals("Refrigerante 2L", resultado.getNome());
        assertEquals("7891234567890", resultado.getGtin());
        assertNull(resultado.getCodigoInterno());

        // Confirma que o produto foi salvo
        verify(repository).save(produto);

        // Não deve verificar duplicidade de código interno nulo
        verify(repository, never())
                .existsByCodigoInterno(anyString());
    }

    @Test
    void deveSalvarProdutoSemGtinESemCodigoInterno() {

        ProdutoRepository repository = mock(ProdutoRepository.class);
        ProdutoService service = new ProdutoService(repository);

        Produto produto = new Produto();
        produto.setNome("Pão Francês");
        produto.setPrecoVenda(new BigDecimal("1.00"));
        produto.setAtivo(true);

        // Ambos os códigos ficam vazios
        produto.setGtin(null);
        produto.setCodigoInterno(null);

        when(repository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = service.salvar(produto);

        assertEquals("Pão Francês", resultado.getNome());
        assertNull(resultado.getGtin());
        assertNull(resultado.getCodigoInterno());

        verify(repository).save(produto);

        // Não deve consultar duplicidade de códigos ausentes
        verify(repository, never()).existsByGtin(anyString());
        verify(repository, never()).existsByCodigoInterno(anyString());
    }
}
