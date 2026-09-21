package prueba.tecnica.libreria.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prueba.tecnica.libreria.exception.ActiveLoanAlreadyExistsException;
import prueba.tecnica.libreria.exception.BookNotFoundException;
import prueba.tecnica.libreria.exception.LoanNotFoundException;
import prueba.tecnica.libreria.exception.NoAvailableCopyException;
import prueba.tecnica.libreria.exception.UserNotFoundException;
import prueba.tecnica.libreria.model.entity.BookCopy;
import prueba.tecnica.libreria.model.entity.Loan;
import prueba.tecnica.libreria.model.entity.User;
import prueba.tecnica.libreria.model.entity.enums.CopyStatus;
import prueba.tecnica.libreria.model.entity.enums.LoanState;
import prueba.tecnica.libreria.repository.BookCopyRepository;
import prueba.tecnica.libreria.repository.BookRepository;
import prueba.tecnica.libreria.repository.LoanRepository;
import prueba.tecnica.libreria.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoanService {

    // Statuses that count as an active loan 
    private static final List<LoanState> ACTIVE_LOAN_STATUSES = List.of(LoanState.PENDING, LoanState.APPROVED);

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    // Register a loan of a book for a user
    @Transactional
    public Loan registerLoan(Long userId, Long bookId, Loan loan) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (loanRepository.existsByUserIdAndLoanStatusIn(userId, ACTIVE_LOAN_STATUSES)) {
            throw new ActiveLoanAlreadyExistsException("User with id: " + userId + " already has an active loan");
        }

        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }

        BookCopy copy = bookCopyRepository.findFirstByBookIdAndStatusOrderByIdAsc(bookId, CopyStatus.AVAILABLE)
                .orElseThrow(() -> new NoAvailableCopyException("No available copies for book with id: " + bookId));

        copy.setStatus(CopyStatus.LOANED);
        bookCopyRepository.save(copy);

        Loan newLoan = Loan.builder()
                .loanDate(loan.getLoanDate() != null ? loan.getLoanDate() : LocalDate.now())
                .returnDate(loan.getReturnDate())
                .loanStatus(loan.getLoanStatus() != null ? loan.getLoanStatus() : LoanState.PENDING)
                .user(user)
                .bookCopy(copy)
                .build();

        return loanRepository.save(newLoan);
    }

    // Mark a loan as returned, freeing up its physical copy. Idempotent: returning
    // an already-returned loan just returns its current state without changing it.
    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + loanId));

        if (loan.getActualReturnDate() == null) {
            loan.setActualReturnDate(LocalDate.now());
            loan.setLoanStatus(LoanState.RETURNED);

            BookCopy copy = loan.getBookCopy();
            copy.setStatus(CopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);

            loan = loanRepository.save(loan);
        }

        return loan;
    }

    // List loans by user
    @Transactional(readOnly = true)
    public List<Loan> findLoansByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return resolveStatuses(loanRepository.findByUserId(userId));
    }

    // List loans by book
    @Transactional(readOnly = true)
    public List<Loan> findLoansByBookId(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }
        return resolveStatuses(loanRepository.findByBookCopy_Book_Id(bookId));
    }

    // Computes overdue status in memory for active loans past their due date 
    private List<Loan> resolveStatuses(List<Loan> loans) {
        LocalDate today = LocalDate.now();
        loans.forEach(loan -> {
            boolean isOpenLoan = loan.getLoanStatus() == LoanState.APPROVED;
            boolean isPastDue = loan.getReturnDate() != null && today.isAfter(loan.getReturnDate());
            if (loan.getActualReturnDate() == null && isOpenLoan && isPastDue) {
                loan.setLoanStatus(LoanState.OVERDUE);
            }
        });
        return loans;
    }

}
