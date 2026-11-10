package br.com.lab.banco.model;

import br.com.lab.banco.model.enums.TipoInvestimento;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Investimento associado a uma conta (composição).
 */
@Getter
public class Investimento {

    private final UUID id;
    private final TipoInvestimento tipo;
    private final Conta contaOrigem;
    private final BigDecimal valorAplicado;
    private final LocalDateTime dataAplicacao;
    private BigDecimal valorAtual;
    private boolean ativo;

    public Investimento(TipoInvestimento tipo, Conta contaOrigem, BigDecimal valorAplicado) {
        if (valorAplicado == null || valorAplicado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do investimento deve ser positivo.");
        }
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.contaOrigem = contaOrigem;
        this.valorAplicado = valorAplicado.setScale(2, RoundingMode.HALF_UP);
        this.valorAtual = this.valorAplicado;
        this.dataAplicacao = LocalDateTime.now();
        this.ativo = true;
    }

    public void aplicarRendimentoMensal() {
        if (!ativo) {
            throw new IllegalStateException("Investimento inativo.");
        }
        BigDecimal rendimento = valorAtual.multiply(tipo.getTaxaMensal());
        this.valorAtual = this.valorAtual.add(rendimento).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal resgatar() {
        if (!ativo) {
            throw new IllegalStateException("Investimento já resgatado.");
        }
        this.ativo = false;
        return valorAtual;
    }

    @Override
    public String toString() {
        return "Investimento{%s | Conta: %s | Aplicado: R$ %.2f | Atual: R$ %.2f | Ativo: %s | Data: %s}"
                .formatted(
                        tipo.getDescricao(),
                        contaOrigem.getNumero(),
                        valorAplicado,
                        valorAtual,
                        ativo ? "sim" : "não",
                        dataAplicacao
                );
    }
}
