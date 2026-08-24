package br.com.pdv.produto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoInternoAndAtivoTrue(String codigoInterno);

    Optional<Produto> findByGtinAndAtivoTrue(String gtin);

    List<Produto> findByAtivoTrue();
}
