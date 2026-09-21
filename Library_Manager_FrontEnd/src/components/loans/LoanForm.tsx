import { useState, type FormEvent } from 'react';
import type { Book, LoanInput, User } from '../../types';
import { today } from '../../utils/date';

interface LoanFormProps {
  users: User[];
  books: Book[];
  onRegister: (data: LoanInput) => Promise<void>;
}

export default function LoanForm({ users, books, onRegister }: LoanFormProps) {
  const [userId, setUserId] = useState('');
  const [bookId, setBookId] = useState('');
  const [loanDate, setLoanDate] = useState(today);
  const [returnDate, setReturnDate] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      await onRegister({
        loanDate,
        returnDate,
        userId: Number(userId),
        bookId: Number(bookId),
      });
      setSuccess('Préstamo registrado correctamente.');
      setUserId('');
      setBookId('');
      setReturnDate('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo registrar el préstamo');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <h2>Registrar préstamo</h2>
      <div className="form-grid">
        <label>
          Usuario
          <select value={userId} onChange={(e) => setUserId(e.target.value)} required>
            <option value="">Selecciona un usuario</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.firstName} {user.lastName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Libro
          <select value={bookId} onChange={(e) => setBookId(e.target.value)} required>
            <option value="">Selecciona un libro</option>
            {books.map((book) => (
              <option key={book.id} value={book.id}>
                {book.title}
              </option>
            ))}
          </select>
        </label>
        <label>
          Fecha de préstamo
          <input type="date" value={loanDate} onChange={(e) => setLoanDate(e.target.value)} required />
        </label>
        <label>
          Fecha de devolución esperada
          <input type="date" value={returnDate} onChange={(e) => setReturnDate(e.target.value)} required />
        </label>
      </div>
      {error && <p className="error-message">{error}</p>}
      {success && <p className="success-message">{success}</p>}
      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={submitting}>
          {submitting ? 'Registrando...' : 'Registrar préstamo'}
        </button>
      </div>
    </form>
  );
}
