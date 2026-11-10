package br.com.lab.banco.repository;

import br.com.lab.banco.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistência em memória de clientes.
 */
public class ClienteRepository implements Repositorio<Cliente> {

    private final ConcurrentHashMap<UUID, Cliente> banco = new ConcurrentHashMap<>();

    @Override
    public Cliente salvar(Cliente entidade) {
        banco.put(entidade.getId(), entidade);
        return entidade;
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(banco.values());
    }

    @Override
    public boolean remover(UUID id) {
        return banco.remove(id) != null;
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return banco.values().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }
}
