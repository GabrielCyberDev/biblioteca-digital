import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Registro do histórico de empréstimos (desafio extra). */
public class Emprestimo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String tituloLivro;
    private final String nomeUsuario;
    private final String emailUsuario;
    private final LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;

    public Emprestimo(Livro livro, Usuario usuario) {
        this.tituloLivro = livro.getTitulo();
        this.nomeUsuario = usuario.getNome();
        this.emailUsuario = usuario.getEmail();
        this.dataEmprestimo = LocalDateTime.now();
    }

    public String getTituloLivro() { return tituloLivro; }
    public String getEmailUsuario() { return emailUsuario; }
    public boolean estaAberto() { return dataDevolucao == null; }
    public void registrarDevolucao() { this.dataDevolucao = LocalDateTime.now(); }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("\"%s\" -> %s <%s> | Emprestado em %s | %s",
                tituloLivro, nomeUsuario, emailUsuario, dataEmprestimo.format(f),
                dataDevolucao == null ? "Ainda não devolvido" : "Devolvido em " + dataDevolucao.format(f));
    }
}
