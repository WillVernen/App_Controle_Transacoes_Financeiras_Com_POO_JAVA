package br.com.lab.banco.model;

import br.com.lab.banco.model.enums.TipoConta;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe abstrata Conta: abstração + encapsulamento.
 * Subclasses implementam regras específicas (polimorfismo).
 */
@Getter
public abstract class Conta {

    private final UUID id;
    private final String numero;
    private final String agencia;
    private final Cliente titular;
    private final TipoConta tipoConta;
    private final LocalDateTime dataAbertura;
    private BigDecimal saldo;
    private String chavePix;

    protected Conta(String numero, String agencia, Cliente titular, TipoConta tipoConta) {
        this.id = UUID.randomUUID();
        this.numero = Objects.requireNonNull(numero, "Número da conta é obrigatório.");
        this.agencia = Objects.requireNonNull(agencia, "Agência é obrigatória.");
        this.titular = Objects.requireNonNull(titular, "Titular é obrigatório.");
        this.tipoConta = Objects.requireNonNull(tipoConta, "Tipo da conta é obrigatório.");
        this.dataAbertura = LocalDateTime.now();
        this.saldo = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Saque com regras específicas por tipo de conta (polimorfismo).
     */
    public abstract void sacar(BigDecimal valor);

    /**
     * Taxa de manutenção (polimorfismo).
     */
    public abstract BigDecimal calcularTaxaManutencao();

    protected void debitar(BigDecimal valor) {
        validarValorPositivo(valor);
        if (this.saldo.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para a operação.");
        }
        atualizarSaldo(this.saldo.subtract(valor));
    }

    /**
     * Permite subclasses atualizarem o saldo com regras próprias (ex.: cheque especial).
     */
    protected void atualizarSaldo(BigDecimal novoSaldo) {
        this.saldo = novoSaldo.setScale(2, RoundingMode.HALF_UP);
    }

    public void definirChavePix(String chavePix) {
        if (chavePix == null || chavePix.isBlank()) {
            throw new IllegalArgumentException("Chave PIX inválida.");
        }
        this.chavePix = chavePix.trim();
    }

    protected void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
    }

    @Override
    public String toString() {
        return "%s | Ag: %s | Conta: %s | Titular: %s | Saldo: R$ %.2f | PIX: %s"
                .formatted(
                        tipoConta.getDescricao(),
                        agencia,
                        numero,
                        titular.getNome(),
                        saldo,
                        chavePix == null ? "não cadastrada" : chavePix
                );
    }
}
