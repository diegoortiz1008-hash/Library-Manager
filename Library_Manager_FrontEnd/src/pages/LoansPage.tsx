import { useState, type FormEvent } from 'react';
import { useUsers } from '../hooks/useUsers';
import { useBooks } from '../hooks/useBooks';
import { useLoans } from '../hooks/useLoans';
import { useAvailableCopies } from '../hooks/useAvailableCopies';
import { findActiveLoan, hasAvailableCopy } from '../utils/loanChecks';
import LoanForm from '../components/loans/LoanForm';
import LoanTable from '../components/loans/LoanTable';
import AvailableCopiesTable from '../components/loans/AvailableCopiesTable';
import type { LoanInput } from '../types';

export default function LoansPage() {
  const { users } = useUsers();
  const { books } = useBooks();
  const { loans, loading, error, registerLoan, fetchByUser, fetchByBook, returnLoan } = useLoans();
  const { copies, loading: copiesLoading, error: copiesError, searched, fetchByIsbn } = useAvailableCopies();

  const [queryUserId, setQueryUserId] = useState('');
  const [queryBookId, setQueryBookId] = useState('');
  const [resultsLabel, setResultsLabel] = useState('');
  const [isbn, setIsbn] = useState('');


  const handleRegisterLoan = async (data: LoanInput) => {
    const activeLoan = await findActiveLoan(data.userId);
    if (activeLoan) {
      throw new Error(
        `Este usuario ya tiene un préstamo activo (del libro "${activeLoan.book.title}"). Debe devolverlo antes de registrar uno nuevo.`,
      );
    }

    const book = books.find((b) => b.id === data.bookId);
    if (book) {
      const available = await hasAvailableCopy(book.isbn);
      if (!available) {
        throw new Error(`No hay ejemplares disponibles de "${book.title}" en este momento.`);
      }
    }

    await registerLoan(data);
  };

  const handleSearchByUser = async () => {
    if (!queryUserId) return;
    const user = users.find((u) => u.id === Number(queryUserId));
    await fetchByUser(Number(queryUserId));
    setResultsLabel(user ? `Préstamos de ${user.firstName} ${user.lastName}` : 'Préstamos del usuario');
  };

  const handleSearchByBook = async () => {
    if (!queryBookId) return;
    const book = books.find((b) => b.id === Number(queryBookId));
    await fetchByBook(Number(queryBookId));
    setResultsLabel(book ? `Préstamos de "${book.title}"` : 'Préstamos del libro');
  };

  const handleSearchIsbn = (e: FormEvent) => {
    e.preventDefault();
    if (isbn.trim()) fetchByIsbn(isbn.trim());
  };

  return (
    <section>
      <h1>Préstamos</h1>

      <LoanForm users={users} books={books} onRegister={handleRegisterLoan} />

      <div className="query-card">
        <h2>Préstamos por usuario</h2>
        <div className="query-row">
          <select value={queryUserId} onChange={(e) => setQueryUserId(e.target.value)}>
            <option value="">Selecciona un usuario</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.firstName} {user.lastName}
              </option>
            ))}
          </select>
          <button type="button" className="btn-secondary" onClick={handleSearchByUser}>
            Consultar
          </button>
        </div>
      </div>

      <div className="query-card">
        <h2>Préstamos por libro</h2>
        <div className="query-row">
          <select value={queryBookId} onChange={(e) => setQueryBookId(e.target.value)}>
            <option value="">Selecciona un libro</option>
            {books.map((book) => (
              <option key={book.id} value={book.id}>
                {book.title}
              </option>
            ))}
          </select>
          <button type="button" className="btn-secondary" onClick={handleSearchByBook}>
            Consultar
          </button>
        </div>
      </div>

      {error && <p className="error-message">{error}</p>}
      {loading && <p>Cargando préstamos...</p>}
      {!loading && !error && resultsLabel && (
        <>
          <h3>{resultsLabel}</h3>
          <LoanTable loans={loans} onReturn={returnLoan} />
        </>
      )}

      <div className="query-card">
        <h2>Ejemplares disponibles por ISBN</h2>
        <form className="query-row" onSubmit={handleSearchIsbn}>
          <input value={isbn} onChange={(e) => setIsbn(e.target.value)} placeholder="Ej: 978-0132350884" />
          <button type="submit" className="btn-secondary">
            Buscar
          </button>
        </form>
        {copiesError && <p className="error-message">{copiesError}</p>}
        {copiesLoading && <p>Buscando ejemplares...</p>}
        {searched &&
          !copiesLoading &&
          !copiesError &&
          (copies.length > 0 ? (
            <AvailableCopiesTable copies={copies} />
          ) : (
            <p className="empty-state">No hay ejemplares disponibles para este ISBN.</p>
          ))}
      </div>
    </section>
  );
}
