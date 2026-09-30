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
    BigDecimal somarPagamentosPorVenda(@Param("vendaId") Long vendaId);
}
