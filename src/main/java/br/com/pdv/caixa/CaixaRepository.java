package br.com.pdv.caixa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Optional<Caixa> findByAbertoTrue();
    List<Caixa> findAllByOrderByDataAberturaDesc();
}
