package br.com.lab.banco;

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
import br.com.lab.banco.repository.ClienteRepository;
import br.com.lab.banco.repository.ContaRepository;
import br.com.lab.banco.repository.InvestimentoRepository;
import br.com.lab.banco.repository.TransacaoRepository;
import br.com.lab.banco.service.ClienteService;
import br.com.lab.banco.service.ContaService;
import br.com.lab.banco.service.InvestimentoService;
import br.com.lab.banco.service.TransacaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Fluxo demonstrativo não interativo para testes e capturas de tela.
 */
public class DemoFluxo {

    public static void main(String[] args) {
        ClienteRepository clienteRepository = new ClienteRepository();
        ContaRepository contaRepository = new ContaRepository();
        TransacaoRepository transacaoRepository = new TransacaoRepository();
        InvestimentoRepository investimentoRepository = new InvestimentoRepository();

        ClienteService clienteService = new ClienteService(clienteRepository);
        ContaService contaService = new ContaService(contaRepository);
        TransacaoService transacaoService = new TransacaoService(contaRepository, transacaoRepository);
        InvestimentoService investimentoService = new InvestimentoService(
                contaRepository, investimentoRepository, transacaoService
        );

        screen("01-menu-principal");
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
                Escolha uma opção:
                """);

        screen("02-cadastro-cliente");
        System.out.println("--- Cadastro de Cliente ---");
        Cliente maria = clienteService.cadastrar(
                "Maria Silva",
                "12345678901",
                LocalDate.of(1990, 3, 15),
                "maria@email.com",
                "11999999999",
                new Endereco("Rua das Flores", "100", "Centro", "Sao Paulo", "SP", "01001000")
        );
        Cliente joao = clienteService.cadastrar(
                "Joao Souza",
                "98765432100",
                LocalDate.of(1985, 5, 20),
                "joao@email.com",
                "11888888888",
                new Endereco("Av Brasil", "200", "Jardins", "Sao Paulo", "SP", "01400000")
        );
        System.out.println("Cliente cadastrado com sucesso!");
        System.out.println(maria);
        System.out.println("Endereço: " + maria.getEndereco());
        System.out.println(joao);

        screen("03-abertura-contas");
        System.out.println("--- Abertura de Conta ---");
        Conta corrente = contaService.abrirConta(maria, TipoConta.CORRENTE, new BigDecimal("500"));
        Conta poupanca = contaService.abrirConta(joao, TipoConta.POUPANCA, null);
        System.out.println("Conta aberta com sucesso!");
        System.out.println(corrente);
        System.out.println(poupanca);
        contaService.cadastrarChavePix(corrente.getNumero(), ChavePixTipo.CPF, null);
        contaService.cadastrarChavePix(poupanca.getNumero(), ChavePixTipo.EMAIL, "joao@email.com");
        System.out.println("Chave PIX cadastrada: " + corrente.getChavePix());
        System.out.println("Chave PIX cadastrada: " + poupanca.getChavePix());

        screen("04-depositos");
        System.out.println("--- Depósito ---");
        Transacao d1 = transacaoService.depositar(corrente.getNumero(), new BigDecimal("1000.00"));
        Transacao d2 = transacaoService.depositar(poupanca.getNumero(), new BigDecimal("500.00"));
        System.out.println("Depósito realizado!");
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(corrente);
        System.out.println(poupanca);

        screen("05-transferencia-pix");
        System.out.println("--- Transferência PIX ---");
        ComprovantePix comprovante = transacaoService.transferirPix(
                corrente.getNumero(),
                poupanca.getChavePix(),
                new BigDecimal("150.75")
        );
        System.out.print(comprovante);

        screen("06-investimento");
        System.out.println("--- Novo Investimento ---");
        Investimento investimento = investimentoService.aplicar(
                corrente.getNumero(),
                TipoInvestimento.CDB,
                new BigDecimal("200.00")
        );
        System.out.println("Investimento criado!");
        System.out.println(investimento);

        screen("07-historico-transacoes");
        System.out.println("--- Histórico Geral ---");
        List<Transacao> historico = transacaoService.historicoGeral();
        historico.forEach(System.out::println);

        screen("08-polimorfismo-contas");
        System.out.println("--- Demonstração de Polimorfismo ---");
        for (Conta conta : contaService.listar()) {
            System.out.printf(
                    "Conta %s (%s) -> Taxa de manutenção: R$ %.2f%n",
                    conta.getNumero(),
                    conta.getTipoConta().getDescricao(),
                    conta.calcularTaxaManutencao()
            );
            if (conta instanceof ContaCorrente cc) {
                System.out.println("  Cheque especial: R$ " + cc.getLimiteChequeEspecial());
            } else if (conta instanceof ContaPoupanca cp) {
                System.out.println("  Rendimento mensal simulado: R$ " + cp.simularRendimentoMensal());
            }
        }
        System.out.println("--- Contas ao final do teste ---");
        contaService.listar().forEach(System.out::println);

        screen("FIM");
        System.out.println("TESTE_OK");
    }

    private static void screen(String id) {
        System.out.println();
        System.out.println("@@@SCREEN:" + id + "@@@");
    }
}
