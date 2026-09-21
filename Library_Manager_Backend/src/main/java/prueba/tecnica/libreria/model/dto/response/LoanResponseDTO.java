package prueba.tecnica.libreria.model.dto.response;

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
@Schema(description = "Response With Loan Data")
public class LoanResponseDTO {

    @Schema(description = "Loan identifier", example = "1")
    private Long id;

    @Schema(description = "Loan date", example = "2026-07-01")
    private LocalDate loanDate;

    @Schema(description = "Expected due date for returning the book", example = "2026-07-15")
    private LocalDate returnDate;

    @Schema(description = "Date the book was actually returned, null if the loan is still open", example = "2026-07-14")
    private LocalDate actualReturnDate;

    @Schema(description = "Loan status (OVERDUE is resolved on read when the due date has passed)", example = "PENDING")
    private LoanState loanStatus;

    @Schema(description = "User who requested the loan")
    private UserResponseDTO user;

    @Schema(description = "Loaned book")
    private BookResponseDTO book;

    @Schema(description = "Identifier of the specific physical copy that was loaned", example = "1")
    private Long bookCopyId;
}
