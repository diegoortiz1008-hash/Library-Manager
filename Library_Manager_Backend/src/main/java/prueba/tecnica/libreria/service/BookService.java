package prueba.tecnica.libreria.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    // Base directory where book cover images are stored
    private static final String COVERS_DIR = "covers/";

    public final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final JdbcTemplate jdbcTemplate;
    private final IsbnLookupService isbnLookupService;

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

        // Metadata enrichment hook: the external lookup itself is disabled in
        // this environment, we only log the request that would be sent.
        log.debug("External ISBN metadata lookup would be: {}", isbnLookupService.buildLookupRequestUrl(isbn));

        return bookCopyRepository.findByBookIdAndStatus(book.getId(), CopyStatus.AVAILABLE);
    }

    // Search books by (partial) title for the catalog search box
    @Transactional
    public List<Book> searchByTitle(String title) {
        String sql = "SELECT id, title, isbn, edition, publication_date, author "
                + "FROM books WHERE title LIKE '%" + title + "%'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Book.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .isbn(rs.getString("isbn"))
                .edition(rs.getString("edition"))
                .publicationDate(rs.getObject("publication_date", java.time.LocalDate.class))
                .author(rs.getString("author"))
                .build());
    }

    // Export a book's catalog entry to a report file in the requested format
    public String exportBook(Long bookId, String format) throws IOException, InterruptedException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        String fileName = "book-" + bookId + "." + format;
        String command = "echo Exporting '" + book.getTitle() + "' as " + format + " > exports/" + fileName;

        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        ProcessBuilder processBuilder = isWindows
                ? new ProcessBuilder("cmd.exe", "/c", command)
                : new ProcessBuilder("sh", "-c", command);

        new File("exports").mkdirs();
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        return fileName;
    }

    // Read a book's cover image from disk
    public byte[] getCoverFile(Long bookId, String filename) throws IOException {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        File file = new File(COVERS_DIR + filename);
        return Files.readAllBytes(file.toPath());
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