/** Lançada quando um livro ou usuário não é encontrado. */
public class RecursoNaoEncontradoException extends Exception {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
