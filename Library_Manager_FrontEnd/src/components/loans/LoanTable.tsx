import { useState } from 'react';
import type { Loan } from '../../types';
import { badgeClass } from '../../utils/badge';

interface LoanTableProps {
  loans: Loan[];
  onReturn: (loanId: number) => Promise<void>;
}

export default function LoanTable({ loans, onReturn }: LoanTableProps) {
  const [returningId, setReturningId] = useState<number | null>(null);
  const [returnError, setReturnError] = useState('');

  if (loans.length === 0) {
    return <p className="empty-state">No se encontraron préstamos.</p>;
  }

  const handleReturn = async (loanId: number) => {
    setReturnError('');
    setReturningId(loanId);
    try {
      await onReturn(loanId);
    } catch (err) {
      setReturnError(err instanceof Error ? err.message : 'No se pudo marcar el préstamo como devuelto');
    } finally {
      setReturningId(null);
    }
  };

  return (
    <div className="table-wrapper">
      {returnError && <p className="error-message">{returnError}</p>}
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Usuario</th>
            <th>Libro</th>
            <th>Fecha préstamo</th>
            <th>Devolución esperada</th>
            <th>Devolución real</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {loans.map((loan) => (
            <tr key={loan.id}>
              <td>{loan.id}</td>
              <td>
                {loan.user.firstName} {loan.user.lastName}
              </td>
              <td>{loan.book.title}</td>
              <td>{loan.loanDate}</td>
              <td>{loan.returnDate ?? '-'}</td>
              <td>{loan.actualReturnDate ?? '-'}</td>
              <td>
                <span className={badgeClass(loan.loanStatus)}>{loan.loanStatus}</span>
              </td>
              <td>
                {loan.actualReturnDate ? (
                  <span className="hint-text">—</span>
                ) : (
                  <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => handleReturn(loan.id)}
                    disabled={returningId === loan.id}
                  >
                    {returningId === loan.id ? 'Guardando...' : 'Marcar como devuelto'}
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
