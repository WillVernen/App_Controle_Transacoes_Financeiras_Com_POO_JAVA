package br.com.lab.banco.model;

import br.com.lab.banco.model.enums.TipoConta;

import java.math.BigDecimal;

/**
 * Conta poupança sem cheque especial e isenta de taxa (herança + polimorfismo).
 */
public class ContaPoupanca extends Conta {

    private static final BigDecimal RENDIMENTO_MENSAL = new BigDecimal("0.005");

    public ContaPoupanca(String numero, String agencia, Cliente titular) {
        super(numero, agencia, titular, TipoConta.POUPANCA);
    }

    @Override
    public void sacar(BigDecimal valor) {
        debitar(valor);
    }

    @Override
    public BigDecimal calcularTaxaManutencao() {
        return BigDecimal.ZERO;
    }

    public BigDecimal simularRendimentoMensal() {
        return getSaldo().multiply(RENDIMENTO_MENSAL);
    }
}
