package br.com.pdv.produto;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProdutoRepositoryIntegrationTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEBuscarProdutoNoBanco() {

        // Cria um produto
        Produto produto = new Produto();
        produto.setNome("Arroz Teste 5kg");
        produto.setCodigoInterno("TESTE001");
        produto.setPrecoVenda(new BigDecimal("29.90"));
        produto.setAtivo(true);

        // Salva no PostgreSQL
        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        // Busca o produto pelo ID
        Optional<Produto> resultado =
                produtoRepository.findById(produtoSalvo.getId());

        // Verifica os dados
        assertTrue(resultado.isPresent());

        assertEquals("Arroz Teste 5kg",
                resultado.get().getNome());

        assertEquals(new BigDecimal("29.90"),
                resultado.get().getPrecoVenda());
    }

    @Test
    void deveBuscarProdutoAtivoPorGtinNoBanco() {

        // Cria um produto com GTIN
        Produto produto = new Produto();
        produto.setNome("Refrigerante Teste 2L");
        produto.setGtin("7891234567890");
        produto.setPrecoVenda(new BigDecimal("12.90"));
        produto.setAtivo(true);

        // Salva no banco de testes
        produtoRepository.saveAndFlush(produto);

        // Busca pelo GTIN
        Optional<Produto> resultado =
                produtoRepository.findByGtinAndAtivoTrue("7891234567890");

        // Verifica se encontrou o produto correto
        assertTrue(resultado.isPresent());

        assertEquals("Refrigerante Teste 2L",
                resultado.get().getNome());

        assertEquals("7891234567890",
                resultado.get().getGtin());
    }

    @Test
    void naoDeveBuscarProdutoInativoPorGtinNoBanco() {

        // Cria um produto desativado
        Produto produto = new Produto();
        produto.setNome("Refrigerante Desativado 2L");
        produto.setGtin("7899876543210");
        produto.setPrecoVenda(new BigDecimal("10.90"));
        produto.setAtivo(false);

        // Salva no banco de testes
        produtoRepository.saveAndFlush(produto);

        // Tenta buscar o produto entre os ativos
        Optional<Produto> resultado =
                produtoRepository.findByGtinAndAtivoTrue("7899876543210");

        // O produto não deve ser encontrado
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBuscarProdutoAtivoPorCodigoInternoNoBanco() {

        // Cria um produto sem GTIN
        Produto produto = new Produto();
        produto.setNome("Bolo Caseiro");
        produto.setCodigoInterno("BOLO001");
        produto.setGtin(null);
        produto.setPrecoVenda(new BigDecimal("15.00"));
        produto.setAtivo(true);

        // Salva no PostgreSQL de testes
        produtoRepository.saveAndFlush(produto);

        // Busca pelo código interno
        Optional<Produto> resultado =
                produtoRepository.findByCodigoInternoAndAtivoTrue("BOLO001");

        // Verifica o resultado
        assertTrue(resultado.isPresent());

        assertEquals("Bolo Caseiro",
                resultado.get().getNome());

        assertEquals("BOLO001",
                resultado.get().getCodigoInterno());

        assertNull(resultado.get().getGtin());
    }

    @Test
    void naoDevePermitirGtinDuplicadoNoBanco() {

        // Primeiro produto
        Produto produto1 = new Produto();
        produto1.setNome("Refrigerante Cola 2L");
        produto1.setGtin("7891111111111");
        produto1.setPrecoVenda(new BigDecimal("12.90"));
        produto1.setAtivo(true);

        produtoRepository.saveAndFlush(produto1);

        // Segundo produto com o mesmo GTIN
        Produto produto2 = new Produto();
        produto2.setNome("Refrigerante Guaraná 2L");
        produto2.setGtin("7891111111111");
        produto2.setPrecoVenda(new BigDecimal("10.90"));
        produto2.setAtivo(true);

        // O PostgreSQL deve rejeitar a duplicidade
        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> produtoRepository.saveAndFlush(produto2)
        );
    }

    @Test
    void naoDevePermitirCodigoInternoDuplicadoNoBanco() {

        // Primeiro produto
        Produto produto1 = new Produto();
        produto1.setNome("Arroz Branco 5kg");
        produto1.setCodigoInterno("ARR001");
        produto1.setPrecoVenda(new BigDecimal("25.90"));
        produto1.setAtivo(true);

        produtoRepository.saveAndFlush(produto1);

        // Segundo produto com o mesmo código interno
        Produto produto2 = new Produto();
        produto2.setNome("Arroz Integral 5kg");
        produto2.setCodigoInterno("ARR001");
        produto2.setPrecoVenda(new BigDecimal("32.90"));
        produto2.setAtivo(true);

        // O PostgreSQL deve rejeitar a duplicidade
        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> produtoRepository.saveAndFlush(produto2)
        );
    }

    @Test
    void deveBuscarProdutosPorNomeIgnorandoMaiusculasNoBanco() {

        // Cadastra um produto no banco de testes
        Produto produto = new Produto();
        produto.setNome("Arroz Branco 5kg");
        produto.setCodigoInterno("ARRTESTE001");
        produto.setPrecoVenda(new BigDecimal("29.90"));
        produto.setAtivo(true);

        produtoRepository.saveAndFlush(produto);

        // Pesquisa usando apenas parte do nome
        List<Produto> resultado =
                produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("arroz");

        // Confirma que encontrou o produto
        assertEquals(1, resultado.size());

        assertEquals("Arroz Branco 5kg",
                resultado.get(0).getNome());
    }

    @Test
    void naoDeveBuscarProdutoInativoPorNomeNoBanco() {

        // Cria um produto desativado
        Produto produto = new Produto();
        produto.setNome("Macarrão Teste Inativo");
        produto.setCodigoInterno("MACINATIVO001");
        produto.setPrecoVenda(new BigDecimal("6.90"));
        produto.setAtivo(false);

        // Salva no PostgreSQL de testes
        produtoRepository.saveAndFlush(produto);

        // Confirma que o produto existe no banco
        Optional<Produto> produtoSalvo =
                produtoRepository.findById(produto.getId());

        assertTrue(produtoSalvo.isPresent());

        // Pesquisa entre os produtos ativos
        List<Produto> resultado =
                produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(
                        "Macarrão Teste Inativo"
                );

        // O produto desativado não deve aparecer
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarSomenteProdutosAtivosNoBanco() {

        // Primeiro produto: ativo
        Produto arroz = new Produto();
        arroz.setNome("Arroz Teste 5kg");
        arroz.setCodigoInterno("LISTA001");
        arroz.setPrecoVenda(new BigDecimal("29.90"));
        arroz.setAtivo(true);

        produtoRepository.saveAndFlush(arroz);

        // Segundo produto: desativado
        Produto feijao = new Produto();
        feijao.setNome("Feijão Teste 1kg");
        feijao.setCodigoInterno("LISTA002");
        feijao.setPrecoVenda(new BigDecimal("12.90"));
        feijao.setAtivo(false);

        produtoRepository.saveAndFlush(feijao);

        // Consulta somente os produtos ativos
        List<Produto> resultado =
                produtoRepository.findByAtivoTrue();

        // Deve encontrar apenas o arroz
        assertEquals(1, resultado.size());

        assertEquals("Arroz Teste 5kg",
                resultado.get(0).getNome());

        assertTrue(resultado.get(0).getAtivo());
    }

    @Test
    void deveContarSomenteProdutosAtivosNoBanco() {

        // Primeiro produto: ativo
        Produto arroz = new Produto();
        arroz.setNome("Arroz Contagem 5kg");
        arroz.setCodigoInterno("CONT001");
        arroz.setPrecoVenda(new BigDecimal("29.90"));
        arroz.setAtivo(true);

        produtoRepository.saveAndFlush(arroz);

        // Segundo produto: ativo
        Produto feijao = new Produto();
        feijao.setNome("Feijão Contagem 1kg");
        feijao.setCodigoInterno("CONT002");
        feijao.setPrecoVenda(new BigDecimal("12.90"));
        feijao.setAtivo(true);

        produtoRepository.saveAndFlush(feijao);

        // Terceiro produto: desativado
        Produto macarrao = new Produto();
        macarrao.setNome("Macarrão Contagem 500g");
        macarrao.setCodigoInterno("CONT003");
        macarrao.setPrecoVenda(new BigDecimal("6.90"));
        macarrao.setAtivo(false);

        produtoRepository.saveAndFlush(macarrao);

        // Conta somente os produtos ativos
        long quantidade = produtoRepository.countByAtivoTrue();

        // Esperamos encontrar 2 produtos ativos
        assertEquals(2L, quantidade);
    }

    @Test
    void deveAtualizarPrecoProdutoNoBanco() {

        // Cadastra o produto com preço inicial
        Produto produto = new Produto();
        produto.setNome("Café Teste 500g");
        produto.setCodigoInterno("CAFE001");
        produto.setPrecoVenda(new BigDecimal("18.90"));
        produto.setAtivo(true);

        Produto produtoSalvo =
                produtoRepository.saveAndFlush(produto);

        // Altera o preço
        produtoSalvo.setPrecoVenda(new BigDecimal("21.50"));

        produtoRepository.saveAndFlush(produtoSalvo);

        // Consulta o produto atualizado
        Optional<Produto> resultado =
                produtoRepository.findById(produtoSalvo.getId());

        // Confirma o novo preço
        assertTrue(resultado.isPresent());

        assertEquals(
                new BigDecimal("21.50"),
                resultado.get().getPrecoVenda()
        );
    }

    @Test
    void deveConfirmarAtualizacaoPrecoDiretamenteNoBanco() {

        // Cadastra o produto
        Produto produto = new Produto();
        produto.setNome("Café Especial 500g");
        produto.setCodigoInterno("CAFE002");
        produto.setPrecoVenda(new BigDecimal("18.90"));
        produto.setAtivo(true);

        Produto salvo = produtoRepository.saveAndFlush(produto);

        // Atualiza o preço
        salvo.setPrecoVenda(new BigDecimal("21.50"));
        produtoRepository.saveAndFlush(salvo);

        // Limpa os objetos mantidos em memória pelo JPA
        entityManager.clear();

        // Consulta novamente no PostgreSQL
        Optional<Produto> resultado =
                produtoRepository.findById(salvo.getId());

        assertTrue(resultado.isPresent());

        assertEquals(
                new BigDecimal("21.50"),
                resultado.get().getPrecoVenda()
        );
    }

    @Test
    void deveDesativarProdutoNoBanco() {

        // Cadastra um produto ativo
        Produto produto = new Produto();
        produto.setNome("Leite Integral 1L");
        produto.setCodigoInterno("LEITE001");
        produto.setPrecoVenda(new BigDecimal("5.90"));
        produto.setAtivo(true);

        Produto salvo = produtoRepository.saveAndFlush(produto);

        // Desativa o produto
        salvo.setAtivo(false);
        produtoRepository.saveAndFlush(salvo);

        // Limpa os objetos mantidos em memória
        entityManager.clear();

        // Confirma que o produto continua cadastrado
        Optional<Produto> resultado =
                produtoRepository.findById(salvo.getId());

        assertTrue(resultado.isPresent());
        assertFalse(resultado.get().getAtivo());

        // Confirma que não aparece na busca de ativos
        Optional<Produto> buscaAtivos =
                produtoRepository.findByCodigoInternoAndAtivoTrue("LEITE001");

        assertTrue(buscaAtivos.isEmpty());
    }

    @Test
    void deveReativarProdutoNoBanco() {

        // Cadastra um produto desativado
        Produto produto = new Produto();
        produto.setNome("Açúcar Cristal 5kg");
        produto.setCodigoInterno("ACUCAR001");
        produto.setPrecoVenda(new BigDecimal("18.90"));
        produto.setAtivo(false);

        Produto salvo = produtoRepository.saveAndFlush(produto);

        // Reativa o produto
        salvo.setAtivo(true);
        produtoRepository.saveAndFlush(salvo);

        // Limpa os objetos mantidos em memória pelo JPA
        entityManager.clear();

        // Consulta novamente no PostgreSQL
        Optional<Produto> resultado =
                produtoRepository.findById(salvo.getId());

        // Confirma que o produto está ativo
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().getAtivo());

        // Confirma que voltou a aparecer na busca de ativos
        Optional<Produto> buscaAtivos =
                produtoRepository.findByCodigoInternoAndAtivoTrue("ACUCAR001");

        assertTrue(buscaAtivos.isPresent());

        assertEquals("Açúcar Cristal 5kg",
                buscaAtivos.get().getNome());
    }

    @Test
    void deveExcluirProdutoNoBancoDeTestes() {

        // Cadastra um produto fictício
        Produto produto = new Produto();
        produto.setNome("Produto Temporário");
        produto.setCodigoInterno("TEMP001");
        produto.setPrecoVenda(new BigDecimal("9.90"));
        produto.setAtivo(true);

        Produto salvo = produtoRepository.saveAndFlush(produto);

        Long id = salvo.getId();

        // Confirma que o produto existe
        assertTrue(produtoRepository.findById(id).isPresent());

        // Exclui o produto do banco de testes
        produtoRepository.delete(salvo);
        produtoRepository.flush();

        // Limpa os objetos em memória
        entityManager.clear();

        // Confirma que o produto não existe mais
        Optional<Produto> resultado = produtoRepository.findById(id);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveAtualizarGtinProdutoNoBanco() {

        // Cadastra um produto
        Produto produto = new Produto();
        produto.setNome("Biscoito Recheado");
        produto.setCodigoInterno("BISC001");
        produto.setGtin("7891111111111");
        produto.setPrecoVenda(new BigDecimal("4.90"));
        produto.setAtivo(true);

        Produto salvo = produtoRepository.saveAndFlush(produto);

        Long idOriginal = salvo.getId();

        // Altera o GTIN
        salvo.setGtin("7892222222222");
        produtoRepository.saveAndFlush(salvo);

        // Limpa os objetos mantidos em memória
        entityManager.clear();

        // Busca pelo novo GTIN
        Optional<Produto> resultado =
                produtoRepository.findByGtinAndAtivoTrue("7892222222222");

        assertTrue(resultado.isPresent());

        // Confirma que continua sendo o mesmo produto
        assertEquals(idOriginal, resultado.get().getId());

        assertEquals("Biscoito Recheado",
                resultado.get().getNome());

        // O GTIN antigo não deve localizar o produto
        Optional<Produto> buscaAntiga =
                produtoRepository.findByGtinAndAtivoTrue("7891111111111");

        assertTrue(buscaAntiga.isEmpty());
    }
}