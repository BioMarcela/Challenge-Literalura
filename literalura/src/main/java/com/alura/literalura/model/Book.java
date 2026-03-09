package com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)  // <- Esto evita duplicados por título
    private String title;

    private String language;
    private Double downloadCount;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book() {}

    public Book(BookData bookData, Author author) {
        this.title = bookData.title();
        List<String> langs = bookData.languages();
        this.language = (langs != null && !langs.isEmpty()) ? langs.get(0) : "Unknown";
        this.downloadCount = (bookData.downloadCount() != null) ? bookData.downloadCount() : 0.0;
        this.author = author;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public Double getDownloadCount() { return downloadCount; }
    public Author getAuthor() { return author; }

}