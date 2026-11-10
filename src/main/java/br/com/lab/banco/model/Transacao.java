package br.com.lab.banco.model;

import br.com.lab.banco.model.enums.StatusTransacao;
import br.com.lab.banco.model.enums.TipoTransacao;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma movimentação financeira registrada no histórico.
 */
@Getter
public class Transacao {

    private final UUID id;
    private final TipoTransacao tipo;
    private final BigDecimal valor;
    private final LocalDateTime dataHora;
    private final String contaOrigem;
    private final String contaDestino;
    private final StatusTransacao status;
    private final String descricao;

    public Transacao(
            TipoTransacao tipo,
            BigDecimal valor,
            String contaOrigem,
            String contaDestino,
            StatusTransacao status,
            String descricao
    ) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.status = status;
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "[%s] %s | R$ %.2f | Origem: %s | Destino: %s | Status: %s | %s"
                .formatted(
                        dataHora,
                        tipo.getDescricao(),
                        valor,
                        contaOrigem == null ? "-" : contaOrigem,
                        contaDestino == null ? "-" : contaDestino,
                        status.getDescricao(),
                        descricao
                );
    }
}
