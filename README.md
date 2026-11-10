# Controle de Transações Financeiras com POO (Java)

Aplicação console em **Java** que simula um sistema bancário básico, desenvolvida para consolidar os pilares da Programação Orientada a Objetos: **encapsulamento**, **herança**, **polimorfismo**, **abstração** e **reuso de código**.

## Funcionalidades

- Cadastro de clientes (com endereço via `record`)
- Abertura de contas **Corrente** e **Poupança**
- Cadastro de chave **PIX** (CPF, e-mail, telefone ou aleatória)
- Depósitos e saques
- Transferências via **PIX** com comprovante
- Criação, rendimento e resgate de **investimentos**
- Histórico de transações
- Demonstração interativa de polimorfismo (taxas por tipo de conta)

## Conceitos de POO aplicados

| Conceito | Onde aparece |
|---|---|
| **Abstração** | Classe abstrata `Conta` e interface `Repositorio<T>` |
| **Herança** | `ContaCorrente` e `ContaPoupanca` herdam de `Conta` |
| **Polimorfismo** | `sacar()` e `calcularTaxaManutencao()` com comportamentos distintos |
| **Encapsulamento** | Atributos privados + Lombok (`@Getter` / `@Setter`) |
| **Composição** | `Investimento` associado a `Conta`; `Cliente` com `Endereco` |
| **Reuso** | Repositório genérico, serviços reutilizados pelo menu |

## Tecnologias

- Java 17+
- Maven
- Lombok
- Enums e Records (Java moderno)
- Persistência **em memória** (simulação de repositórios)

## Estrutura do projeto

```text
src/main/java/br/com/lab/banco/
├── Main.java
├── exception/
├── model/
│   ├── enums/
│   ├── record/
│   ├── Cliente.java
│   ├── Conta.java
│   ├── ContaCorrente.java
│   ├── ContaPoupanca.java
│   ├── Transacao.java
│   └── Investimento.java
├── repository/
├── service/
├── ui/
└── util/
```

## Como executar

### Pré-requisitos

- JDK 17 ou superior
- Maven 3.8+

### Opção A — Maven

```bash
mvn clean compile
mvn exec:java
```

Ou, após empacotar:

```bash
mvn clean package
java -jar target/controle-transacoes-financeiras-1.0.0.jar
```

### Opção B — Scripts PowerShell (Windows)

```powershell
.\scripts\compilar.ps1
.\scripts\executar.ps1
```

Os scripts baixam o Lombok automaticamente (se necessário) e compilam com `javac`.

## Fluxo sugerido de uso (demo)

1. Cadastrar um cliente
2. Abrir uma conta corrente e uma poupança
3. Cadastrar chave PIX em cada conta
4. Depositar valores
5. Realizar um saque
6. Transferir via PIX entre contas
7. Criar um investimento, aplicar rendimento e resgatar
8. Consultar o histórico de transações
9. Usar a opção **Demo de polimorfismo** para ver taxas diferentes

## Exemplo de dados para teste

- CPF: `12345678901`
- Data de nascimento: `15/03/1990`
- E-mail: `cliente@email.com`
- UF: `SP`

## Documentação adicional

- [DOCUMENTACAO.md](DOCUMENTACAO.md) — decisões técnicas e mapeamento dos conceitos POO
- Pasta [`images/`](images/) — espaço para capturas de tela do console

## Aprendizados

Durante o desenvolvimento, foquei em:

- Separar responsabilidades em camadas (`model`, `repository`, `service`, `ui`)
- Usar tipos imutáveis (`record`) para dados de valor
- Centralizar regras de negócio nos serviços
- Representar estados e categorias com `enum`
- Reduzir boilerplate com Lombok, sem perder o encapsulamento

## Autor

Projeto acadêmico de laboratório de Programação Orientada a Objetos em Java.
