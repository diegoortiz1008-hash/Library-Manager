package prueba.tecnica.libreria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prueba.tecnica.libreria.exception.BookNotFoundException;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.repository.BookCopyRepository;
import prueba.tecnica.libreria.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBookShouldPersistCopiedBook() {
        Book input = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.createBook(input);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book savedBook = captor.getValue();
        assertNotSame(input, savedBook);
        assertEquals(input.getTitle(), savedBook.getTitle());
        assertEquals(input.getIsbn(), savedBook.getIsbn());
        assertEquals(input.getEdition(), savedBook.getEdition());
        assertEquals(input.getPublicationDate(), savedBook.getPublicationDate());
        assertEquals(input.getAuthor(), savedBook.getAuthor());
        assertEquals(savedBook, result);
    }

    @Test
    void addCopiesShouldCreateAvailableCopiesForExistingBook() {
        Book existing = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        List<BookCopy> savedCopies = List.of(
                copy(10L, existing, CopyStatus.AVAILABLE),
                copy(11L, existing, CopyStatus.AVAILABLE));
        when(bookCopyRepository.saveAll(any())).thenReturn(savedCopies);

        List<BookCopy> result = bookService.addCopies(1L, 2);

        ArgumentCaptor<List<BookCopy>> captor = ArgumentCaptor.forClass(List.class);
        verify(bookCopyRepository).saveAll(captor.capture());
        List<BookCopy> generatedCopies = captor.getValue();
        assertEquals(2, generatedCopies.size());
        assertEquals(CopyStatus.AVAILABLE, generatedCopies.get(0).getStatus());
        assertEquals(existing, generatedCopies.get(0).getBook());
        assertEquals(savedCopies, result);
    }

    @Test
    void findAvailableCopiesByIsbnShouldReturnAvailableCopies() {
        Book existing = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.of(existing));
        List<BookCopy> copies = List.of(copy(10L, existing, CopyStatus.AVAILABLE));
        when(bookCopyRepository.findByBookIdAndStatus(1L, CopyStatus.AVAILABLE)).thenReturn(copies);

        List<BookCopy> result = bookService.findAvailableCopiesByIsbn("9780132350884");

        assertEquals(copies, result);
    }

    @Test
    void findAvailableCopiesByIsbnShouldThrowWhenBookIsMissing() {
        when(bookRepository.findByIsbn("missing")).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                () -> bookService.findAvailableCopiesByIsbn("missing"));

        assertEquals("Book not found with isbn: missing", exception.getMessage());
    }

    @Test
    void updateBookShouldModifyExistingBook() {
        Book existing = book(1L, "Old Title", "old-isbn", "Old Edition", LocalDate.of(2000, 1, 1), "Old Author");
        Book update = book(null, "New Title", "new-isbn", "New Edition", LocalDate.of(2024, 2, 2), "New Author");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.updateBook(1L, update);

        verify(bookRepository).save(existing);
        assertEquals("New Title", result.getTitle());
        assertEquals("new-isbn", result.getIsbn());
        assertEquals("New Edition", result.getEdition());
        assertEquals(LocalDate.of(2024, 2, 2), result.getPublicationDate());
        assertEquals("New Author", result.getAuthor());
    }

    @Test
    void deleteBookShouldCallRepositoryDeleteById() {
        bookService.deleteBook(7L);

        verify(bookRepository).deleteById(7L);
    }

    @Test
    void findBookByIdShouldThrowWhenMissing() {
        when(bookRepository.findById(7L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.findBookById(7L));

        assertEquals("Book not found with id: 7", exception.getMessage());
    }

    @Test
    void findAllBooksShouldReturnRepositoryIterable() {
        List<Book> books = List.of(
            book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin"),
            book(2L, "Domain-Driven Design", "9780321125217", "1st Edition", LocalDate.of(2003, 8, 30), "Eric Evans"));
        when(bookRepository.findAll()).thenReturn(books);

        Iterable<Book> result = bookService.findAllBooks();

        assertEquals(books, result);
    }

    private Book book(Long id, String title, String isbn, String edition, LocalDate publicationDate, String author) {
        return Book.builder()
                .id(id)
                .title(title)
                .isbn(isbn)
                .edition(edition)
                .publicationDate(publicationDate)
                .author(author)
                .build();
    }

    private BookCopy copy(Long id, Book book, CopyStatus status) {
        return BookCopy.builder()
                .id(id)
                .book(book)
                .status(status)
                .build();
    }
}