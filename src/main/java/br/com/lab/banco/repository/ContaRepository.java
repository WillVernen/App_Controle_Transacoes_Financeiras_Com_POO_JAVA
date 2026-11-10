package br.com.lab.banco.repository;

import br.com.lab.banco.model.Conta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistência em memória de contas.
 */
public class ContaRepository implements Repositorio<Conta> {

    private final ConcurrentHashMap<UUID, Conta> banco = new ConcurrentHashMap<>();

    @Override
    public Conta salvar(Conta entidade) {
        banco.put(entidade.getId(), entidade);
        return entidade;
    }

    @Override
    public Optional<Conta> buscarPorId(UUID id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Conta> listarTodos() {
        return new ArrayList<>(banco.values());
    }

    @Override
    public boolean remover(UUID id) {
        return banco.remove(id) != null;
    }

    public Optional<Conta> buscarPorNumero(String numero) {
        return banco.values().stream()
                .filter(c -> c.getNumero().equals(numero))
                .findFirst();
    }

    public Optional<Conta> buscarPorChavePix(String chavePix) {
        return banco.values().stream()
                .filter(c -> c.getChavePix() != null && c.getChavePix().equalsIgnoreCase(chavePix))
                .findFirst();
    }

    public List<Conta> listarPorCpfTitular(String cpf) {
        return banco.values().stream()
                .filter(c -> c.getTitular().getCpf().equals(cpf))
                .toList();
    }
}
