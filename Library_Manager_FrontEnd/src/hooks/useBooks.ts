import { useCallback, useEffect, useState } from 'react';
import { apiFetch, API_BASE_URL } from '../api/config';
import type { Book, BookInput, BookCopy } from '../types';

export function useBooks() {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await apiFetch<Book[]>('/books/all');
      setBooks(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los libros');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  const createBook = async (input: BookInput) => {
    await apiFetch<Book>('/books', {
      method: 'POST',
      body: JSON.stringify(input),
    });
    await fetchBooks();
  };

  const updateBook = async (id: number, input: BookInput) => {
    await apiFetch<Book>(`/books/${id}`, {
      method: 'PUT',
      body: JSON.stringify(input),
    });
    await fetchBooks();
  };

  const deleteBook = async (id: number) => {
    await apiFetch<void>(`/books/${id}`, { method: 'DELETE' });
    await fetchBooks();
  };

  const addCopies = async (id: number, quantity: number) => {
    await apiFetch<BookCopy[]>(`/books/${id}/copies?quantity=${quantity}`, {
      method: 'POST',
    });
  };

  const searchBooks = async (title: string) => {
    if (!title.trim()) {
      await fetchBooks();
      return;
    }
    setLoading(true);
    setError('');
    try {
      const data = await apiFetch<Book[]>(`/books/search?title=${encodeURIComponent(title)}`);
      setBooks(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al buscar libros');
    } finally {
      setLoading(false);
    }
  };

  const exportBook = async (id: number, format: string) => {
    return apiFetch<{ file: string }>(`/books/${id}/export?format=${encodeURIComponent(format)}`);
  };

  const coverUrl = (id: number, filename: string) =>
    `${API_BASE_URL}/books/${id}/cover?filename=${encodeURIComponent(filename)}`;

  return {
    books,
    loading,
    error,
    createBook,
    updateBook,
    deleteBook,
    addCopies,
    searchBooks,
    exportBook,
    coverUrl,
    refresh: fetchBooks,
  };
}
