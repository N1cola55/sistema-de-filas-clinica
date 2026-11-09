/*
 * Trabalho Prático - Sistema de Filas de Atendimento e Consultas
 * Estruturas de Dados - Ciência da Computação
 *
 * Integrantes (nome - RA):
 *   Caio Elias      - 52421171
 *   Gabriel Lorente - 52421216
 *   Nicolas Santos  - 52217939
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa um paciente da clínica.
 * A senha é sequencial e gerada automaticamente quando o paciente é criado.
 */
public class Paciente {

    // contador compartilhado por todos os pacientes (static)
    private static int proximaSenha = 1;

    private int senha;
    private String nome;
    private String cpf;
    private LocalDateTime horaChegada;
    private LocalDateTime horaConsulta;   // só é preenchida quando o médico chama
    private boolean prioritario;          // bônus: idosos, gestantes...

    public Paciente(String nome, String cpf, boolean prioritario) {
        this.senha = proximaSenha;
        proximaSenha++;
        this.nome = nome;
        this.cpf = cpf;
        this.prioritario = prioritario;
        this.horaChegada = LocalDateTime.now();
    }

    // Usado só nos testes, para a senha voltar a começar do 1
    static void reiniciarContador() {
        proximaSenha = 1;
    }

    public int getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDateTime getHoraChegada() {
        return horaChegada;
    }

    public LocalDateTime getHoraConsulta() {
        return horaConsulta;
    }

    public void setHoraConsulta(LocalDateTime horaConsulta) {
        this.horaConsulta = horaConsulta;
    }

    public boolean isPrioritario() {
        return prioritario;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String texto = "Senha " + String.format("%03d", senha)
                + " | " + nome
                + " | CPF: " + cpf
                + " | Chegada: " + horaChegada.format(formato);
        if (prioritario) {
            texto = texto + " | PRIORITÁRIO";
        }
        return texto;
    }
}
