package br.com.lab.banco.model;

import br.com.lab.banco.model.enums.TipoConta;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta corrente com cheque especial (herança + polimorfismo).
 */
@Getter
public class ContaCorrente extends Conta {

    private final BigDecimal limiteChequeEspecial;
    private static final BigDecimal TAXA_MANUTENCAO = new BigDecimal("12.90");

    public ContaCorrente(String numero, String agencia, Cliente titular, BigDecimal limiteChequeEspecial) {
        super(numero, agencia, titular, TipoConta.CORRENTE);
        this.limiteChequeEspecial = limiteChequeEspecial == null
                ? BigDecimal.ZERO
                : limiteChequeEspecial.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        BigDecimal disponivel = getSaldo().add(limiteChequeEspecial);
        if (disponivel.compareTo(valor) < 0) {
            throw new IllegalArgumentException(
                    "Saldo + cheque especial insuficiente. Disponível: R$ %.2f".formatted(disponivel)
            );
        }
        atualizarSaldo(getSaldo().subtract(valor));
    }

    @Override
    public BigDecimal calcularTaxaManutencao() {
        return TAXA_MANUTENCAO;
    }
}
