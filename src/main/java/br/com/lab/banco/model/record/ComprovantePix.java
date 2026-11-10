package br.com.lab.banco.model.record;

import br.com.lab.banco.model.enums.StatusTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comprovante imutável gerado após uma transferência PIX.
 */
public record ComprovantePix(
        UUID idTransacao,
        String chaveDestino,
        String contaOrigem,
        String contaDestino,
        BigDecimal valor,
        LocalDateTime dataHora,
        StatusTransacao status,
        String mensagem
) {
    @Override
    public String toString() {
        return """
                ===== COMPROVANTE PIX =====
                ID: %s
                Origem: %s
                Destino: %s
                Chave: %s
                Valor: R$ %.2f
                Data/Hora: %s
                Status: %s
                Mensagem: %s
                ===========================
                """.formatted(
                idTransacao,
                contaOrigem,
                contaDestino,
                chaveDestino,
                valor,
                dataHora,
                status.getDescricao(),
                mensagem
        );
    }
}
