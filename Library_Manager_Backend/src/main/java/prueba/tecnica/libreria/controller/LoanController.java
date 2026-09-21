package prueba.tecnica.libreria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import prueba.tecnica.libreria.mapper.LoanMapper;
import prueba.tecnica.libreria.model.dto.request.LoanRequestDTO;
import prueba.tecnica.libreria.model.dto.response.LoanResponseDTO;
import prueba.tecnica.libreria.model.entity.Loan;
import prueba.tecnica.libreria.service.LoanService;

@RestController
@RequestMapping("loans")
@RequiredArgsConstructor
@Tag(name = "Loan Controller", description = "Controller for managing loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping("")
    @Operation(summary = "Register a loan", description = "Registers a loan of a book for a user, assigning any available physical copy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Loan registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "User or book not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "User already has an active loan or no copies are available", content = @Content)
    })
    public ResponseEntity<LoanResponseDTO> registerLoan(@Valid @RequestBody LoanRequestDTO loanDTO) {
        Loan createdLoan = loanService.registerLoan(loanDTO.getUserId(), loanDTO.getBookId(), loanMapper.toEntity(loanDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(loanMapper.toDto(createdLoan));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get loans by user", description = "Retrieves the loans belonging to a given user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<List<LoanResponseDTO>> getLoansByUserId(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(loanMapper.toDtoList(loanService.findLoansByUserId(userId)));
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Return a loan", description = "Marks a loan as returned and frees up its associated physical copy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
    })
    public ResponseEntity<LoanResponseDTO> returnLoan(
            @Parameter(description = "Loan ID", example = "1") @PathVariable Long id) {
        Loan returnedLoan = loanService.returnLoan(id);
        return ResponseEntity.ok(loanMapper.toDto(returnedLoan));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get loans by book", description = "Retrieves the loans associated with a given book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public ResponseEntity<List<LoanResponseDTO>> getLoansByBookId(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        return ResponseEntity.ok(loanMapper.toDtoList(loanService.findLoansByBookId(bookId)));
    }

}
