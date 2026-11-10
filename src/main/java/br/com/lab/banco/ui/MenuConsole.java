package br.com.lab.banco.ui;

import br.com.lab.banco.exception.NegocioException;
import br.com.lab.banco.model.Cliente;
import br.com.lab.banco.model.Conta;
import br.com.lab.banco.model.ContaCorrente;
import br.com.lab.banco.model.ContaPoupanca;
import br.com.lab.banco.model.Investimento;
import br.com.lab.banco.model.Transacao;
import br.com.lab.banco.model.enums.ChavePixTipo;
import br.com.lab.banco.model.enums.TipoConta;
import br.com.lab.banco.model.enums.TipoInvestimento;
import br.com.lab.banco.model.record.ComprovantePix;
import br.com.lab.banco.model.record.Endereco;
import br.com.lab.banco.service.ClienteService;
import br.com.lab.banco.service.ContaService;
import br.com.lab.banco.service.InvestimentoService;
import br.com.lab.banco.service.TransacaoService;
import br.com.lab.banco.util.ConsoleUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Interface de usuário via console com menus e fluxos interativos.
 */
public class MenuConsole {

    private final Scanner scanner;
    private final ClienteService clienteService;
    private final ContaService contaService;
    private final TransacaoService transacaoService;
    private final InvestimentoService investimentoService;

    public MenuConsole(
            Scanner scanner,
            ClienteService clienteService,
            ContaService contaService,
            TransacaoService transacaoService,
            InvestimentoService investimentoService
    ) {
        this.scanner = scanner;
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.transacaoService = transacaoService;
        this.investimentoService = investimentoService;
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirMenuPrincipal();
            int opcao = ConsoleUtil.lerInteiro(scanner, "Escolha uma opção: ");
            try {
                switch (opcao) {
                    case 1 -> cadastrarCliente();
                    case 2 -> listarClientes();
                    case 3 -> abrirConta();
                    case 4 -> listarContas();
                    case 5 -> cadastrarChavePix();
                    case 6 -> depositar();
                    case 7 -> sacar();
                    case 8 -> transferirPix();
                    case 9 -> criarInvestimento();
                    case 10 -> gerenciarInvestimentos();
                    case 11 -> consultarHistorico();
                    case 12 -> demonstrarPolimorfismo();
                    case 0 -> {
                        System.out.println("Encerrando o sistema. Até logo!");
                        executando = false;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NegocioException | IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }

            if (executando) {
                ConsoleUtil.pausar(scanner);
            }
        }
    }

    private void exibirMenuPrincipal() {
        ConsoleUtil.limparTela();
        System.out.println("""
                ================================================
                  CONTROLE DE TRANSAÇÕES FINANCEIRAS (POO JAVA)
                ================================================
                 1  - Cadastrar cliente
                 2  - Listar clientes
                 3  - Abrir conta
                 4  - Listar contas
                 5  - Cadastrar chave PIX
                 6  - Depositar
                 7  - Sacar
                 8  - Transferência PIX
                 9  - Criar investimento
                10  - Gerenciar investimentos
                11  - Histórico de transações
                12  - Demo de polimorfismo (taxas)
                 0  - Sair
                ================================================
                """);
    }

    private void cadastrarCliente() {
        System.out.println("--- Cadastro de Cliente ---");
        String nome = ConsoleUtil.lerTexto(scanner, "Nome: ");
        String cpf = ConsoleUtil.lerTexto(scanner, "CPF (somente números): ");
        LocalDate nascimento = ConsoleUtil.lerData(scanner, "Data de nascimento (dd/MM/yyyy): ");
        String email = ConsoleUtil.lerTexto(scanner, "E-mail: ");
        String telefone = ConsoleUtil.lerTexto(scanner, "Telefone: ");

        String logradouro = ConsoleUtil.lerTexto(scanner, "Logradouro: ");
        String numero = ConsoleUtil.lerTexto(scanner, "Número: ");
        String bairro = ConsoleUtil.lerTexto(scanner, "Bairro: ");
        String cidade = ConsoleUtil.lerTexto(scanner, "Cidade: ");
        String uf = ConsoleUtil.lerTexto(scanner, "UF (2 letras): ");
        String cep = ConsoleUtil.lerTexto(scanner, "CEP: ");

        Endereco endereco = new Endereco(logradouro, numero, bairro, cidade, uf, cep);
        Cliente cliente = clienteService.cadastrar(nome, cpf, nascimento, email, telefone, endereco);
        System.out.println("Cliente cadastrado com sucesso!");
        System.out.println(cliente);
        System.out.println("Endereço: " + cliente.getEndereco());
    }

    private void listarClientes() {
        List<Cliente> clientes = clienteService.listar();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("--- Clientes ---");
        clientes.forEach(System.out::println);
    }

    private void abrirConta() {
        System.out.println("--- Abertura de Conta ---");
        String cpf = ConsoleUtil.lerTexto(scanner, "CPF do titular: ");
        Cliente titular = clienteService.buscarPorCpf(cpf);

        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupança");
        int tipo = ConsoleUtil.lerInteiro(scanner, "Tipo da conta: ");

        Conta conta;
        if (tipo == 1) {
            BigDecimal limite = ConsoleUtil.lerDecimal(scanner, "Limite do cheque especial: ");
            conta = contaService.abrirConta(titular, TipoConta.CORRENTE, limite);
        } else if (tipo == 2) {
            conta = contaService.abrirConta(titular, TipoConta.POUPANCA, null);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }

        System.out.println("Conta aberta com sucesso!");
        System.out.println(conta);
    }

    private void listarContas() {
        List<Conta> contas = contaService.listar();
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }
        System.out.println("--- Contas ---");
        contas.forEach(System.out::println);
    }

