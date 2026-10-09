package br.com.pdv.venda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EstornoPagamentoRepository extends JpaRepository<EstornoPagamento, Long> {

    boolean existsByPagamentoId(Long pagamentoId);

    @Query("""
            SELECT COALESCE(SUM(e.valor), 0)
            FROM EstornoPagamento  e
            WHERE e.caixaEstorno.id = :caixaId
              AND e.pagamento.tipo = :tipo
              AND e.status = br.com.pdv.venda.StatusEstorno.CONCLUIDO
           """)
    BigDecimal somarEstornosConcluidosPorCaixaETipo(
            @Param("caixaId") Long caixaId,
            @Param("tipo") TipoPagamento tipo
    );

    Long countByStatusAndCaixaEstornoIsNull(StatusEstorno status);

}
