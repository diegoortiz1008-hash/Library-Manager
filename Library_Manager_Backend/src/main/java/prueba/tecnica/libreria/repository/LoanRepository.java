package prueba.tecnica.libreria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prueba.tecnica.libreria.model.entity.Loan;
import prueba.tecnica.libreria.model.entity.enums.LoanState;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByBookCopy_Book_Id(Long bookId);

    boolean existsByUserIdAndLoanStatusIn(Long userId, List<LoanState> loanStatuses);

}
