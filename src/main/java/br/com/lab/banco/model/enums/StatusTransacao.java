package br.com.lab.banco.model.enums;

public enum StatusTransacao {
    CONCLUIDA("Concluída"),
    FALHA("Falha"),
    PENDENTE("Pendente");

    private final String descricao;

    StatusTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
