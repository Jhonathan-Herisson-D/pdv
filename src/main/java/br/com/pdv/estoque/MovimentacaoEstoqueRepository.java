package br.com.pdv.estoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("""
           SELECT COALESCE(SUM(
               CASE 
                   WHEN m.tipo = br.com.pdv.estoque.TipoMovimentacaoEstoque.ENTRADA
                       THEN m.quantidade
                   WHEN m.tipo = br.com.pdv.estoque.TipoMovimentacaoEstoque.SAIDA
                       THEN -m.quantidade
                   ELSE 0
               END
           ), 0)
           FROM MovimentacaoEstoque m
           WHERE m.produto.id = :produtoId
           """)
    BigDecimal calcularSaldo(@Param("produtoId") Long produtoId);
}
