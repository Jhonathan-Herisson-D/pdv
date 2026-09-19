package br.com.pdv.caixa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Long> {

    List<MovimentacaoCaixa> findByCaixaIdOrderByDataHora(Long caixaId);

    @Query("""
            SELECT COALESCE(SUM(m.valor), 0)
            FROM MovimentacaoCaixa m 
            WHERE m.caixa.id = :caixaId
            AND m.tipo = :tipo
            """)
    BigDecimal somarPorCaixaETipo(
            @Param("caixaId") Long caixaId,
            @Param("tipo") TipoMovimentacaoCaixa tipo);

}