    private void cadastrarChavePix() {
        System.out.println("--- Cadastro de Chave PIX ---");
        String numero = ConsoleUtil.lerTexto(scanner, "Número da conta: ");

        System.out.println("1 - CPF");
        System.out.println("2 - E-mail");
        System.out.println("3 - Telefone");
        System.out.println("4 - Aleatória");
        int opcao = ConsoleUtil.lerInteiro(scanner, "Tipo da chave: ");

        ChavePixTipo tipo;
        String valor = null;
        switch (opcao) {
            case 1 -> tipo = ChavePixTipo.CPF;
            case 2 -> {
                tipo = ChavePixTipo.EMAIL;
                valor = ConsoleUtil.lerTexto(scanner, "Informe o e-mail: ");
            }
            case 3 -> {
                tipo = ChavePixTipo.TELEFONE;
                valor = ConsoleUtil.lerTexto(scanner, "Informe o telefone: ");
            }
            case 4 -> tipo = ChavePixTipo.ALEATORIA;
            default -> {
                System.out.println("Opção inválida.");
                return;
            }
        }

        Conta conta = contaService.cadastrarChavePix(numero, tipo, valor);
        System.out.println("Chave PIX cadastrada: " + conta.getChavePix());
    }

    private void depositar() {
        System.out.println("--- Depósito ---");
        String numero = ConsoleUtil.lerTexto(scanner, "Número da conta: ");
        BigDecimal valor = ConsoleUtil.lerDecimal(scanner, "Valor: ");
        Transacao t = transacaoService.depositar(numero, valor);
        System.out.println("Depósito realizado!");
        System.out.println(t);
        System.out.println(contaService.buscarPorNumero(numero));
    }

    private void sacar() {
        System.out.println("--- Saque ---");
        String numero = ConsoleUtil.lerTexto(scanner, "Número da conta: ");
        BigDecimal valor = ConsoleUtil.lerDecimal(scanner, "Valor: ");
        Transacao t = transacaoService.sacar(numero, valor);
        System.out.println("Saque realizado!");
        System.out.println(t);
        System.out.println(contaService.buscarPorNumero(numero));
    }

