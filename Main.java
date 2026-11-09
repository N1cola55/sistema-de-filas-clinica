/*
 * Trabalho Prático - Sistema de Filas de Atendimento e Consultas
 * Estruturas de Dados - Ciência da Computação
 *
 * Integrantes (nome - RA):
 *   Caio Elias      - 52421171
 *   Gabriel Lorente - 52421216
 *   Nicolas Santos  - 52217939
 */
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu de console da clínica.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Clínica - Sistema de Filas ===");
        int limite = lerInt(sc, "Limite da Fila de Consultas (ex.: 5): ", 1);
        boolean prioridade = lerSimNao(sc, "Ativar atendimento prioritário (bônus) na Fila 2? (s/n): ");
        ServicoClinica clinica = new ServicoClinica(limite, prioridade);

        int opcao;
        do {
            mostrarMenu();
            opcao = lerInt(sc, "Escolha: ", 0);

            switch (opcao) {
                case 1:
                    retirarSenha(sc, clinica);
                    break;
                case 2:
                    chamarRecepcao(clinica);
                    break;
                case 3:
                    chamarConsulta(clinica);
                    break;
                case 4:
                    listar("FILA DE ATENDIMENTO", clinica.listarFilaAtendimento());
                    break;
                case 5:
                    listar("FILA DE CONSULTAS", clinica.listarFilaConsultas());
                    break;
                case 6:
                    verProximos(clinica);
                    break;
                case 7:
                    System.out.println();
                    System.out.println(clinica.relatorio());
                    break;
                case 8:
                    demonstrarExcecoes(clinica);
                    break;
                case 0:
                    System.out.println("Encerrando. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);

        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("1 - Retirar senha");
        System.out.println("2 - Chamar próximo na recepção (Fila 1 -> Fila 2)");
        System.out.println("3 - Chamar próximo para consulta");
        System.out.println("4 - Listar fila de atendimento");
        System.out.println("5 - Listar fila de consultas");
        System.out.println("6 - Ver próximo de cada fila (peek)");
        System.out.println("7 - Relatório (atendidos, tempo médio de espera, tamanho das filas)");
        System.out.println("8 - Demonstrar exceções (remove/element x poll/peek)");
        System.out.println("0 - Sair");
    }

    private static void retirarSenha(Scanner sc, ServicoClinica clinica) {
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("CPF (11 dígitos): ");
        String cpf = sc.nextLine().trim();

        if (nome.isEmpty() || !cpf.matches("\\d{11}")) {
            System.out.println("Dados inválidos: informe um nome e um CPF com 11 dígitos.");
            return;
        }

        boolean prioritario = lerSimNao(sc, "Paciente prioritário (idoso/gestante)? (s/n): ");
        Paciente p = clinica.retirarSenha(nome, cpf, prioritario);
        System.out.println("Senha gerada! " + p);
    }

    private static void chamarRecepcao(ServicoClinica clinica) {
        int resultado = clinica.chamarProximoRecepcao();

        if (resultado == ServicoClinica.RECEPCAO_OK) {
            System.out.println("Paciente encaminhado à Fila de Consultas.");
        } else if (resultado == ServicoClinica.FILA_ATENDIMENTO_VAZIA) {
            System.out.println("Não há ninguém na Fila de Atendimento.");
        } else {
            System.out.println("AVISO: Fila de Consultas cheia (limite "
                    + clinica.getLimiteFilaConsultas()
                    + "). O paciente permanece na Fila de Atendimento.");
        }
    }

    private static void chamarConsulta(ServicoClinica clinica) {
        Paciente p = clinica.chamarProximoConsulta();
        if (p == null) {
            System.out.println("Não há ninguém na Fila de Consultas.");
        } else {
            System.out.println("Chamando para o médico: " + p);
        }
    }

    private static void listar(String titulo, List<Paciente> pacientes) {
        System.out.println();
        System.out.println("--- " + titulo + " (" + pacientes.size() + ") ---");
        if (pacientes.isEmpty()) {
            System.out.println("(vazia)");
            return;
        }
        int posicao = 1;
        for (Paciente p : pacientes) {
            System.out.println(posicao + ". " + p);
            posicao++;
        }
    }

    private static void verProximos(ServicoClinica clinica) {
        Paciente a = clinica.verProximoAtendimento();
        Paciente c = clinica.verProximoConsulta();

        if (a == null) {
            System.out.println("Próximo na Fila de Atendimento: (vazia)");
        } else {
            System.out.println("Próximo na Fila de Atendimento: " + a);
        }

        if (c == null) {
            System.out.println("Próximo na Fila de Consultas:   (vazia)");
        } else {
            System.out.println("Próximo na Fila de Consultas:   " + c);
        }
    }

    private static void demonstrarExcecoes(ServicoClinica clinica) {
        System.out.println();
        System.out.println("--- Em uma fila vazia qualquer ---");
        System.out.println(clinica.demonstrarExcecoes());

        System.out.println();
        System.out.println("--- Na Fila de Consultas real, usando remove() ---");
        try {
            Paciente p = clinica.removerConsultaComExcecao();
            System.out.println("remove() retornou: " + p);
        } catch (NoSuchElementException e) {
            System.out.println("Fila de Consultas vazia: NoSuchElementException tratada.");
        }

        System.out.println();
        System.out.println("--- Na Fila de Atendimento real, usando element() ---");
        try {
            Paciente p = clinica.consultarAtendimentoComExcecao();
            System.out.println("element() retornou: " + p);
        } catch (NoSuchElementException e) {
            System.out.println("Fila de Atendimento vazia: NoSuchElementException tratada.");
        }
    }

    // ---- Métodos para ler do teclado sem o programa quebrar ----

    private static int lerInt(Scanner sc, String mensagem, int minimo) {
        while (true) {
            System.out.print(mensagem);
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor >= minimo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // não era um número, cai na mensagem abaixo
            }
            System.out.println("Digite um número inteiro válido (mínimo " + minimo + ").");
        }
    }

    private static boolean lerSimNao(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String resposta = sc.nextLine().trim().toLowerCase();
            if (resposta.equals("s")) {
                return true;
            }
            if (resposta.equals("n")) {
                return false;
            }
            System.out.println("Responda com 's' ou 'n'.");
        }
    }
}
