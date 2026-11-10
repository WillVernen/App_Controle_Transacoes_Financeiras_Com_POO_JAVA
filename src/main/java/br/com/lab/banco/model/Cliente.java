package br.com.lab.banco.model;

import br.com.lab.banco.model.record.Endereco;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Cliente com encapsulamento via Lombok.
 */
@Getter
public class Cliente {

    private final UUID id;
    private final String nome;
    private final String cpf;
    private final LocalDate dataNascimento;

    @Setter
    private String email;

    @Setter
    private String telefone;

    @Setter
    private Endereco endereco;

    public Cliente(String nome, String cpf, LocalDate dataNascimento, String email, String telefone, Endereco endereco) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos numéricos.");
        }
        this.id = UUID.randomUUID();
        this.nome = nome.trim();
        this.cpf = cpf;
        this.dataNascimento = Objects.requireNonNull(dataNascimento, "Data de nascimento é obrigatória.");
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return "Cliente{id=%s, nome='%s', cpf='%s', email='%s'}".formatted(id, nome, cpf, email);
    }
}
