import { useState } from 'react';

interface AddCopiesControlProps {
  bookId: number;
  onAdd: (bookId: number, quantity: number) => Promise<void>;
}

export default function AddCopiesControl({ bookId, onAdd }: AddCopiesControlProps) {
  const [quantity, setQuantity] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleClick = async () => {
    setError('');
    setSubmitting(true);
    try {
      await onAdd(bookId, quantity);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al agregar ejemplares');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="add-copies">
      <input
        type="number"
        min={1}
        value={quantity}
        onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
        aria-label="Cantidad de ejemplares"
      />
      <button type="button" className="btn-secondary" onClick={handleClick} disabled={submitting}>
        {submitting ? '...' : 'Agregar ejemplares'}
      </button>
      {error && <span className="error-message">{error}</span>}
    </div>
  );
}
