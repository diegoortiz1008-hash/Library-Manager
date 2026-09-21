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

import prueba.tecnica.libreria.mapper.LoanMapper;
import prueba.tecnica.libreria.model.dto.request.LoanRequestDTO;
import prueba.tecnica.libreria.model.dto.response.BookResponseDTO;
import prueba.tecnica.libreria.model.dto.response.LoanResponseDTO;
import prueba.tecnica.libreria.model.dto.response.UserResponseDTO;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.Loan;
import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.model.entity.enums.LoanState;
import prueba.tecnica.libreria.service.LoanService;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanController loanController;

    @Test
    void registerLoanShouldReturnCreatedLoan() {
        LoanRequestDTO request = loanRequest(1L, 10L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING);
        Loan entity = loan(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING);
        LoanResponseDTO response = loanResponse(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING, userResponse(1L), bookResponse(10L), 100L);
        when(loanMapper.toEntity(request)).thenReturn(entity);
        when(loanService.registerLoan(1L, 10L, entity)).thenReturn(entity);
        when(loanMapper.toDto(entity)).thenReturn(response);

        var result = loanController.registerLoan(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(loanService).registerLoan(1L, 10L, entity);
    }

    @Test
    void getLoansByUserIdShouldReturnMappedLoans() {
        Loan entity = loan(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING);
        LoanResponseDTO response = loanResponse(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING, userResponse(1L), bookResponse(10L), 100L);
        when(loanService.findLoansByUserId(1L)).thenReturn(List.of(entity));
        when(loanMapper.toDtoList(List.of(entity))).thenReturn(List.of(response));

        var result = loanController.getLoansByUserId(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(List.of(response), result.getBody());
    }

    @Test
    void getLoansByBookIdShouldReturnMappedLoans() {
        Loan entity = loan(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING);
        LoanResponseDTO response = loanResponse(1L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10), LoanState.PENDING, userResponse(1L), bookResponse(10L), 100L);
        when(loanService.findLoansByBookId(10L)).thenReturn(List.of(entity));
        when(loanMapper.toDtoList(List.of(entity))).thenReturn(List.of(response));

        var result = loanController.getLoansByBookId(10L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(List.of(response), result.getBody());
    }

    private LoanRequestDTO loanRequest(Long userId, Long bookId, LocalDate loanDate, LocalDate returnDate, LoanState loanStatus) {
        return LoanRequestDTO.builder()
                .userId(userId)
                .bookId(bookId)
                .loanDate(loanDate)
                .returnDate(returnDate)
                .loanStatus(loanStatus)
                .build();
    }

    private Loan loan(Long id, LocalDate loanDate, LocalDate returnDate, LoanState loanStatus) {
        return Loan.builder()
                .id(id)
                .loanDate(loanDate)
                .returnDate(returnDate)
                .loanStatus(loanStatus)
                .user(User.builder().id(1L).firstName("Ana").lastName("Lopez").build())
                .bookCopy(BookCopy.builder().id(100L).status(CopyStatus.LOANED).book(Book.builder().id(10L).title("Clean Code").build()).build())
                .build();
    }

    private LoanResponseDTO loanResponse(Long id, LocalDate loanDate, LocalDate returnDate, LoanState loanStatus,
            UserResponseDTO user, BookResponseDTO book, Long bookCopyId) {
        return LoanResponseDTO.builder()
                .id(id)
                .loanDate(loanDate)
                .returnDate(returnDate)
                .loanStatus(loanStatus)
                .user(user)
                .book(book)
                .bookCopyId(bookCopyId)
                .build();
    }

    private UserResponseDTO userResponse(Long id) {
        return UserResponseDTO.builder()
                .id(id)
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana@example.com")
                .birthDate(LocalDate.of(2000, 1, 10))
                .build();
    }

    private BookResponseDTO bookResponse(Long id) {
        return BookResponseDTO.builder()
                .id(id)
                .title("Clean Code")
                .isbn("9780132350884")
                .edition("1")
                .publicationDate(LocalDate.of(2008, 8, 1))
                .author("Robert C. Martin")
                .build();
    }
}