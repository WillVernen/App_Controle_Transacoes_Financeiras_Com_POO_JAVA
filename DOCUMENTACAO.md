# Documentação Técnica

## 1. Visão geral da arquitetura

A aplicação segue uma organização em camadas simples, adequada a um laboratório de POO:

1. **UI (`MenuConsole`)** — interação com o usuário via terminal  
2. **Service** — regras de negócio (depósito, saque, PIX, investimentos)  
3. **Repository** — persistência simulada em memória  
4. **Model** — entidades, enums e records do domínio  

Essa separação facilita manutenção, testes manuais e entendimento dos papéis de cada classe.

## 2. Modelo de domínio

### Cliente

Representa a pessoa titular das contas. Possui:

- Identificador UUID
- Dados pessoais
- Endereço (`record Endereco`)

### Conta (abstrata)

Define o contrato comum:

- depósito
- saque (abstrato)
- cálculo de taxa de manutenção (abstrato)
- chave PIX

#### ContaCorrente

- Possui **cheque especial**
- Permite saldo negativo até o limite
- Cobra taxa de manutenção fixa

#### ContaPoupanca

- Não possui cheque especial
- Saque apenas com saldo suficiente
- Isenta de taxa
- Possui simulação de rendimento mensal

### Transacao

Registra cada operação financeira com tipo, valor, contas envolvidas, status e descrição.

### Investimento

Composição com `Conta`: o valor é debitado da conta na aplicação e creditado no resgate.

## 3. Enums utilizados

- `TipoConta`
- `TipoTransacao`
- `StatusTransacao`
- `TipoInvestimento` (com taxa mensal embutida)
- `ChavePixTipo`

Enums ajudam a evitar “números mágicos” e strings soltas no código.

## 4. Records

- `Endereco` — valor imutável com validação no construtor compacto
- `ComprovantePix` — comprovante imutável gerado após transferência

## 5. Repositórios em memória

Todos implementam `Repositorio<T>`:

- `ClienteRepository`
- `ContaRepository`
- `TransacaoRepository`
- `InvestimentoRepository`

A estrutura usa `ConcurrentHashMap` para armazenar entidades em RAM, simulando persistência sem banco de dados.

## 6. Serviços e regras principais

### Depósito

- Valida valor positivo
- Credita saldo
- Registra histórico

### Saque

- Usa polimorfismo: cada subclasse de `Conta` aplica sua regra
- Conta corrente considera cheque especial
- Conta poupança exige saldo disponível

### PIX

1. Localiza conta de origem pelo número  
2. Localiza destino pela chave PIX  
3. Debita origem e credita destino  
4. Gera `ComprovantePix`  
5. Registra a transação  

### Investimentos

- Aplicação debita a conta
- Rendimento mensal atualiza `valorAtual`
- Resgate devolve o valor atual para a conta e inativa o investimento

## 7. Lombok

Uso pontual de:

- `@Getter`
- `@Setter` (apenas campos mutáveis autorizados)

Objetivo: reduzir boilerplate sem esconder a lógica de negócio.

## 8. Tratamento de erros

- `IllegalArgumentException` nas entidades (invariantes do domínio)
- `NegocioException` nos serviços (regras da aplicação)
- Menu captura exceções e exibe mensagens amigáveis no console

## 9. Como evidenciar os pilares de POO na defesa/apresentação

1. Mostre `Conta` abstrata e as duas subclasses  
2. Execute saques em corrente e poupança com saldo baixo  
3. Use a opção 12 do menu para comparar taxas  
4. Explique o repositório genérico como exemplo de abstração/reuso  
5. Mostre `Endereco` e `ComprovantePix` como records  

## 10. Limitações conscientes do laboratório

- Dados ficam apenas em memória (reinício zera o estado)
- Não há autenticação/login
- Não há integração com APIs bancárias reais
- Validações de CPF/e-mail são básicas (didáticas)

Essas limitações são intencionais para manter o foco nos conceitos de POO.
