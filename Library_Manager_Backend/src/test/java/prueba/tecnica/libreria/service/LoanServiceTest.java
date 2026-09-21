package prueba.tecnica.libreria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import prueba.tecnica.libreria.exception.ActiveLoanAlreadyExistsException;
import prueba.tecnica.libreria.exception.BookNotFoundException;
import prueba.tecnica.libreria.exception.NoAvailableCopyException;
import prueba.tecnica.libreria.exception.UserNotFoundException;
import prueba.tecnica.libreria.model.entity.Book;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.Loan;
import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.model.entity.enums.LoanState;
import prueba.tecnica.libreria.repository.BookCopyRepository;
import prueba.tecnica.libreria.repository.BookRepository;
import prueba.tecnica.libreria.repository.LoanRepository;
import prueba.tecnica.libreria.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void registerLoanShouldAssignDefaultsAndLoanAvailableCopy() {
        User user = user(1L, "Ana", "Lopez");
        Book book = book(10L, "Clean Code");
        BookCopy copy = copy(100L, book, CopyStatus.AVAILABLE);
        Loan input = Loan.builder().build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndLoanStatusIn(1L, List.of(LoanState.PENDING, LoanState.APPROVED))).thenReturn(false);
        when(bookRepository.existsById(10L)).thenReturn(true);
        when(bookCopyRepository.findFirstByBookIdAndStatusOrderByIdAsc(10L, CopyStatus.AVAILABLE)).thenReturn(Optional.of(copy));
        when(bookCopyRepository.save(any(BookCopy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.registerLoan(1L, 10L, input);

        ArgumentCaptor<BookCopy> copyCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyRepository).save(copyCaptor.capture());
        assertEquals(CopyStatus.LOANED, copyCaptor.getValue().getStatus());

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());
        Loan savedLoan = loanCaptor.getValue();
        assertEquals(LocalDate.now(), savedLoan.getLoanDate());
        assertEquals(LoanState.PENDING, savedLoan.getLoanStatus());
        assertEquals(user, savedLoan.getUser());
        assertEquals(copy, savedLoan.getBookCopy());
        assertEquals(savedLoan, result);
    }

    @Test
    void registerLoanShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> loanService.registerLoan(1L, 10L, Loan.builder().build()));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void registerLoanShouldThrowWhenUserAlreadyHasActiveLoan() {
        User user = user(1L, "Ana", "Lopez");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndLoanStatusIn(1L, List.of(LoanState.PENDING, LoanState.APPROVED))).thenReturn(true);

        ActiveLoanAlreadyExistsException exception = assertThrows(ActiveLoanAlreadyExistsException.class,
                () -> loanService.registerLoan(1L, 10L, Loan.builder().build()));

        assertEquals("User with id: 1 already has an active loan", exception.getMessage());
    }

    @Test
    void registerLoanShouldThrowWhenBookDoesNotExist() {
        User user = user(1L, "Ana", "Lopez");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndLoanStatusIn(1L, List.of(LoanState.PENDING, LoanState.APPROVED))).thenReturn(false);
        when(bookRepository.existsById(10L)).thenReturn(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                () -> loanService.registerLoan(1L, 10L, Loan.builder().build()));

        assertEquals("Book not found with id: 10", exception.getMessage());
    }

    @Test
    void registerLoanShouldThrowWhenNoCopyIsAvailable() {
        User user = user(1L, "Ana", "Lopez");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndLoanStatusIn(1L, List.of(LoanState.PENDING, LoanState.APPROVED))).thenReturn(false);
        when(bookRepository.existsById(10L)).thenReturn(true);
        when(bookCopyRepository.findFirstByBookIdAndStatusOrderByIdAsc(10L, CopyStatus.AVAILABLE)).thenReturn(Optional.empty());

        NoAvailableCopyException exception = assertThrows(NoAvailableCopyException.class,
                () -> loanService.registerLoan(1L, 10L, Loan.builder().build()));

        assertEquals("No available copies for book with id: 10", exception.getMessage());
    }

    @Test
    void findLoansByUserIdShouldMarkOverdueApprovedLoans() {
        User user = user(1L, "Ana", "Lopez");
        Loan overdueLoan = Loan.builder()
                .loanDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(5))
                .loanStatus(LoanState.APPROVED)
                .user(user)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(loanRepository.findByUserId(1L)).thenReturn(List.of(overdueLoan));

        List<Loan> result = loanService.findLoansByUserId(1L);

        assertEquals(LoanState.OVERDUE, result.get(0).getLoanStatus());
    }

    @Test
    void findLoansByBookIdShouldReturnResolvedLoans() {
        Book book = book(10L, "Clean Code");
        BookCopy copy = copy(100L, book, CopyStatus.LOANED);
        Loan loan = Loan.builder()
                .loanDate(LocalDate.now().minusDays(2))
                .returnDate(LocalDate.now().plusDays(5))
                .loanStatus(LoanState.PENDING)
                .bookCopy(copy)
                .build();
        when(bookRepository.existsById(10L)).thenReturn(true);
        when(loanRepository.findByBookCopy_Book_Id(10L)).thenReturn(List.of(loan));

        List<Loan> result = loanService.findLoansByBookId(10L);

        assertEquals(LoanState.PENDING, result.get(0).getLoanStatus());
    }

    @Test
    void findLoansByUserIdShouldThrowWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> loanService.findLoansByUserId(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    private User user(Long id, String firstName, String lastName) {
        return User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private Book book(Long id, String title) {
        return Book.builder()
                .id(id)
                .title(title)
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