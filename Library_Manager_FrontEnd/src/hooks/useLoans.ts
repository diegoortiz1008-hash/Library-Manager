import { useState } from 'react';
import { apiFetch } from '../api/config';
import type { Loan, LoanInput } from '../types';

export function useLoans() {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const registerLoan = async (input: LoanInput) => {
    await apiFetch<Loan>('/loans', {
      method: 'POST',
      body: JSON.stringify(input),
    });
  };

  const fetchByUser = async (userId: number) => {
    setLoading(true);
    setError('');
    try {
      setLoans(await apiFetch<Loan[]>(`/loans/user/${userId}`));
    } catch (err) {
      setLoans([]);
      setError(err instanceof Error ? err.message : 'Error al consultar los préstamos');
    } finally {
      setLoading(false);
    }
  };

  const fetchByBook = async (bookId: number) => {
    setLoading(true);
    setError('');
    try {
      setLoans(await apiFetch<Loan[]>(`/loans/book/${bookId}`));
    } catch (err) {
      setLoans([]);
      setError(err instanceof Error ? err.message : 'Error al consultar los préstamos');
    } finally {
      setLoading(false);
    }
  };

  const returnLoan = async (loanId: number) => {
    const updated = await apiFetch<Loan>(`/loans/${loanId}/return`, { method: 'PATCH' });
    setLoans((prev) => prev.map((loan) => (loan.id === loanId ? updated : loan)));
  };

  return { loans, loading, error, registerLoan, fetchByUser, fetchByBook, returnLoan };
}
