package br.com.lab.banco.service;

import br.com.lab.banco.exception.NegocioException;
import br.com.lab.banco.model.Cliente;
import br.com.lab.banco.model.record.Endereco;
import br.com.lab.banco.repository.ClienteRepository;

import java.time.LocalDate;
import java.util.List;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(
            String nome,
            String cpf,
            LocalDate dataNascimento,
            String email,
            String telefone,
            Endereco endereco
    ) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        clienteRepository.buscarPorCpf(cpfLimpo).ifPresent(c -> {
            throw new NegocioException("Já existe cliente cadastrado com o CPF informado.");
        });

        Cliente cliente = new Cliente(nome, cpfLimpo, dataNascimento, email, telefone, endereco);
        return clienteRepository.salvar(cliente);
    }

    public Cliente buscarPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return clienteRepository.buscarPorCpf(cpfLimpo)
                .orElseThrow(() -> new NegocioException("Cliente não encontrado para o CPF informado."));
    }

    public List<Cliente> listar() {
        return clienteRepository.listarTodos();
    }
}
