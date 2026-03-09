package com.alura.literalura.view;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.model.BookData;
import com.alura.literalura.model.Results;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.DataConverter;

import java.util.Scanner;

public class Menu {

    private static final String URL_BASE = "https://gutendex.com/books/";

    private ConsumoAPI consumer = new ConsumoAPI();
    private DataConverter converter = new DataConverter();
    private Scanner scanner = new Scanner(System.in);

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    private String json;

    public Menu(BookRepository bookRepository, AuthorRepository authorRepository){
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    private String menu = """
            -------------
            Elija la opción a través de su número:
            1. Buscar libro por título
            2. Listar libros registrados
            3. Listar autores registrados
            4. Listar autores vivos en un determinado año
            5. Listar libros por idioma
            0. Salir
            """;

    private void showMenu(){
        System.out.println(menu);
    }

    public void run(){

        int option = -1;

        while(option != 0){

            showMenu();
            option = Integer.parseInt(scanner.nextLine());

            switch (option){
                case 1:
                    findBookByTitle();
                    break;

                case 2:
                    listRegisteredBooks();
                    break;

                case 3:
                    listRegisteredAuthors();
                    break;

                case 4:
                    listAuthorsAliveByYear();
                    break;

                case 5:
                    listBooksByLanguage();
                    break;

                case 0:
                    System.out.println("Cerrando aplicación...");
                    break;

                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void findBookByTitle() {

        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String title = scanner.nextLine();

        json = consumer.obtenerDatos(URL_BASE + "?search=" + title.replace(" ", "+"));

        BookData bookData = getBookData(title);

        if(bookData != null){

            System.out.println("\nLibro encontrado:");
            System.out.println("Título: " + bookData.title());
            System.out.println("Idiomas: " + bookData.languages());
            System.out.println("Descargas: " + bookData.downloadCount());

            // ---------------------------------
            // Guardar autores y libro en BD
            // ---------------------------------
            for (var authorData : bookData.authors()) {

                // Verifica si el autor ya existe, si no lo crea
                var author = authorRepository.findByName(authorData.name())
                        .orElseGet(() -> authorRepository.save(new Author(authorData)));

                // Verifica si el libro ya existe
                var existingBook = bookRepository.findByTitle(bookData.title());
                if (existingBook.isEmpty()) {
                    var book = new Book(bookData, author);
                    bookRepository.save(book);
                    System.out.println("Libro guardado en la base de datos con autor: " + author.getName());
                } else {
                    System.out.println("El libro ya existe en la base de datos.");
                }
            }

        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    private BookData getBookData(String title) {

        Results data = converter.getData(json, Results.class);

        return data.bookDataList()
                .stream()
                .filter(book -> book.title().toUpperCase().contains(title.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    private void listRegisteredBooks() {

        var books = bookRepository.findAll();

        books.forEach(book ->
                System.out.println(
                        "Título: " + book.getTitle() +
                                " | Autor: " + book.getAuthor().getName() +
                                " | Idioma: " + book.getLanguage() +
                                " | Descargas: " + book.getDownloadCount()
                )
        );
    }

    private void listRegisteredAuthors() {

        var authors = authorRepository.findAll();

        authors.forEach(author ->
                System.out.println(
                        "Autor: " + author.getName() +
                                " | Nacimiento: " + author.getBirthYear() +
                                " | Fallecimiento: " + author.getDeathYear()
                )
        );
    }

    private void listAuthorsAliveByYear(){

        System.out.println("Ingrese el año:");
        int year = Integer.parseInt(scanner.nextLine());

        var authors = authorRepository
                .findByBirthYearLessThanEqualAndDeathYearGreaterThanEqual(year, year);

        authors.forEach(author ->
                System.out.println(author.getName())
        );
    }

    private void listBooksByLanguage() {

        System.out.println("""
        Ingrese el idioma:

        es - español
        en - inglés
        fr - francés
        pt - portugués
        """);

        String language = scanner.nextLine();

        var books = bookRepository.findByLanguage(language);

        books.forEach(book ->
                System.out.println(book.getTitle())
        );
    }
}