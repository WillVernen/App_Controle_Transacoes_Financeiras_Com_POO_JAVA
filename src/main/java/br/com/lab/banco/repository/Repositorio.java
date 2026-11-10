package br.com.lab.banco.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato genérico de repositório (abstração + reuso).
 */
public interface Repositorio<T> {
    T salvar(T entidade);

    Optional<T> buscarPorId(UUID id);

    List<T> listarTodos();

    boolean remover(UUID id);
}
