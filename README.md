# Sistema de Gerenciamento de Biblioteca Digital

Aplicação de console em Java (sem dependências) com exceções, serialização, generics, Comparable e Streams.

## Como executar
Requer JDK 17 ou superior.
```bash
cd src
javac -encoding UTF-8 -d ../out *.java
cd ../out && java -cp . Main
```
Os dados são salvos automaticamente em `biblioteca.dat` (pasta de onde o programa é executado) e carregados na inicialização.

## Mapa dos requisitos
- **Exceções:** `LivroIndisponivelException` (empréstimo de livro já emprestado), `RecursoNaoEncontradoException`; uso de
  `try-catch-finally` na leitura/gravação do arquivo e no fluxo principal.
- **Arquivos:** `Livro`, `Usuario` e `Emprestimo` implementam `Serializable`; `Biblioteca.salvar()` / `carregar()`.
- **Generics:** `List<Livro>`, `List<Usuario>`, `List<Emprestimo>`.
- **Comparable:** `Livro implements Comparable<Livro>` (ordem por título).
- **Streams/Lambdas:** busca por título (`filter`), por autor (`filter`), usuário por e-mail (`map` + `filter`),
  ordenação por ano (`sorted` + lambda), agrupamento por autor (`Collectors.groupingBy`).
- **Extras:** listar disponíveis, listar emprestados e histórico de empréstimos (livro, usuário, data e hora).
