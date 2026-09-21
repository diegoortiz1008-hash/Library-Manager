import { useCallback, useState } from 'react';
import { apiFetch } from '../api/config';
import type { BookCopy } from '../types';

export function useAvailableCopies() {
  const [copies, setCopies] = useState<BookCopy[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [searched, setSearched] = useState(false);

  const fetchByIsbn = useCallback(async (isbn: string) => {
    setLoading(true);
    setError('');
    setSearched(true);
    try {
      setCopies(await apiFetch<BookCopy[]>(`/books/isbn/${encodeURIComponent(isbn)}/copies/available`));
    } catch (err) {
      setCopies([]);
      setError(err instanceof Error ? err.message : 'Error al buscar los ejemplares');
    } finally {
      setLoading(false);
    }
  }, []);

  return { copies, loading, error, searched, fetchByIsbn };
}
