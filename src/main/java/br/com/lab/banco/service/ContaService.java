package br.com.lab.banco.service;

import br.com.lab.banco.exception.NegocioException;
import br.com.lab.banco.model.Cliente;
import br.com.lab.banco.model.Conta;
import br.com.lab.banco.model.ContaCorrente;
import br.com.lab.banco.model.ContaPoupanca;
import br.com.lab.banco.model.enums.ChavePixTipo;
import br.com.lab.banco.model.enums.TipoConta;
import br.com.lab.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ContaService {

    private final ContaRepository contaRepository;
    private final AtomicInteger sequencial = new AtomicInteger(1000);

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public Conta abrirConta(Cliente titular, TipoConta tipoConta, BigDecimal limiteChequeEspecial) {
        String numero = String.valueOf(sequencial.incrementAndGet());
        String agencia = "0001";

        Conta conta = switch (tipoConta) {
            case CORRENTE -> new ContaCorrente(numero, agencia, titular, limiteChequeEspecial);
            case POUPANCA -> new ContaPoupanca(numero, agencia, titular);
        };

        return contaRepository.salvar(conta);
    }

    public Conta buscarPorNumero(String numero) {
        return contaRepository.buscarPorNumero(numero)
                .orElseThrow(() -> new NegocioException("Conta não encontrada: " + numero));
    }

    public List<Conta> listar() {
        return contaRepository.listarTodos();
    }

    public List<Conta> listarPorCpf(String cpf) {
        return contaRepository.listarPorCpfTitular(cpf.replaceAll("\\D", ""));
    }

    public Conta cadastrarChavePix(String numeroConta, ChavePixTipo tipo, String valorChave) {
        Conta conta = buscarPorNumero(numeroConta);
        String chave = gerarChave(tipo, valorChave, conta);

        contaRepository.buscarPorChavePix(chave).ifPresent(c -> {
            throw new NegocioException("Chave PIX já está em uso por outra conta.");
        });

        conta.definirChavePix(chave);
        return contaRepository.salvar(conta);
    }

    private String gerarChave(ChavePixTipo tipo, String valorChave, Conta conta) {
        return switch (tipo) {
            case CPF -> {
                String cpf = conta.getTitular().getCpf();
                yield cpf;
            }
            case EMAIL -> {
                if (valorChave == null || !valorChave.contains("@")) {
                    throw new NegocioException("E-mail inválido para chave PIX.");
                }
                yield valorChave.trim().toLowerCase();
            }
            case TELEFONE -> {
                String tel = valorChave == null ? "" : valorChave.replaceAll("\\D", "");
                if (tel.length() < 10) {
                    throw new NegocioException("Telefone inválido para chave PIX.");
                }
                yield "+55" + tel;
            }
            case ALEATORIA -> UUID.randomUUID().toString();
        };
    }
}
