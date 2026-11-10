package br.com.lab.banco.service;

import br.com.lab.banco.exception.NegocioException;
import br.com.lab.banco.model.Conta;
import br.com.lab.banco.model.Investimento;
import br.com.lab.banco.model.enums.StatusTransacao;
import br.com.lab.banco.model.enums.TipoInvestimento;
import br.com.lab.banco.model.enums.TipoTransacao;
import br.com.lab.banco.repository.ContaRepository;
import br.com.lab.banco.repository.InvestimentoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class InvestimentoService {

    private final ContaRepository contaRepository;
    private final InvestimentoRepository investimentoRepository;
    private final TransacaoService transacaoService;

    public InvestimentoService(
            ContaRepository contaRepository,
            InvestimentoRepository investimentoRepository,
            TransacaoService transacaoService
    ) {
        this.contaRepository = contaRepository;
        this.investimentoRepository = investimentoRepository;
        this.transacaoService = transacaoService;
    }

    public Investimento aplicar(String numeroConta, TipoInvestimento tipo, BigDecimal valor) {
        Conta conta = contaRepository.buscarPorNumero(numeroConta)
                .orElseThrow(() -> new NegocioException("Conta não encontrada: " + numeroConta));

        try {
            conta.sacar(valor);
            contaRepository.salvar(conta);

            Investimento investimento = new Investimento(tipo, conta, valor);
            investimentoRepository.salvar(investimento);

            transacaoService.registrar(
                    TipoTransacao.INVESTIMENTO,
                    valor,
                    conta.getNumero(),
                    null,
                    StatusTransacao.CONCLUIDA,
                    "Aplicação em " + tipo.getDescricao()
            );

            return investimento;
        } catch (IllegalArgumentException e) {
            transacaoService.registrar(
                    TipoTransacao.INVESTIMENTO,
                    valor,
                    numeroConta,
                    null,
                    StatusTransacao.FALHA,
                    e.getMessage()
            );
            throw new NegocioException(e.getMessage());
        }
    }

    public Investimento aplicarRendimento(UUID idInvestimento) {
        Investimento investimento = investimentoRepository.buscarPorId(idInvestimento)
                .orElseThrow(() -> new NegocioException("Investimento não encontrado."));

        BigDecimal antes = investimento.getValorAtual();
        investimento.aplicarRendimentoMensal();
        investimentoRepository.salvar(investimento);

        BigDecimal rendimento = investimento.getValorAtual().subtract(antes);
        transacaoService.registrar(
                TipoTransacao.RENDIMENTO,
                rendimento,
                null,
                investimento.getContaOrigem().getNumero(),
                StatusTransacao.CONCLUIDA,
                "Rendimento de " + investimento.getTipo().getDescricao()
        );

        return investimento;
    }

    public Investimento resgatar(UUID idInvestimento) {
        Investimento investimento = investimentoRepository.buscarPorId(idInvestimento)
                .orElseThrow(() -> new NegocioException("Investimento não encontrado."));

        BigDecimal valorResgate = investimento.resgatar();
        Conta conta = investimento.getContaOrigem();
        conta.depositar(valorResgate);
        contaRepository.salvar(conta);
        investimentoRepository.salvar(investimento);

        transacaoService.registrar(
                TipoTransacao.DEPOSITO,
                valorResgate,
                null,
                conta.getNumero(),
                StatusTransacao.CONCLUIDA,
                "Resgate de investimento " + investimento.getTipo().getDescricao()
        );

        return investimento;
    }

    public List<Investimento> listar() {
        return investimentoRepository.listarTodos();
    }

    public List<Investimento> listarPorConta(String numeroConta) {
        return investimentoRepository.listarPorConta(numeroConta);
    }
}
