package com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer birthYear;
    private Integer deathYear;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    // Constructor vacío requerido por JPA
    public Author() {}

    // Constructor que recibe AuthorData y convierte Strings a Integer
    public Author(AuthorData authorData) {
        this.name = authorData.name();

        // Convertimos de String a Integer de manera segura
        this.birthYear = (authorData.birthYear() != null && !authorData.birthYear().isBlank())
                ? Integer.valueOf(authorData.birthYear())
                : null;

        this.deathYear = (authorData.deathYear() != null && !authorData.deathYear().isBlank())
                ? Integer.valueOf(authorData.deathYear())
                : null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public List<Book> getBooks() {
        return books;
    }
}