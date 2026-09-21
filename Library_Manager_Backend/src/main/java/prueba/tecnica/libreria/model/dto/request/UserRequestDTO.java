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
@Schema(description = "Request to create or update a user")
public class UserRequestDTO {

    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User birth date", example = "1990-05-20")
    private LocalDate birthDate;
}
