package br.com.lab.banco.model.enums;

public enum ChavePixTipo {
    CPF("CPF"),
    EMAIL("E-mail"),
    TELEFONE("Telefone"),
    ALEATORIA("Chave Aleatória");

    private final String descricao;

    ChavePixTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
