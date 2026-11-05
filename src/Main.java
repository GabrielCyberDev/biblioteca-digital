import java.util.List;
import java.util.Scanner;

/** Menu de console da Biblioteca Digital. */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Biblioteca biblioteca = new Biblioteca("biblioteca.dat");

    public static void main(String[] args) {
        biblioteca.carregar(); // carrega automaticamente ao iniciar
        int opcao = -1;
        try {
            do {
                menu();
                opcao = lerInteiro("Opção: ");
                try {
                    executar(opcao);
                } catch (LivroIndisponivelException | RecursoNaoEncontradoException e) {
                    System.out.println("Erro: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Dados inválidos: " + e.getMessage());
                }
            } while (opcao != 0);
        } finally {
            biblioteca.salvar(); // garante o salvamento ao sair
            scanner.close();
            System.out.println("Dados salvos. Até logo!");
        }
    }

    private static void menu() {
        System.out.println("\n===== BIBLIOTECA DIGITAL =====");
        System.out.println(" 1 - Cadastrar livro");
        System.out.println(" 2 - Listar livros");
        System.out.println(" 3 - Buscar livro por título");
        System.out.println(" 4 - Ordenar livros por título");
        System.out.println(" 5 - Ordenar livros por ano");
        System.out.println(" 6 - Listar livros de um autor");
        System.out.println(" 7 - Agrupar livros por autor");
        System.out.println(" 8 - Cadastrar usuário");
        System.out.println(" 9 - Listar usuários");
        System.out.println("10 - Buscar usuário por e-mail");
        System.out.println("11 - Realizar empréstimo");
        System.out.println("12 - Devolver livro");
        System.out.println("13 - Listar livros disponíveis");
        System.out.println("14 - Listar livros emprestados");
        System.out.println("15 - Histórico de empréstimos");
        System.out.println(" 0 - Sair");
    }

    private static void executar(int opcao) throws LivroIndisponivelException, RecursoNaoEncontradoException {
        switch (opcao) {
            case 1 -> {
                String titulo = ler("Título: ");
                String autor = ler("Autor: ");
                int ano = lerInteiro("Ano de publicação: ");
                System.out.println("Cadastrado: " + biblioteca.cadastrarLivro(titulo, autor, ano));
            }
            case 2 -> imprimir(biblioteca.listarLivros());
            case 3 -> imprimir(biblioteca.buscarPorTitulo(ler("Título (ou parte): ")));
            case 4 -> imprimir(biblioteca.ordenarPorTitulo());
            case 5 -> imprimir(biblioteca.ordenarPorAno());
            case 6 -> imprimir(biblioteca.listarPorAutor(ler("Autor: ")));
            case 7 -> biblioteca.agruparPorAutor().forEach((autor, lista) -> {
                System.out.println(autor + ":");
                lista.forEach(l -> System.out.println("   " + l));
            });
            case 8 -> {
                String nome = ler("Nome: ");
                String email = ler("E-mail: ");
                System.out.println("Cadastrado: " + biblioteca.cadastrarUsuario(nome, email));
            }
            case 9 -> imprimir(biblioteca.listarUsuarios());
            case 10 -> imprimir(biblioteca.buscarUsuariosPorEmail(ler("E-mail: ")));
            case 11 -> {
                String titulo = ler("Título do livro: ");
                String email = ler("E-mail do usuário: ");
                System.out.println("Empréstimo realizado: " + biblioteca.emprestar(titulo, email));
            }
            case 12 -> {
                biblioteca.devolver(ler("Título do livro a devolver: "));
                System.out.println("Livro devolvido com sucesso.");
            }
            case 13 -> imprimir(biblioteca.listarDisponiveis());
            case 14 -> imprimir(biblioteca.listarEmprestados());
            case 15 -> imprimir(biblioteca.getHistorico());
            case 0 -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static <T> void imprimir(List<T> itens) {
        if (itens.isEmpty()) {
            System.out.println("(nenhum resultado)");
        } else {
            itens.forEach(System.out::println);
        }
    }

    private static String ler(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    private static int lerInteiro(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
