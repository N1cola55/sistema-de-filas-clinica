/*
 * Trabalho Prático - Sistema de Filas de Atendimento e Consultas
 * Estruturas de Dados - Ciência da Computação
 *
 * Integrantes (nome - RA):
 *   Caio Elias      - 52421171
 *   Gabriel Lorente - 52421216
 *   Nicolas Santos  - 52217939
 */
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Testes feitos "na mão", sem JUnit (rodam com: java TestesClinica).
 * Cada teste cria um ServicoClinica novo e reinicia a numeração das senhas.
 */
public class TestesClinica {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {
        teste1FilasVazias();
        teste2Fila2Cheia();
        teste3FluxoCompleto();
        teste4RemoveEmFilaVazia();
        teste5OrdemFifo();
        teste6PrioridadeNaFila2();
        teste7PacienteBarradoFicaNaFila1();

        System.out.println();
        System.out.println("Resultado: " + passou + " passaram, " + falhou + " falharam.");
        if (falhou > 0) {
            System.exit(1);
        }
    }

    // Teste 1: filas vazias não quebram o programa
    static void teste1FilasVazias() {
        ServicoClinica c = novoServico(5, false);

        verifica("T1 recepção com Fila 1 vazia",
                c.chamarProximoRecepcao() == ServicoClinica.FILA_ATENDIMENTO_VAZIA);
        verifica("T1 consulta com Fila 2 vazia retorna null",
                c.chamarProximoConsulta() == null);
        verifica("T1 peek nas duas filas vazias retorna null",
                c.verProximoAtendimento() == null && c.verProximoConsulta() == null);
        verifica("T1 tempo médio com 0 atendidos é 0",
                c.tempoMedioEsperaSegundos() == 0.0);
    }

    // Teste 2: Fila 2 cheia, o paciente não passa
    static void teste2Fila2Cheia() {
        ServicoClinica c = novoServico(2, false);
        for (int i = 0; i < 3; i++) {
            c.retirarSenha("P" + i, "12345678901", false);
        }

        verifica("T2 1º paciente passa",
                c.chamarProximoRecepcao() == ServicoClinica.RECEPCAO_OK);
        verifica("T2 2º paciente passa",
                c.chamarProximoRecepcao() == ServicoClinica.RECEPCAO_OK);
        verifica("T2 3º paciente é barrado (Fila 2 cheia)",
                c.chamarProximoRecepcao() == ServicoClinica.FILA_CONSULTAS_CHEIA);
        verifica("T2 Fila 2 continua com 2",
                c.tamanhoFilaConsultas() == 2);
    }

    // Teste 3: fluxo completo de um paciente
    static void teste3FluxoCompleto() {
        ServicoClinica c = novoServico(5, false);
        Paciente p = c.retirarSenha("Ana", "12345678901", false);

        verifica("T3 entrou na Fila 1", c.tamanhoFilaAtendimento() == 1);

        c.chamarProximoRecepcao();
        verifica("T3 saiu da Fila 1 e entrou na Fila 2",
                c.tamanhoFilaAtendimento() == 0 && c.tamanhoFilaConsultas() == 1);

        Paciente chamado = c.chamarProximoConsulta();
        verifica("T3 o médico chamou o mesmo paciente", chamado == p);
        verifica("T3 foi para a lista de atendidos",
                c.getAtendidos().size() == 1 && c.tamanhoFilaConsultas() == 0);
        verifica("T3 hora da consulta foi registrada", p.getHoraConsulta() != null);
    }

    // Teste 4: remove() e element() em fila vazia lançam exceção (e tratamos)
    static void teste4RemoveEmFilaVazia() {
        ServicoClinica c = novoServico(5, false);

        boolean lancou = false;
        try {
            c.removerConsultaComExcecao();
        } catch (NoSuchElementException e) {
            lancou = true;
        }
        verifica("T4 remove() em fila vazia lança NoSuchElementException", lancou);

        lancou = false;
        try {
            c.consultarAtendimentoComExcecao();
        } catch (NoSuchElementException e) {
            lancou = true;
        }
        verifica("T4 element() em fila vazia lança NoSuchElementException", lancou);

        verifica("T4 demonstrarExcecoes() roda sem quebrar",
                c.demonstrarExcecoes().contains("tratada"));
    }

    // Teste 5: a ordem FIFO é mantida nas duas filas
    static void teste5OrdemFifo() {
        ServicoClinica c = novoServico(5, false);
        Paciente a = c.retirarSenha("A", "11111111111", false);
        Paciente b = c.retirarSenha("B", "22222222222", false);
        Paciente d = c.retirarSenha("C", "33333333333", false);

        verifica("T5 Fila 1 na ordem de chegada",
                c.listarFilaAtendimento().equals(Arrays.asList(a, b, d)));

        c.chamarProximoRecepcao();
        c.chamarProximoRecepcao();
        c.chamarProximoRecepcao();

        verifica("T5 Fila 2 na ordem de chegada",
                c.listarFilaConsultas().equals(Arrays.asList(a, b, d)));
        verifica("T5 consulta chama A", c.chamarProximoConsulta() == a);
        verifica("T5 consulta chama B", c.chamarProximoConsulta() == b);
        verifica("T5 consulta chama C", c.chamarProximoConsulta() == d);
    }

    // Teste 6 (bônus): prioritário passa na frente na Fila 2
    static void teste6PrioridadeNaFila2() {
        ServicoClinica c = novoServico(5, true);
        Paciente normal1 = c.retirarSenha("Normal1", "11111111111", false);
        Paciente idoso = c.retirarSenha("Idoso", "22222222222", true);
        Paciente normal2 = c.retirarSenha("Normal2", "33333333333", false);
        Paciente gestante = c.retirarSenha("Gestante", "44444444444", true);

        for (int i = 0; i < 4; i++) {
            c.chamarProximoRecepcao();
        }

        verifica("T6 listagem: prioritários primeiro, depois por senha",
                c.listarFilaConsultas().equals(Arrays.asList(idoso, gestante, normal1, normal2)));
        verifica("T6 1º chamado é o idoso", c.chamarProximoConsulta() == idoso);
        verifica("T6 2º chamado é a gestante", c.chamarProximoConsulta() == gestante);
        verifica("T6 3º chamado é Normal1", c.chamarProximoConsulta() == normal1);
        verifica("T6 4º chamado é Normal2", c.chamarProximoConsulta() == normal2);
    }

    // Teste 7: quem foi barrado continua na Fila 1 e passa quando abre vaga
    static void teste7PacienteBarradoFicaNaFila1() {
        ServicoClinica c = novoServico(1, false);
        Paciente a = c.retirarSenha("A", "11111111111", false);
        Paciente b = c.retirarSenha("B", "22222222222", false);

        c.chamarProximoRecepcao();   // A passa
        c.chamarProximoRecepcao();   // B é barrado

        verifica("T7 B continua no começo da Fila 1", c.verProximoAtendimento() == b);

        c.chamarProximoConsulta();   // libera vaga
        boolean bPassou = c.chamarProximoRecepcao() == ServicoClinica.RECEPCAO_OK
                && c.verProximoConsulta() == b;
        verifica("T7 B passa quando abre vaga", bPassou);
    }

    // ---- métodos auxiliares ----

    private static ServicoClinica novoServico(int limite, boolean prioridade) {
        Paciente.reiniciarContador();
        return new ServicoClinica(limite, prioridade);
    }

    private static void verifica(String descricao, boolean condicao) {
        if (condicao) {
            passou++;
            System.out.println("[OK]    " + descricao);
        } else {
            falhou++;
            System.out.println("[FALHA] " + descricao);
        }
    }
}
