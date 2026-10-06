package br.com.pdv.venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DetalheVenda {

    private Long vendaId;
    private Long caixaId;
    private LocalDateTime dataHora;
    private StatusVenda statusVenda;
    private BigDecimal total;

    private List<DetalheItemVenda> itens;
    private List<DetalhePagamentoVenda> pagamentos;

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    public Long getCaixaId() {
        return caixaId;
    }

    public void setCaixaId(Long caixaId) {
        this.caixaId = caixaId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusVenda getStatusVenda() {
        return statusVenda;
    }

    public void setStatusVenda(StatusVenda statusVenda) {
        this.statusVenda = statusVenda;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalheItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<DetalheItemVenda> itens) {
        this.itens = itens;
    }

    public List<DetalhePagamentoVenda> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<DetalhePagamentoVenda> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
