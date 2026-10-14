package br.com.pdv.venda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByVendaIdOrderByIdAsc(Long vendaId);

    @Query("""
           SELECT COALESCE(SUM(p.valor), 0)
           FROM Pagamento p
           WHERE p.venda.id = :vendaId
           """)
    BigDecimal somarPagamentosPorVenda(
            @Param("vendaId") Long vendaId
    );

    @Query("""
       SELECT COALESCE(SUM(p.valor), 0)
       FROM Pagamento p
       WHERE p.venda.caixa.id = :caixaId
         AND p.tipo = :tipo
         AND p.venda.status IN (
             br.com.pdv.venda.StatusVenda.FINALIZADA,
             br.com.pdv.venda.StatusVenda.CANCELADA
         )
       """)
    BigDecimal somarPorCaixaETipo(
            @Param("caixaId") Long caixaId,
            @Param("tipo") TipoPagamento tipo
    );

    @Query("""
       SELECT COUNT(DISTINCT p.venda.id)
       FROM Pagamento p
       WHERE p.venda.caixa.id = :caixaId
         AND p.tipo = :tipo
         AND p.venda.status IN (
             br.com.pdv.venda.StatusVenda.FINALIZADA,
             br.com.pdv.venda.StatusVenda.CANCELADA
         )
       """)
    Long contarPorCaixaETipo(
            @Param("caixaId") Long caixaId,
            @Param("tipo") TipoPagamento tipo
    );

    @Query("""
       SELECT p
       FROM Pagamento p
       WHERE p.venda.caixa.id = :caixaId
         AND p.venda.status IN (
             br.com.pdv.venda.StatusVenda.FINALIZADA,
             br.com.pdv.venda.StatusVenda.CANCELADA
         )
       ORDER BY p.dataHora ASC
       """)
    List<Pagamento> buscarPagamentosPorCaixa(
            @Param("caixaId") Long caixaId
    );
}