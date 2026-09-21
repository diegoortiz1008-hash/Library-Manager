export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  birthDate: string;
}

export type UserInput = Omit<User, 'id'>;

export interface Book {
  id: number;
  title: string;
  isbn: string;
  edition: string;
  publicationDate: string;
  author: string;
}

export type BookInput = Omit<Book, 'id'>;

export type CopyStatus = 'AVAILABLE' | 'LOANED';

export interface BookCopy {
  id: number;
  status: CopyStatus;
  bookId: number;
}

export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'RETURNED' | 'OVERDUE';

export interface Loan {
  id: number;
  loanDate: string;
  returnDate: string | null;
  actualReturnDate: string | null;
  loanStatus: LoanStatus;
  user: User;
  book: Book;
  bookCopyId: number;
}

export interface LoanInput {
  loanDate: string;
  returnDate: string;
  userId: number;
  bookId: number;
}
