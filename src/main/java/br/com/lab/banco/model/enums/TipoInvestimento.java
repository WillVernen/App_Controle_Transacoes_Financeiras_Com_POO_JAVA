package br.com.lab.banco.model.enums;

import java.math.BigDecimal;

public enum TipoInvestimento {
    CDB("CDB", new BigDecimal("0.010")),
    TESOURO_DIRETO("Tesouro Direto", new BigDecimal("0.008")),
    FUNDO_ACOES("Fundo de Ações", new BigDecimal("0.015"));

    private final String descricao;
    private final BigDecimal taxaMensal;

    TipoInvestimento(String descricao, BigDecimal taxaMensal) {
        this.descricao = descricao;
        this.taxaMensal = taxaMensal;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getTaxaMensal() {
        return taxaMensal;
    }
}
