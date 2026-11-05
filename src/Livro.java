import java.io.Serializable;

/** Livro da biblioteca. Ordenação natural (Comparable): por título, sem diferenciar maiúsculas. */
public class Livro implements Serializable, Comparable<Livro> {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private String autor;
    private int anoPublicacao;
    private boolean emprestado;

    public Livro(String titulo, String autor, int anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.emprestado = false;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public boolean isEmprestado() { return emprestado; }
    public void setEmprestado(boolean emprestado) { this.emprestado = emprestado; }

    /** Marca o livro como emprestado. */
    public void emprestar() throws LivroIndisponivelException {
        if (emprestado) {
            throw new LivroIndisponivelException(titulo);
        }
        emprestado = true;
    }

    /** Marca o livro como disponível. */
    public void devolver() {
        emprestado = false;
    }

    @Override
    public int compareTo(Livro outro) {
        return this.titulo.compareToIgnoreCase(outro.titulo);
    }

    @Override
    public String toString() {
        return String.format("\"%s\" - %s (%d) [%s]", titulo, autor, anoPublicacao,
                emprestado ? "Emprestado" : "Disponível");
    }
}
