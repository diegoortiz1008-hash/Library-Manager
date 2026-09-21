package prueba.tecnica.libreria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import prueba.tecnica.libreria.exception.BookNotFoundException;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.repository.BookCopyRepository;
import prueba.tecnica.libreria.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookService {

    public final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    // Create a Book
    @Transactional
    public Book createBook(Book book) {
        Book createdBook = Book.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .edition(book.getEdition())
                .publicationDate(book.getPublicationDate())
                .author(book.getAuthor())
                .copies(book.getCopies())
                .build();

        Book savedBook = bookRepository.save(createdBook);
        return savedBook;
    }

    // Register additional physical copies (ejemplares) for an existing Book
    @Transactional
    public List<BookCopy> addCopies(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        List<BookCopy> newCopies = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            newCopies.add(BookCopy.builder()
                    .status(CopyStatus.AVAILABLE)
                    .book(book)
                    .build());
        }

        return bookCopyRepository.saveAll(newCopies);
    }

    // List available copies of a book, looked up by its ISBN
    @Transactional
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with isbn: " + isbn));

        return bookCopyRepository.findByBookIdAndStatus(book.getId(), CopyStatus.AVAILABLE);
    }

    // Update a Book
    @Transactional
    public Book updateBook(Long bookId, Book book) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        existingBook.setTitle(book.getTitle());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setEdition(book.getEdition());
        existingBook.setPublicationDate(book.getPublicationDate());
        existingBook.setAuthor(book.getAuthor());

        Book updatedBook = bookRepository.save(existingBook);
        return updatedBook;
    }
    
    // Delete a Book by ID
    @Transactional
    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }
    // Get a Book by ID
    @Transactional
    public Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
    }

    // Get all Books
    @Transactional
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }


}