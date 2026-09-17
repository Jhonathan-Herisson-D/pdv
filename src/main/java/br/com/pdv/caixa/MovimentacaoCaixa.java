package br.com.pdv.caixa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_caixa")
public class MovimentacaoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacaoCaixa tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;

    }

    public TipoMovimentacaoCaixa getTipo() {
        return tipo;

    }

    public void setTipo(TipoMovimentacaoCaixa tipo) {
        this.tipo = tipo;

    }

    public BigDecimal getValor() {
        return valor;

    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;

    }

    public LocalDateTime getDataHora() {
        return dataHora;

    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;

    }

    public String getDescricao() {
        return descricao;

    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;

    }
}
