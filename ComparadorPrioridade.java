/*
 * Trabalho Prático - Sistema de Filas de Atendimento e Consultas
 * Estruturas de Dados - Ciência da Computação
 *
 * Integrantes (nome - RA):
 *   Caio Elias      - 52421171
 *   Gabriel Lorente - 52421216
 *   Nicolas Santos  - 52217939
 */
import java.util.Comparator;

/**
 * Comparator usado pela PriorityQueue da Fila de Consultas (bônus).
 * Regra: paciente prioritário vem primeiro. Se os dois forem iguais
 * (os dois prioritários ou os dois normais), vem primeiro quem tem a menor senha.
 */
public class ComparadorPrioridade implements Comparator<Paciente> {

    @Override
    public int compare(Paciente a, Paciente b) {
        // a é prioritário e b não: a vem antes (número negativo)
        if (a.isPrioritario() && !b.isPrioritario()) {
            return -1;
        }
        // b é prioritário e a não: b vem antes (número positivo)
        if (!a.isPrioritario() && b.isPrioritario()) {
            return 1;
        }
        // empate na prioridade: desempata pela senha (quem chegou antes)
        return Integer.compare(a.getSenha(), b.getSenha());
    }
}
