import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Regras da biblioteca: cadastros, buscas (Streams), empréstimos e persistência em .dat. */
public class Biblioteca {

    private final String arquivo;
    private List<Livro> livros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Emprestimo> historico = new ArrayList<>();

    public Biblioteca(String arquivo) {
        this.arquivo = arquivo;
    }

    // ------------------------- Livros -------------------------

    public Livro cadastrarLivro(String titulo, String autor, int ano) {
        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Título e autor são obrigatórios.");
        }
        if (ano <= 0) {
            throw new IllegalArgumentException("Ano de publicação inválido.");
        }
        Livro livro = new Livro(titulo.trim(), autor.trim(), ano);
        livros.add(livro);
        salvar();
        return livro;
    }

    public List<Livro> listarLivros() {
        return new ArrayList<>(livros);
    }

    /** Busca por título (parte do texto, case insensitive) usando Stream. */
    public List<Livro> buscarPorTitulo(String termo) {
        return livros.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(termo.trim().toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Ordenação natural: por título (Comparable). */
    public List<Livro> ordenarPorTitulo() {
        return livros.stream().sorted().collect(Collectors.toList());
    }

    /** Ordenação por ano com sorted + lambda. */
    public List<Livro> ordenarPorAno() {
        return livros.stream()
                .sorted((a, b) -> Integer.compare(a.getAnoPublicacao(), b.getAnoPublicacao()))
                .collect(Collectors.toList());
    }

    /** Livros de um autor (filter). */
    public List<Livro> listarPorAutor(String autor) {
        return livros.stream()
                .filter(l -> l.getAutor().equalsIgnoreCase(autor.trim()))
                .collect(Collectors.toList());
    }

    /** Agrupa livros por autor (Collectors.groupingBy), com autores em ordem alfabética. */
    public Map<String, List<Livro>> agruparPorAutor() {
        return livros.stream()
                .sorted(Comparator.comparing(Livro::getAutor, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.groupingBy(Livro::getAutor,
                        java.util.TreeMap::new, Collectors.toList()));
    }

    public List<Livro> listarDisponiveis() {
        return livros.stream().filter(l -> !l.isEmprestado()).collect(Collectors.toList());
    }

    public List<Livro> listarEmprestados() {
        return livros.stream().filter(Livro::isEmprestado).collect(Collectors.toList());
    }

    // ------------------------- Usuários -------------------------

    public Usuario cadastrarUsuario(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        boolean existe = usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email.trim()));
        if (existe) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }
        Usuario usuario = new Usuario(nome.trim(), email.trim());
        usuarios.add(usuario);
        salvar();
        return usuario;
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    /** Busca usuários por e-mail usando map + filter (compara e-mails em minúsculas). */
    public List<Usuario> buscarUsuariosPorEmail(String email) {
        String alvo = email.trim().toLowerCase();
        return usuarios.stream()
                .filter(u -> Optional.of(u.getEmail()).map(String::toLowerCase).filter(alvo::equals).isPresent())
                .collect(Collectors.toList());
    }

    // ------------------------- Empréstimos -------------------------

    public Emprestimo emprestar(String tituloLivro, String emailUsuario)
            throws LivroIndisponivelException, RecursoNaoEncontradoException {
        Livro livro = livros.stream()
                .filter(l -> l.getTitulo().equalsIgnoreCase(tituloLivro.trim()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado: " + tituloLivro));
        Usuario usuario = buscarUsuariosPorEmail(emailUsuario).stream()
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + emailUsuario));

        livro.emprestar(); // lança LivroIndisponivelException se já estiver emprestado
        Emprestimo emprestimo = new Emprestimo(livro, usuario);
        historico.add(emprestimo);
        salvar();
        return emprestimo;
    }

    public void devolver(String tituloLivro) throws RecursoNaoEncontradoException {
        Livro livro = livros.stream()
                .filter(l -> l.getTitulo().equalsIgnoreCase(tituloLivro.trim()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado: " + tituloLivro));
        if (!livro.isEmprestado()) {
            throw new RecursoNaoEncontradoException("O livro \"" + livro.getTitulo() + "\" não está emprestado.");
        }
        livro.devolver();
        historico.stream()
                .filter(e -> e.estaAberto() && e.getTituloLivro().equalsIgnoreCase(livro.getTitulo()))
                .forEach(Emprestimo::registrarDevolucao);
        salvar();
    }

    public List<Emprestimo> getHistorico() {
        return new ArrayList<>(historico);
    }

    // ------------------------- Persistência (.dat) -------------------------

    /** Serializa livros, usuários e histórico no arquivo .dat. */
    public void salvar() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(arquivo));
            out.writeObject(livros);
            out.writeObject(usuarios);
            out.writeObject(historico);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        } finally {
            fechar(out);
        }
    }

    /** Carrega os dados do .dat, se o arquivo existir. */
    @SuppressWarnings("unchecked")
    public void carregar() {
        File f = new File(arquivo);
        if (!f.exists()) {
            return;
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(f));
            livros = (List<Livro>) in.readObject();
            usuarios = (List<Usuario>) in.readObject();
            historico = (List<Emprestimo>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar dados (iniciando vazio): " + e.getMessage());
        } finally {
            fechar(in);
        }
    }

    private void fechar(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar arquivo: " + e.getMessage());
            }
        }
    }
}
