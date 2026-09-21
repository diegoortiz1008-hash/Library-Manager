package prueba.tecnica.libreria.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response With Book Copy Data")
public class BookCopyResponseDTO {

    @Schema(description = "Book copy identifier", example = "1")
    private Long id;

    @Schema(description = "Book copy status", example = "AVAILABLE")
    private CopyStatus status;

    @Schema(description = "Identifier of the book this copy belongs to", example = "1")
    private Long bookId;
}
