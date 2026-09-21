package prueba.tecnica.libreria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import prueba.tecnica.libreria.mapper.BookMapper;
import prueba.tecnica.libreria.model.dto.request.BookRequestDTO;
import prueba.tecnica.libreria.model.dto.response.BookCopyResponseDTO;
import prueba.tecnica.libreria.model.dto.response.BookResponseDTO;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.service.BookService;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book Controller", description = "Controller for managing books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @PostMapping("")
    @Operation(summary = "Create a book", description = "Creates a new book in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
    })
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO bookDTO) {
        Book createdBook = bookService.createBook(bookMapper.toEntity(bookDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toDto(createdBook));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates the data of an existing book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<BookResponseDTO> updateBook(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO dto) {

        Book updatedBook = bookService.updateBook(id, bookMapper.toEntity(dto));

        return ResponseEntity.ok(bookMapper.toDto(updatedBook));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes an existing book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<Void> deleteBook(@Parameter(description = "Book ID", example = "1")
                                            @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Retrieves an existing book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<BookResponseDTO> getBookById(@Parameter(description = "Book ID", example = "1")
                                                        @PathVariable Long id) {
        Book book = bookService.findBookById(id);
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all books", description = "Retrieves the list of all books in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class)))
    })
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookMapper.toDtoList(bookService.findAllBooks()));
    }

    @PostMapping("/{id}/copies")
    @Operation(summary = "Add copies to a book", description = "Registers additional physical copies for an existing book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Copies created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCopyResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<List<BookCopyResponseDTO>> addCopies(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Number of copies to add", example = "3") @RequestParam int quantity) {

        List<BookCopyResponseDTO> createdCopies = bookMapper.toCopyDtoList(bookService.addCopies(id, quantity));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCopies);
    }

    @GetMapping("/isbn/{isbn}/copies/available")
    @Operation(summary = "Get available copies by ISBN", description = "Lists the available physical copies of a book, looked up by its ISBN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available copies retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCopyResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<List<BookCopyResponseDTO>> getAvailableCopiesByIsbn(
            @Parameter(description = "Book ISBN", example = "978-0132350884") @PathVariable String isbn) {

        List<BookCopyResponseDTO> availableCopies = bookMapper.toCopyDtoList(bookService.findAvailableCopiesByIsbn(isbn));
        return ResponseEntity.ok(availableCopies);
    }

}
