import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react';
import type { Book, BookInput } from '../../types';

interface BookFormProps {
  initialData?: Book;
  onSubmit: (data: BookInput) => Promise<void>;
  onSuccess: () => void;
  onCancel: () => void;
}

const emptyForm: BookInput = { title: '', isbn: '', edition: '', publicationDate: '', author: '' };

export default function BookForm({ initialData, onSubmit, onSuccess, onCancel }: BookFormProps) {
  const [form, setForm] = useState<BookInput>(emptyForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    setForm(
      initialData
        ? {
            title: initialData.title,
            isbn: initialData.isbn,
            edition: initialData.edition,
            publicationDate: initialData.publicationDate,
            author: initialData.author,
          }
        : emptyForm,
    );
    setError('');
  }, [initialData]);

  const handleChange = (field: keyof BookInput) => (e: ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await onSubmit({
        title: form.title.trim(),
        isbn: form.isbn.trim(),
        edition: form.edition.trim(),
        publicationDate: form.publicationDate,
        author: form.author.trim(),
      });
      onSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el libro');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <h2>{initialData ? 'Editar libro' : 'Nuevo libro'}</h2>
      <div className="form-grid">
        <label>
          Título
          <input value={form.title} onChange={handleChange('title')} required />
        </label>
        <label>
          ISBN
          <input value={form.isbn} onChange={handleChange('isbn')} required />
        </label>
        <label>
          Edición
          <input value={form.edition} onChange={handleChange('edition')} required />
        </label>
        <label>
          Fecha de publicación
          <input type="date" value={form.publicationDate} onChange={handleChange('publicationDate')} required />
        </label>
        <label>
          Autor
          <input value={form.author} onChange={handleChange('author')} required />
        </label>
      </div>
      {error && <p className="error-message">{error}</p>}
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel}>
          Cancelar
        </button>
        <button type="submit" className="btn-primary" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
}
