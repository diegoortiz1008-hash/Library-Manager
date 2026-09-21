import { useEffect } from 'react';
import { useAvailableCopies } from '../../hooks/useAvailableCopies';
import AddCopiesControl from './AddCopiesControl';

interface CopiesCellProps {
  bookId: number;
  isbn: string;
  onAdd: (bookId: number, quantity: number) => Promise<void>;
}

export default function CopiesCell({ bookId, isbn, onAdd }: CopiesCellProps) {
  const { copies, loading, fetchByIsbn } = useAvailableCopies();

  useEffect(() => {
    fetchByIsbn(isbn);
  }, [isbn, fetchByIsbn]);

  const handleAdd = async (id: number, quantity: number) => {
    await onAdd(id, quantity);
    await fetchByIsbn(isbn);
  };

  return (
    <div className="copies-cell">
      <span className="copies-count">{loading ? 'Cargando...' : `${copies.length} disponible(s)`}</span>
      <AddCopiesControl bookId={bookId} onAdd={handleAdd} />
    </div>
  );
}
