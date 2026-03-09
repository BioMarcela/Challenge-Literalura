package com.alura.literalura.repository;

import com.alura.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Método existente para autores vivos en un año
    List<Author> findByBirthYearLessThanEqualAndDeathYearGreaterThanEqual(Integer year1, Integer year2);

    // Nuevo método para buscar autor por nombre
    Optional<Author> findByName(String name);
}
