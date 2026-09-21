package prueba.tecnica.libreria.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a book")
public class BookRequestDTO {

    @Schema(description = "Book title", example = "Clean Code")
    private String title;

    @Schema(description = "Book ISBN", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Book edition", example = "1st Edition")
    private String edition;

    @Schema(description = "Publication date", example = "2008-08-01")
    private LocalDate publicationDate;

    @Schema(description = "Book author", example = "Robert C. Martin")
    private String author;
}
