package br.com.lab.banco.model.record;

/**
 * Record imutável representando o endereço do cliente.
 */
public record Endereco(
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String uf,
        String cep
) {
    public Endereco {
        if (logradouro == null || logradouro.isBlank()) {
            throw new IllegalArgumentException("Logradouro é obrigatório.");
        }
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("Cidade é obrigatória.");
        }
        if (uf == null || uf.isBlank() || uf.length() != 2) {
            throw new IllegalArgumentException("UF inválida.");
        }
    }

    @Override
    public String toString() {
        return "%s, %s - %s, %s/%s - CEP %s"
                .formatted(logradouro, numero, bairro, cidade, uf.toUpperCase(), cep);
    }
}
