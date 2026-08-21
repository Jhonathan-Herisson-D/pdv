package br.com.pdv.produto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoInterno(String codigoInterno);

    Optional<Produto> findByGtin(String gtin);

    Long id(Long id);
}
