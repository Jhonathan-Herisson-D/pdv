package br.com.pdv.produto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
}
