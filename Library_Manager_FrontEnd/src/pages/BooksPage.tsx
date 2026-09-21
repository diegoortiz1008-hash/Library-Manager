import { useState, type FormEvent } from 'react';
import { useBooks } from '../hooks/useBooks';
import BookForm from '../components/books/BookForm';
import BookTable from '../components/books/BookTable';
import ConfirmDialog from '../components/ConfirmDialog';
import type { Book, BookInput } from '../types';

export default function BooksPage() {
  const { books, loading, error, createBook, updateBook, deleteBook, addCopies, searchBooks, exportBook, coverUrl } =
    useBooks();
  const [showForm, setShowForm] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [deletingBook, setDeletingBook] = useState<Book | null>(null);
  const [deleteError, setDeleteError] = useState('');
  const [searchTitle, setSearchTitle] = useState('');

  const closeForm = () => {
    setShowForm(false);
    setEditingBook(null);
  };

  const handleEdit = (book: Book) => {
    setEditingBook(book);
    setShowForm(true);
  };

  const assertIsbnAvailable = (data: BookInput) => {
    const duplicate = books.some((b) => b.isbn.trim() === data.isbn && b.id !== editingBook?.id);
    if (duplicate) {
      throw new Error(`Ya existe un libro registrado con el ISBN ${data.isbn}.`);
    }
  };

  const handleCreate = async (data: BookInput) => {
    assertIsbnAvailable(data);
    await createBook(data);
  };

  const handleUpdate = async (data: BookInput) => {
    if (!editingBook) return;
    assertIsbnAvailable(data);
    await updateBook(editingBook.id, data);
  };

  const handleConfirmDelete = async () => {
    if (!deletingBook) return;
    setDeleteError('');
    try {
      await deleteBook(deletingBook.id);
      setDeletingBook(null);
    } catch (err) {
      setDeleteError(err instanceof Error ? err.message : 'No se pudo eliminar el libro');
    }
  };

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    await searchBooks(searchTitle);
  };

  return (
    <section>
      <div className="page-header">
        <h1>Libros</h1>
        {!showForm && (
          <button type="button" className="btn-primary" onClick={() => setShowForm(true)}>
            Nuevo libro
          </button>
        )}
      </div>

      {error && <p className="error-message">{error}</p>}
      {deleteError && <p className="error-message">{deleteError}</p>}

      <form className="search-bar" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Buscar por título..."
          value={searchTitle}
          onChange={(e) => setSearchTitle(e.target.value)}
        />
        <button type="submit" className="btn-secondary">
          Buscar
        </button>
      </form>

      {showForm && (
        <BookForm
          initialData={editingBook ?? undefined}
          onSubmit={editingBook ? handleUpdate : handleCreate}
          onSuccess={closeForm}
          onCancel={closeForm}
        />
      )}

      {loading ? (
        <p>Cargando libros...</p>
      ) : (
        <BookTable
          books={books}
          onEdit={handleEdit}
          onDelete={setDeletingBook}
          onAddCopies={addCopies}
          onExport={exportBook}
          coverUrl={coverUrl}
        />
      )}

      {deletingBook && (
        <ConfirmDialog
          message={`¿Eliminar "${deletingBook.title}"?`}
          onConfirm={handleConfirmDelete}
          onCancel={() => setDeletingBook(null)}
        />
      )}
    </section>
  );
}
