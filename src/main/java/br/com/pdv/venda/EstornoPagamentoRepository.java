package br.com.pdv.venda;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EstornoPagamentoRepository extends JpaRepository<EstornoPagamento, Long> {

    boolean existsByPagamentoId(Long pagamentoId);
}
