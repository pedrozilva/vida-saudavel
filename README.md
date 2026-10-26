# Vida Saudável — Sistema de Gestão

Trabalho da P2 (POO). Sistema desktop (Java + Swing) para a empresa **Vida
Saudável**, que gerencia clientes, avaliações físicas, planos de
treino/nutrição, agenda de consultas e pagamentos de assinaturas
(Básico/VIP).

## Requisitos

- **Java 17** ou superior (JDK)
- **Maven 3.8+**

Nenhuma credencial, chave de API ou serviço externo é necessário — por
isso não há `.env.example` neste repositório.

## Como executar

```bash
mvn clean package
java -jar target/vida-saudavel.jar
```

O comando `mvn package` baixa as dependências (Gson e JUnit), compila o
projeto e gera um `.jar` executável já com as dependências embutidas
(plugin `maven-shade-plugin`).

Na primeira execução o sistema cria a pasta `data/` com os arquivos
`clientes.json`, `consultas.json`, `pagamentos.json` e `profissionais.json`
(estes dois últimos profissionais são cadastrados automaticamente para a
Agenda não começar vazia). Se qualquer um desses arquivos não existir, o
sistema simplesmente começa com uma lista vazia — não quebra (RNF03).

## Como rodar os testes

```bash
mvn test
```

Os testes unitários (JUnit 5) cobrem os 12 casos do plano de testes do
documento de requisitos (`TC01` a `TC12`): cálculo e classificação de IMC,
meta calórica por objetivo, intervalo de revisão por tipo de assinatura,
valor de consulta extra, conflito de horário, bloqueio por inadimplência,
feedback de progresso, multa por atraso, arquivo de dados ausente e
validação de campos obrigatórios.

## Estrutura do projeto

```
src/main/java/com/vidasaudavel/
├── Main.java            — ponto de entrada
├── model/                — entidades (Pessoa, Cliente, Assinatura, Plano, ...)
├── service/               — regras de negócio (cálculos, validações)
├── persistence/           — leitura/gravação dos arquivos JSON (Gson)
├── ui/                    — telas Swing (MainFrame + 4 painéis)
└── util / exception/      — apoio (IMC, datas) e exceções de domínio
src/test/java/...          — testes JUnit 5
data/                      — arquivos JSON gerados em tempo de execução
```

Mais detalhes sobre a arquitetura, o modelo de classes (POO) e as
decisões de design estão no documento de requisitos do projeto.

## Telas

- **Clientes** — cadastro, edição, remoção e histórico de avaliações físicas.
- **Plano** — definição de assinatura (Básico/VIP), geração de plano de
  treino/nutrição com meta calórica calculada, feedback de progresso.
- **Agenda** — agendamento de consultas com profissionais, com bloqueio de
  conflito de horário e de clientes inadimplentes.
- **Relatórios** — mensalidades, pagamentos e relatório financeiro
  (exportável em `.txt`).

## Bibliotecas utilizadas

| Biblioteca | Uso | Motivo |
| --- | --- | --- |
| Gson 2.11 | Serialização JSON | Mapeamento quase direto dos objetos Java para arquivo, legível e fácil de depurar |
| JUnit 5 | Testes unitários | Framework padrão para testes em Java, integrado ao Maven |

## Limitações conhecidas

- A geração de relatório é em texto (`.txt`), não em PDF — o enunciado
  aceita "PDF/console" e optamos por manter o projeto sem dependências
  pesadas de geração de PDF.
- O "desafio" de persistir os dados no Google Drive não foi implementado
  nesta entrega.
