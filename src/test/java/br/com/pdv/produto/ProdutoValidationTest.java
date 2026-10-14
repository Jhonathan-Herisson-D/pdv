package br.com.pdv.produto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoValidationTest {

    @Test
    void naoDeveAceitarPrecoNegativo() {

        Produto produto = new Produto();
        produto.setNome("Arroz 5kg");
        produto.setPrecoVenda(new BigDecimal("-10.00"));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){

            Validator validator = factory.getValidator();

            var violacoes = validator.validate(produto);

            assertTrue(
                    violacoes.stream().anyMatch(
                            violacao -> violacao.getPropertyPath()
                                    .toString().equals("precoVenda")
                    ),
                    "O preço negativo deveria ser rejeitado"
            );

        }
    }

    @Test
    void naoDeveAceitarPrecoZero() {

        Produto produto = new Produto();
        produto.setNome("Feijão 1kg");
        produto.setPrecoVenda(new BigDecimal("0.00"));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){

            Validator validator = factory.getValidator();

            var violacoes = validator.validate(produto);

            assertTrue(
                    violacoes.stream().anyMatch(
                            violacao -> violacao.getPropertyPath()
                                    .toString().equals("precoVenda")
                    ),
                    "O preço zero deveria ser rejeitado"
            );

        }
    }

    @Test
    void naoDeveAceitarProdutoSemNome() {

        Produto produto = new Produto();
        produto.setNome("");
        produto.setPrecoVenda(new BigDecimal("10.00"));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {

            Validator validator = factory.getValidator();

            var violacoes = validator.validate(produto);

            assertTrue(violacoes.stream().anyMatch(
                    violacao -> violacao.getPropertyPath()
                            .toString().equals("nome")
                    ),
            "O nome vazio deveria ser rejeitado"
            );
        }
    }

    @Test
    void deveAceitarProdutoValido() {

        Produto produto = new Produto();
        produto.setNome("Arroz 5kg");
        produto.setPrecoVenda(new BigDecimal("29.90"));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){

            Validator validator = factory.getValidator();

            var violacoes = validator.validate(produto);

            assertTrue(
                    violacoes.isEmpty(),
                    "Um produto válido não deveria apresentar erros"
            );
        }
    }

}
