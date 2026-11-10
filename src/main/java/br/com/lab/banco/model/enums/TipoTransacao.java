package br.com.lab.banco.model.enums;

public enum TipoTransacao {
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    PIX("Transferência PIX"),
    INVESTIMENTO("Aplicação em Investimento"),
    RENDIMENTO("Rendimento de Investimento");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
