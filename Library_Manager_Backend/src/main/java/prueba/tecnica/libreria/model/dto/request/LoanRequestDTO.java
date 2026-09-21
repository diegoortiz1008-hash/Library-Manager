package prueba.tecnica.libreria.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import prueba.tecnica.libreria.model.entity.enums.LoanState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a loan")
public class LoanRequestDTO {

    @Schema(description = "Loan date", example = "2026-07-01")
    private LocalDate loanDate;

    @Schema(description = "Expected due date for returning the book", example = "2026-07-15")
    private LocalDate returnDate;

    @Schema(description = "Loan status", example = "PENDING")
    private LoanState loanStatus;

    @Schema(description = "Identifier of the user requesting the loan", example = "1")
    private Long userId;

    @Schema(description = "Identifier of the loaned book", example = "1")
    private Long bookId;
}
