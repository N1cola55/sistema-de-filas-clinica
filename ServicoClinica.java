/*
 * Trabalho Prático - Sistema de Filas de Atendimento e Consultas
 * Estruturas de Dados - Ciência da Computação
 *
 * Integrantes (nome - RA):
 *   Caio Elias      - 52421171
 *   Gabriel Lorente - 52421216
 *   Nicolas Santos  - 52217939
 */
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Aqui ficam as duas filas e as regras de negócio da clínica.
 * As filas são declaradas pela interface Queue (polimorfismo), então o resto
 * do código não precisa saber qual implementação está sendo usada.
 */
public class ServicoClinica {

    // Possíveis resultados de "chamar próximo na recepção"
    public static final int RECEPCAO_OK = 0;
    public static final int FILA_ATENDIMENTO_VAZIA = 1;
    public static final int FILA_CONSULTAS_CHEIA = 2;

    // Fila 1: LinkedList (FIFO)
    private Queue<Paciente> filaAtendimento = new LinkedList<>();

    // Fila 2: ArrayDeque (FIFO) ou PriorityQueue (bônus)
    private Queue<Paciente> filaConsultas;

    private List<Paciente> atendidos = new ArrayList<>();
    private int limiteFilaConsultas;
    private boolean modoPrioridade;
    private ComparadorPrioridade comparador = new ComparadorPrioridade();

    // Construtor padrão: versão obrigatória (FIFO puro, limite 5)
    public ServicoClinica() {
        this(5, false);
    }

    public ServicoClinica(int limiteFilaConsultas, boolean modoPrioridade) {
        if (limiteFilaConsultas < 1) {
            throw new IllegalArgumentException("O limite da Fila 2 deve ser pelo menos 1.");
        }
        this.limiteFilaConsultas = limiteFilaConsultas;
        this.modoPrioridade = modoPrioridade;

        if (modoPrioridade) {
            filaConsultas = new PriorityQueue<>(comparador);
        } else {
            filaConsultas = new ArrayDeque<>();
        }
    }

    // Regra 1: retirar senha -> o paciente entra na Fila de Atendimento
    public Paciente retirarSenha(String nome, String cpf, boolean prioritario) {
        Paciente p = new Paciente(nome, cpf, prioritario);
        filaAtendimento.offer(p);   // offer: não lança exceção
        return p;
    }

    // Regras 2 e 3: atender na recepção (Fila 1 -> Fila 2) respeitando o limite
    public int chamarProximoRecepcao() {
        if (filaAtendimento.isEmpty()) {
            return FILA_ATENDIMENTO_VAZIA;
        }
        // A Queue não tem tamanho máximo, então controlamos com size()
        if (filaConsultas.size() >= limiteFilaConsultas) {
            return FILA_CONSULTAS_CHEIA;   // o paciente continua na Fila 1
        }
        Paciente p = filaAtendimento.poll();
        filaConsultas.offer(p);
        return RECEPCAO_OK;
    }

    // Regra 4: chamar para consulta -> sai da Fila 2 e vai para a lista de atendidos
    // Retorna null se a Fila 2 estiver vazia
    public Paciente chamarProximoConsulta() {
        Paciente p = filaConsultas.poll();   // poll retorna null se a fila estiver vazia
        if (p != null) {
            p.setHoraConsulta(LocalDateTime.now());
            atendidos.add(p);
        }
        return p;
    }

    // peek: só olha o primeiro, sem remover
    public Paciente verProximoAtendimento() {
        return filaAtendimento.peek();
    }

    public Paciente verProximoConsulta() {
        return filaConsultas.peek();
    }

    public List<Paciente> listarFilaAtendimento() {
        return new ArrayList<>(filaAtendimento);
    }

    // Na PriorityQueue, percorrer a fila NÃO devolve os elementos em ordem.
    // Por isso copiamos para uma lista e ordenamos a cópia.
    public List<Paciente> listarFilaConsultas() {
        List<Paciente> copia = new ArrayList<>(filaConsultas);
        if (modoPrioridade) {
            Collections.sort(copia, comparador);
        }
        return copia;
    }

    public List<Paciente> getAtendidos() {
        return new ArrayList<>(atendidos);
    }

    public int tamanhoFilaAtendimento() {
        return filaAtendimento.size();
    }

    public int tamanhoFilaConsultas() {
        return filaConsultas.size();
    }

    public int getLimiteFilaConsultas() {
        return limiteFilaConsultas;
    }

    public boolean isModoPrioridade() {
        return modoPrioridade;
    }

    // ---- Métodos que LANÇAM exceção: remove() e element() ----

    // Mostra remove() e element() em fila vazia (as exceções são tratadas)
    // e também poll() e peek(), que devolvem null
    public String demonstrarExcecoes() {
        Queue<Paciente> vazia = new LinkedList<>();
        String resultado = "";

        try {
            vazia.remove();
        } catch (NoSuchElementException e) {
            resultado = resultado + "remove()  em fila vazia -> NoSuchElementException tratada.\n";
        }

        try {
            vazia.element();
        } catch (NoSuchElementException e) {
            resultado = resultado + "element() em fila vazia -> NoSuchElementException tratada.\n";
        }

        resultado = resultado + "poll()    em fila vazia -> " + vazia.poll() + "\n";
        resultado = resultado + "peek()    em fila vazia -> " + vazia.peek();
        return resultado;
    }

    // remove() de verdade na Fila 2: lança NoSuchElementException se estiver vazia
    public Paciente removerConsultaComExcecao() {
        return filaConsultas.remove();
    }

    // element() de verdade na Fila 1: lança NoSuchElementException se estiver vazia
    public Paciente consultarAtendimentoComExcecao() {
        return filaAtendimento.element();
    }

    // ---- Relatório ----

    // Tempo médio entre a chegada e a chamada para a consulta, em segundos
    public double tempoMedioEsperaSegundos() {
        if (atendidos.isEmpty()) {
            return 0.0;
        }
        long total = 0;
        for (Paciente p : atendidos) {
            total = total + Duration.between(p.getHoraChegada(), p.getHoraConsulta()).getSeconds();
        }
        return (double) total / atendidos.size();
    }

    public String relatorio() {
        String modo;
        if (modoPrioridade) {
            modo = "ATIVO (PriorityQueue)";
        } else {
            modo = "desligado (ArrayDeque FIFO)";
        }
        return "===== RELATÓRIO =====\n"
                + "Pacientes atendidos (consultados): " + atendidos.size() + "\n"
                + "Tempo médio de espera: " + String.format("%.1f", tempoMedioEsperaSegundos()) + " segundos\n"
                + "Tamanho da Fila de Atendimento: " + filaAtendimento.size() + "\n"
                + "Tamanho da Fila de Consultas: " + filaConsultas.size()
                + " (limite " + limiteFilaConsultas + ")\n"
                + "Modo prioridade na Fila 2: " + modo;
    }
}
