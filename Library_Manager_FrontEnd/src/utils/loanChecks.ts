import { apiFetch } from '../api/config';
import type { BookCopy, Loan } from '../types';

const ACTIVE_LOAN_STATUSES = ['PENDING', 'APPROVED', 'OVERDUE'];

export async function findActiveLoan(userId: number): Promise<Loan | undefined> {
  const loans = await apiFetch<Loan[]>(`/loans/user/${userId}`);
  return loans.find((loan) => loan.actualReturnDate === null && ACTIVE_LOAN_STATUSES.includes(loan.loanStatus));
}

export async function hasAvailableCopy(isbn: string): Promise<boolean> {
  const copies = await apiFetch<BookCopy[]>(`/books/isbn/${encodeURIComponent(isbn)}/copies/available`);
  return copies.length > 0;
}