    private void transferirPix() {
        System.out.println("--- Transferência PIX ---");
        String origem = ConsoleUtil.lerTexto(scanner, "Conta de origem: ");
        String chave = ConsoleUtil.lerTexto(scanner, "Chave PIX de destino: ");
        BigDecimal valor = ConsoleUtil.lerDecimal(scanner, "Valor: ");
        ComprovantePix comprovante = transacaoService.transferirPix(origem, chave, valor);
        System.out.println(comprovante);
    }

    private void criarInvestimento() {
        System.out.println("--- Novo Investimento ---");
        String numero = ConsoleUtil.lerTexto(scanner, "Número da conta: ");

        TipoInvestimento[] tipos = TipoInvestimento.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("%d - %s (taxa mensal: %.2f%%)%n",
                    i + 1, tipos[i].getDescricao(), tipos[i].getTaxaMensal().multiply(BigDecimal.valueOf(100)));
        }
        int escolha = ConsoleUtil.lerInteiro(scanner, "Escolha o tipo: ");
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Tipo inválido.");
            return;
        }

        BigDecimal valor = ConsoleUtil.lerDecimal(scanner, "Valor a aplicar: ");
        Investimento investimento = investimentoService.aplicar(numero, tipos[escolha - 1], valor);
        System.out.println("Investimento criado!");
        System.out.println(investimento);
    }

    private void gerenciarInvestimentos() {
        System.out.println("--- Investimentos ---");
        List<Investimento> lista = investimentoService.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum investimento cadastrado.");
            return;
        }
        lista.forEach(i -> System.out.println(i.getId() + " | " + i));

        System.out.println("1 - Aplicar rendimento mensal");
        System.out.println("2 - Resgatar investimento");
        System.out.println("0 - Voltar");
        int opcao = ConsoleUtil.lerInteiro(scanner, "Opção: ");

        if (opcao == 0) {
            return;
        }

        String idTexto = ConsoleUtil.lerTexto(scanner, "ID do investimento: ");
        UUID id = UUID.fromString(idTexto);

        if (opcao == 1) {
            Investimento atualizado = investimentoService.aplicarRendimento(id);
            System.out.println("Rendimento aplicado!");
            System.out.println(atualizado);
        } else if (opcao == 2) {
            Investimento resgatado = investimentoService.resgatar(id);
            System.out.println("Investimento resgatado!");
            System.out.println(resgatado);
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private void consultarHistorico() {
        System.out.println("--- Histórico ---");
        System.out.println("1 - Por conta");
        System.out.println("2 - Geral");
        int opcao = ConsoleUtil.lerInteiro(scanner, "Opção: ");

        List<Transacao> historico;
        if (opcao == 1) {
            String numero = ConsoleUtil.lerTexto(scanner, "Número da conta: ");
            historico = transacaoService.historicoPorConta(numero);
        } else if (opcao == 2) {
            historico = transacaoService.historicoGeral();
        } else {
            System.out.println("Opção inválida.");
            return;
        }

        if (historico.isEmpty()) {
            System.out.println("Nenhuma transação encontrada.");
            return;
        }
        historico.forEach(System.out::println);
    }

    /**
     * Demonstra polimorfismo: mesma mensagem, comportamentos diferentes.
     */
    private void demonstrarPolimorfismo() {
        System.out.println("--- Demonstração de Polimorfismo ---");
        List<Conta> contas = contaService.listar();
        if (contas.isEmpty()) {
            System.out.println("Cadastre ao menos uma conta para visualizar o exemplo.");
            return;
        }

        for (Conta conta : contas) {
            System.out.printf(
                    "Conta %s (%s) -> Taxa de manutenção: R$ %.2f%n",
                    conta.getNumero(),
                    conta.getTipoConta().getDescricao(),
                    conta.calcularTaxaManutencao()
            );

            if (conta instanceof ContaCorrente corrente) {
                System.out.println("  Cheque especial: R$ " + corrente.getLimiteChequeEspecial());
            } else if (conta instanceof ContaPoupanca poupanca) {
                System.out.println("  Rendimento mensal simulado: R$ " + poupanca.simularRendimentoMensal());
            }
        }
    }
}
