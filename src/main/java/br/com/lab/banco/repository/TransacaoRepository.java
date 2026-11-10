package br.com.lab.banco.repository;

import br.com.lab.banco.model.Transacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistência em memória do histórico de transações.
 */
public class TransacaoRepository implements Repositorio<Transacao> {

    private final ConcurrentHashMap<UUID, Transacao> banco = new ConcurrentHashMap<>();

    @Override
    public Transacao salvar(Transacao entidade) {
        banco.put(entidade.getId(), entidade);
        return entidade;
    }

    @Override
    public Optional<Transacao> buscarPorId(UUID id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Transacao> listarTodos() {
        return banco.values().stream()
                .sorted(Comparator.comparing(Transacao::getDataHora).reversed())
                .toList();
    }

    @Override
    public boolean remover(UUID id) {
        return banco.remove(id) != null;
    }

    public List<Transacao> listarPorConta(String numeroConta) {
        return banco.values().stream()
                .filter(t -> numeroConta.equals(t.getContaOrigem()) || numeroConta.equals(t.getContaDestino()))
                .sorted(Comparator.comparing(Transacao::getDataHora).reversed())
                .toList();
    }
}
