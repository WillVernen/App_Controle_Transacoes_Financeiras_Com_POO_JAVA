package br.com.lab.banco.service;

import br.com.lab.banco.exception.NegocioException;
import br.com.lab.banco.model.Conta;
import br.com.lab.banco.model.Transacao;
import br.com.lab.banco.model.enums.StatusTransacao;
import br.com.lab.banco.model.enums.TipoTransacao;
import br.com.lab.banco.model.record.ComprovantePix;
import br.com.lab.banco.repository.ContaRepository;
import br.com.lab.banco.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransacaoService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public Transacao depositar(String numeroConta, BigDecimal valor) {
        Conta conta = buscarConta(numeroConta);
        try {
            conta.depositar(valor);
            contaRepository.salvar(conta);
            return registrar(TipoTransacao.DEPOSITO, valor, null, conta.getNumero(),
                    StatusTransacao.CONCLUIDA, "Depósito em conta");
        } catch (IllegalArgumentException e) {
            registrar(TipoTransacao.DEPOSITO, valor, null, numeroConta, StatusTransacao.FALHA, e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

    public Transacao sacar(String numeroConta, BigDecimal valor) {
        Conta conta = buscarConta(numeroConta);
        try {
            conta.sacar(valor); // polimorfismo: corrente x poupança
            contaRepository.salvar(conta);
            return registrar(TipoTransacao.SAQUE, valor, conta.getNumero(), null,
                    StatusTransacao.CONCLUIDA, "Saque em conta");
        } catch (IllegalArgumentException e) {
            registrar(TipoTransacao.SAQUE, valor, numeroConta, null, StatusTransacao.FALHA, e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

    public ComprovantePix transferirPix(String numeroOrigem, String chaveDestino, BigDecimal valor) {
        Conta origem = buscarConta(numeroOrigem);
        Conta destino = contaRepository.buscarPorChavePix(chaveDestino)
                .orElseThrow(() -> new NegocioException("Nenhuma conta encontrada para a chave PIX informada."));

        if (origem.getNumero().equals(destino.getNumero())) {
            throw new NegocioException("Não é permitido transferir PIX para a mesma conta.");
        }

        try {
            origem.sacar(valor);
            destino.depositar(valor);
            contaRepository.salvar(origem);
            contaRepository.salvar(destino);

            Transacao transacao = registrar(
                    TipoTransacao.PIX,
                    valor,
                    origem.getNumero(),
                    destino.getNumero(),
                    StatusTransacao.CONCLUIDA,
                    "PIX para chave " + chaveDestino
            );

            return new ComprovantePix(
                    transacao.getId(),
                    chaveDestino,
                    origem.getNumero(),
                    destino.getNumero(),
                    valor,
                    LocalDateTime.now(),
                    StatusTransacao.CONCLUIDA,
                    "Transferência PIX realizada com sucesso."
            );
        } catch (IllegalArgumentException e) {
            registrar(TipoTransacao.PIX, valor, origem.getNumero(), destino.getNumero(),
                    StatusTransacao.FALHA, e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

    public List<Transacao> historicoPorConta(String numeroConta) {
        buscarConta(numeroConta);
        return transacaoRepository.listarPorConta(numeroConta);
    }

    public List<Transacao> historicoGeral() {
        return transacaoRepository.listarTodos();
    }

    public Transacao registrar(
            TipoTransacao tipo,
            BigDecimal valor,
            String origem,
            String destino,
            StatusTransacao status,
            String descricao
    ) {
        Transacao transacao = new Transacao(tipo, valor, origem, destino, status, descricao);
        return transacaoRepository.salvar(transacao);
    }

    private Conta buscarConta(String numero) {
        return contaRepository.buscarPorNumero(numero)
                .orElseThrow(() -> new NegocioException("Conta não encontrada: " + numero));
    }
}
