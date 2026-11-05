/** Lançada ao tentar emprestar um livro que já está emprestado. */
public class LivroIndisponivelException extends Exception {
    public LivroIndisponivelException(String titulo) {
        super("O livro \"" + titulo + "\" já está emprestado.");
    }
}
