package br.com.lab.banco;

import br.com.lab.banco.repository.ClienteRepository;
import br.com.lab.banco.repository.ContaRepository;
import br.com.lab.banco.repository.InvestimentoRepository;
import br.com.lab.banco.repository.TransacaoRepository;
import br.com.lab.banco.service.ClienteService;
import br.com.lab.banco.service.ContaService;
import br.com.lab.banco.service.InvestimentoService;
import br.com.lab.banco.service.TransacaoService;
import br.com.lab.banco.ui.MenuConsole;

import java.util.Scanner;

/**
 * Ponto de entrada da aplicação de Controle de Transações Financeiras.
 */
public class Main {

    public static void main(String[] args) {
        ClienteRepository clienteRepository = new ClienteRepository();
        ContaRepository contaRepository = new ContaRepository();
        TransacaoRepository transacaoRepository = new TransacaoRepository();
        InvestimentoRepository investimentoRepository = new InvestimentoRepository();

        ClienteService clienteService = new ClienteService(clienteRepository);
        ContaService contaService = new ContaService(contaRepository);
        TransacaoService transacaoService = new TransacaoService(contaRepository, transacaoRepository);
        InvestimentoService investimentoService = new InvestimentoService(
                contaRepository,
                investimentoRepository,
                transacaoService
        );

        try (Scanner scanner = new Scanner(System.in)) {
            MenuConsole menu = new MenuConsole(
                    scanner,
                    clienteService,
                    contaService,
                    transacaoService,
                    investimentoService
            );
            menu.iniciar();
        }
    }
}
