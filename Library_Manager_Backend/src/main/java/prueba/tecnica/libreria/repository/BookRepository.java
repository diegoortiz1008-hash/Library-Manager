package prueba.tecnica.libreria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prueba.tecnica.libreria.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

}
