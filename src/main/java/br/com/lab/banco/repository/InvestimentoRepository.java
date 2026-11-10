package br.com.lab.banco.repository;

import br.com.lab.banco.model.Investimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistência em memória de investimentos.
 */
public class InvestimentoRepository implements Repositorio<Investimento> {

    private final ConcurrentHashMap<UUID, Investimento> banco = new ConcurrentHashMap<>();

    @Override
    public Investimento salvar(Investimento entidade) {
        banco.put(entidade.getId(), entidade);
        return entidade;
    }

    @Override
    public Optional<Investimento> buscarPorId(UUID id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Investimento> listarTodos() {
        return new ArrayList<>(banco.values());
    }

    @Override
    public boolean remover(UUID id) {
        return banco.remove(id) != null;
    }

    public List<Investimento> listarPorConta(String numeroConta) {
        return banco.values().stream()
                .filter(i -> i.getContaOrigem().getNumero().equals(numeroConta))
                .toList();
    }

    public List<Investimento> listarAtivos() {
        return banco.values().stream()
                .filter(Investimento::isAtivo)
                .toList();
    }
}
