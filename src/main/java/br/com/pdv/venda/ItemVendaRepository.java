package br.com.pdv.venda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    List<ItemVenda> findByVendaIdOrderByIdAsc(Long vendaId);

    @Query("""
            SELECT COALESCE(SUM (i.subtotal), 0)
            FROM ItemVenda i
            WHERE i.venda.id = :vendaId
            """)

    BigDecimal somarSubtotalPorVenda(@Param("vendaId") Long vendaId);
}
