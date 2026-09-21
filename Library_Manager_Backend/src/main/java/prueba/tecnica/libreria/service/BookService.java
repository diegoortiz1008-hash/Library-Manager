package prueba.tecnica.libreria.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Export formats the export endpoint is allowed to produce; format never
    // reaches a shell command, it only selects which pure-Java branch runs.
    private static final Set<String> ALLOWED_EXPORT_FORMATS = Set.of("csv", "pdf");

    public final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
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
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Export a book's catalog entry to a report file in the requested format.
    // format is validated against a fixed allow-list and only ever selects
    // which pure-Java content generator runs; it never reaches a shell command.
    public String exportBook(Long bookId, String format) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        String normalizedFormat = format == null ? "" : format.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXPORT_FORMATS.contains(normalizedFormat)) {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }

        String fileName = "book-" + bookId + "." + normalizedFormat;
        String content = "csv".equals(normalizedFormat) ? toCsv(book) : toTextReport(book);

        Path exportsDir = Path.of("exports");
        Files.createDirectories(exportsDir);
        Files.writeString(exportsDir.resolve(fileName), content, StandardCharsets.UTF_8);

        return fileName;
    }

    private String toCsv(Book book) {
        return "id,title,isbn,edition,publicationDate,author\n"
                + book.getId() + "," + csvEscape(book.getTitle()) + "," + csvEscape(book.getIsbn()) + ","
                + csvEscape(book.getEdition()) + "," + book.getPublicationDate() + "," + csvEscape(book.getAuthor())
                + "\n";
    }

    private String csvEscape(String value) {
        return value == null ? "" : "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String toTextReport(Book book) {
        return "Book Report\n"
                + "ID: " + book.getId() + "\n"
                + "Title: " + book.getTitle() + "\n"
                + "ISBN: " + book.getIsbn() + "\n"
                + "Edition: " + book.getEdition() + "\n"
                + "Publication date: " + book.getPublicationDate() + "\n"
                + "Author: " + book.getAuthor() + "\n";
    }

    // Read a book's cover image from disk. filename is resolved against
    // COVERS_DIR and the result must stay inside it after normalization,
    // otherwise the request is rejected (blocks "../" traversal).
    public byte[] getCoverFile(Long bookId, String filename) throws IOException {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        Path coversDir = Path.of(COVERS_DIR).toAbsolutePath().normalize();
        Path resolved = coversDir.resolve(filename).normalize();

        if (!resolved.startsWith(coversDir)) {
            throw new SecurityException("Invalid cover file path: " + filename);
        }

        return Files.readAllBytes(resolved);
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