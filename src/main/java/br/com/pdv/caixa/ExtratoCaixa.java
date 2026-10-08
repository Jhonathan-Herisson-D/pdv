package br.com.pdv.caixa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExtratoCaixa {

    // Identificação do caixa
    private Long caixaId;
    private boolean aberto;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    // Resumo financeiro
    private Long quantidadeDinheiro;
    private BigDecimal totalDinheiro;
    private BigDecimal totalEstornosDinheiro;
    private BigDecimal totalDinheiroLiquido;
    private Long quantidadePix;
    private BigDecimal totalPix;
    private BigDecimal totalEstornosPix;
    private BigDecimal totalPixLiquido;
    private Long quantidadeCartaoDebito;
    private BigDecimal totalCartaoDebito;
    private BigDecimal totalEstornosDebito;
    private BigDecimal totalDebitoLiquido;
    private Long quantidadeCartaoCredito;
    private BigDecimal totalCartaoCredito;
    private BigDecimal totalEstornosCredito;
    private BigDecimal totalCreditoLiquido;
    private BigDecimal totalVendas;

    // Conferência do dinheiro físico
    private BigDecimal saldoInicial;
    private BigDecimal totalSuprimentos;
    private BigDecimal totalSangrias;
    private BigDecimal saldoEsperado;
    private BigDecimal saldoFinal;
    private BigDecimal diferenca;

    // Detalhes dos pagamentos
    private List<DetalhePagamentoCaixa> pagamentos;

    public Long getCaixaId() {
        return caixaId;
    }

    public void setCaixaId(Long caixaId) {
        this.caixaId = caixaId;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Long getQuantidadeDinheiro() {
        return quantidadeDinheiro;
    }

    public void setQuantidadeDinheiro(Long quantidadeDinheiro) {
        this.quantidadeDinheiro = quantidadeDinheiro;
    }

    public BigDecimal getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(BigDecimal totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public BigDecimal getTotalEstornosDinheiro() {
        return totalEstornosDinheiro;
    }

    public void setTotalEstornosDinheiro(BigDecimal totalEstornosDinheiro) {
        this.totalEstornosDinheiro = totalEstornosDinheiro;
    }

    public BigDecimal getTotalDinheiroLiquido() {
        return totalDinheiroLiquido;
    }

    public void setTotalDinheiroLiquido(BigDecimal totalDinheiroLiquido) {
        this.totalDinheiroLiquido = totalDinheiroLiquido;
    }

    public Long getQuantidadePix() {
        return quantidadePix;
    }

    public void setQuantidadePix(Long quantidadePix) {
        this.quantidadePix = quantidadePix;
    }

    public BigDecimal getTotalPix() {
        return totalPix;
    }

    public void setTotalPix(BigDecimal totalPix) {
        this.totalPix = totalPix;
    }

    public BigDecimal getTotalEstornosPix() {
        return totalEstornosPix;
    }

    public void setTotalEstornosPix(BigDecimal totalEstornosPix) {
        this.totalEstornosPix = totalEstornosPix;
    }

    public BigDecimal getTotalPixLiquido() {
        return totalPixLiquido;
    }

    public void setTotalPixLiquido(BigDecimal totalPixLiquido) {
        this.totalPixLiquido = totalPixLiquido;
    }

    public Long getQuantidadeCartaoDebito() {
        return quantidadeCartaoDebito;
    }

    public void setQuantidadeCartaoDebito(Long quantidadeCartaoDebito) {
        this.quantidadeCartaoDebito = quantidadeCartaoDebito;
    }

    public BigDecimal getTotalCartaoDebito() {
        return totalCartaoDebito;
    }

    public void setTotalCartaoDebito(BigDecimal totalCartaoDebito) {
        this.totalCartaoDebito = totalCartaoDebito;
    }

    public BigDecimal getTotalEstornosDebito() {
        return totalEstornosDebito;
    }

    public void setTotalEstornosDebito(BigDecimal totalEstornosDebito) {
        this.totalEstornosDebito = totalEstornosDebito;
    }

    public BigDecimal getTotalDebitoLiquido() {
        return totalDebitoLiquido;
    }

    public void setTotalDebitoLiquido(BigDecimal totalDebitoLiquido) {
        this.totalDebitoLiquido = totalDebitoLiquido;
    }

    public Long getQuantidadeCartaoCredito() {
        return quantidadeCartaoCredito;
    }

    public void setQuantidadeCartaoCredito(Long quantidadeCartaoCredito) {
        this.quantidadeCartaoCredito = quantidadeCartaoCredito;
    }

    public BigDecimal getTotalCartaoCredito() {
        return totalCartaoCredito;
    }

    public void setTotalCartaoCredito(BigDecimal totalCartaoCredito) {
        this.totalCartaoCredito = totalCartaoCredito;
    }

    public BigDecimal getTotalEstornosCredito() {
        return totalEstornosCredito;
    }

    public void setTotalEstornosCredito(BigDecimal totalEstornosCredito) {
        this.totalEstornosCredito = totalEstornosCredito;
    }

    public BigDecimal getTotalCreditoLiquido() {
        return totalCreditoLiquido;
    }

    public void setTotalCreditoLiquido(BigDecimal totalCreditoLiquido) {
        this.totalCreditoLiquido = totalCreditoLiquido;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getTotalSuprimentos() {
        return totalSuprimentos;
    }

    public void setTotalSuprimentos(BigDecimal totalSuprimentos) {
        this.totalSuprimentos = totalSuprimentos;
    }

    public BigDecimal getTotalSangrias() {
        return totalSangrias;
    }

    public void setTotalSangrias(BigDecimal totalSangrias) {
        this.totalSangrias = totalSangrias;
    }

    public BigDecimal getSaldoEsperado() {
        return saldoEsperado;
    }

    public void setSaldoEsperado(BigDecimal saldoEsperado) {
        this.saldoEsperado = saldoEsperado;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public List<DetalhePagamentoCaixa> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<DetalhePagamentoCaixa> pagamentos) {
        this.pagamentos = pagamentos;
    }
}