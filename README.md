# Sistema de Filas de Atendimento e Consultas

Trabalho prático de **Estruturas de Dados** (Ciência da Computação), feito em Java com a interface `java.util.Queue`.

## Integrantes

| Nome | RA |
| --- | --- |
| Caio Elias | 52421171 |
| Gabriel Lorente | 52421216 |
| Nicolas Santos | 52217939 |

## O que o sistema faz

Uma clínica tem duas filas independentes:

```
Chegada -> [Fila 1: Atendimento] -> recepção -> [Fila 2: Consultas] -> médico
```

- O paciente retira uma senha e entra na **Fila de Atendimento** (`LinkedList`).
- Na recepção, ele sai da Fila 1 e vai para a **Fila de Consultas** (`ArrayDeque`).
- A Fila 2 tem limite de N pacientes (controlado com `size()`). Se estiver cheia, o paciente continua na Fila 1 e o sistema avisa.
- Quando o médico chama, o paciente sai da Fila 2 e vai para uma lista de atendidos.
- **Bônus:** com o modo prioridade ligado, a Fila 2 vira uma `PriorityQueue` (prioritário primeiro e, se empatar, menor senha).

## Estrutura do projeto

```
.
├── Paciente.java             dados do paciente e senha automática
├── ComparadorPrioridade.java regra de prioridade (bônus)
├── ServicoClinica.java       as duas filas e as regras de negócio
├── Main.java                 menu de console (Scanner)
├── TestesClinica.java        testes (7 casos, 28 verificações)
├── compilar.bat / compilar.sh   scripts para compilar
└── README.md
```

## Como compilar e rodar

É preciso ter o JDK instalado (versão 17 ou mais nova).

**Windows:** dê dois cliques em `compilar.bat` (ou rode no terminal). **Linux/Mac:** `./compilar.sh`.

Ou, manualmente, na pasta do projeto:

```
javac -encoding UTF-8 *.java
java Main
```

No Windows, para os acentos aparecerem certos no terminal, rode antes `chcp 65001`.

## Como rodar os testes

Na pasta do projeto, depois de compilar:

```
java TestesClinica
```

A última linha deve ser: `Resultado: 28 passaram, 0 falharam.`

## Menu

```
1 - Retirar senha
2 - Chamar próximo na recepção (Fila 1 -> Fila 2)
3 - Chamar próximo para consulta
4 - Listar fila de atendimento
5 - Listar fila de consultas
6 - Ver próximo de cada fila (peek)
7 - Relatório (atendidos, tempo médio de espera, tamanho das filas)
8 - Demonstrar exceções (remove/element x poll/peek)
0 - Sair
```

## Métodos da Queue usados

| Operação | Lança exceção | Retorna valor especial |
| --- | --- | --- |
| Inserir | `add()` | `offer()` |
| Remover | `remove()` | `poll()` |
| Consultar | `element()` | `peek()` |

No fluxo normal usamos `offer`, `poll` e `peek`. A opção 8 do menu mostra `remove` e `element` lançando `NoSuchElementException` (tratada com `try/catch`).
