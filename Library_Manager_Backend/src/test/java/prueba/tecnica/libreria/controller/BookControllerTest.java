package prueba.tecnica.libreria.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prueba.tecnica.libreria.mapper.BookMapper;
import prueba.tecnica.libreria.model.dto.request.BookRequestDTO;
import prueba.tecnica.libreria.model.dto.response.BookCopyResponseDTO;
import prueba.tecnica.libreria.model.dto.response.BookResponseDTO;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookController bookController;

    @Test
    void createBookShouldReturnCreatedBook() {
        BookRequestDTO request = bookRequest("Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        Book entity = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        BookResponseDTO response = bookResponse(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        when(bookMapper.toEntity(request)).thenReturn(entity);
        when(bookService.createBook(entity)).thenReturn(entity);
        when(bookMapper.toDto(entity)).thenReturn(response);

        var result = bookController.createBook(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(bookService).createBook(entity);
    }

    @Test
    void getAllBooksShouldReturnMappedBooks() {
        List<Book> books = List.of(book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin"));
        List<BookResponseDTO> responses = List.of(bookResponse(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin"));
        when(bookService.findAllBooks()).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(responses);

        var result = bookController.getAllBooks();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(responses, result.getBody());
    }

    @Test
    void addCopiesShouldReturnCreatedCopies() {
        Book book = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        BookCopy copy = bookCopy(10L, book, CopyStatus.AVAILABLE);
        BookCopyResponseDTO response = bookCopyResponse(10L, CopyStatus.AVAILABLE, 1L);
        when(bookService.addCopies(1L, 2)).thenReturn(List.of(copy));
        when(bookMapper.toCopyDtoList(List.of(copy))).thenReturn(List.of(response));

        var result = bookController.addCopies(1L, 2);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(List.of(response), result.getBody());
    }

    @Test
    void getAvailableCopiesByIsbnShouldReturnMappedCopies() {
        Book book = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        BookCopy copy = bookCopy(10L, book, CopyStatus.AVAILABLE);
        BookCopyResponseDTO response = bookCopyResponse(10L, CopyStatus.AVAILABLE, 1L);
        when(bookService.findAvailableCopiesByIsbn("9780132350884")).thenReturn(List.of(copy));
        when(bookMapper.toCopyDtoList(List.of(copy))).thenReturn(List.of(response));

        var result = bookController.getAvailableCopiesByIsbn("9780132350884");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(List.of(response), result.getBody());
    }

    @Test
    void updateBookShouldReturnUpdatedBook() {
        BookRequestDTO request = bookRequest("Domain-Driven Design", "9780321125217", "1st Edition", LocalDate.of(2003, 8, 30), "Eric Evans");
        Book entity = book(2L, "Domain-Driven Design", "9780321125217", "1st Edition", LocalDate.of(2003, 8, 30), "Eric Evans");
        BookResponseDTO response = bookResponse(2L, "Domain-Driven Design", "9780321125217", "1st Edition", LocalDate.of(2003, 8, 30), "Eric Evans");
        when(bookMapper.toEntity(request)).thenReturn(entity);
        when(bookService.updateBook(2L, entity)).thenReturn(entity);
        when(bookMapper.toDto(entity)).thenReturn(response);

        var result = bookController.updateBook(2L, request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteBookShouldReturnNoContent() {
        var result = bookController.deleteBook(3L);

        assertEquals(204, result.getStatusCodeValue());
        verify(bookService).deleteBook(3L);
    }

    @Test
    void getBookByIdShouldReturnMappedBook() {
        Book entity = book(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        BookResponseDTO response = bookResponse(1L, "Clean Code", "9780132350884", "1st Edition", LocalDate.of(2008, 8, 1), "Robert C. Martin");
        when(bookService.findBookById(1L)).thenReturn(entity);
        when(bookMapper.toDto(entity)).thenReturn(response);

        var result = bookController.getBookById(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    private BookRequestDTO bookRequest(String title, String isbn, String edition, LocalDate publicationDate, String author) {
        return BookRequestDTO.builder()
                .title(title)
                .isbn(isbn)
                .edition(edition)
                .publicationDate(publicationDate)
                .author(author)
                .build();
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

    private BookResponseDTO bookResponse(Long id, String title, String isbn, String edition, LocalDate publicationDate, String author) {
        return BookResponseDTO.builder()
                .id(id)
                .title(title)
                .isbn(isbn)
                .edition(edition)
                .publicationDate(publicationDate)
                .author(author)
                .build();
    }

    private BookCopy bookCopy(Long id, Book book, CopyStatus status) {
        return BookCopy.builder()
                .id(id)
                .book(book)
                .status(status)
                .build();
    }

    private BookCopyResponseDTO bookCopyResponse(Long id, CopyStatus status, Long bookId) {
        return BookCopyResponseDTO.builder()
                .id(id)
                .status(status)
                .bookId(bookId)
                .build();
    }
}