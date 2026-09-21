import type { Book } from '../../types';
import CopiesCell from './CopiesCell';

interface BookTableProps {
  books: Book[];
  onEdit: (book: Book) => void;
  onDelete: (book: Book) => void;
  onAddCopies: (bookId: number, quantity: number) => Promise<void>;
  onExport: (bookId: number, format: string) => Promise<{ file: string }>;
  coverUrl: (bookId: number, filename: string) => string;
}

export default function BookTable({ books, onEdit, onDelete, onAddCopies, onExport, coverUrl }: BookTableProps) {
  if (books.length === 0) {
    return <p className="empty-state">No hay libros registrados.</p>;
  }

  return (
    <div className="table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>ISBN</th>
            <th>Edición</th>
            <th>Publicación</th>
            <th>Autor</th>
            <th>Ejemplares</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.id}>
              <td>{book.id}</td>
              <td>{book.title}</td>
              <td>{book.isbn}</td>
              <td>{book.edition}</td>
              <td>{book.publicationDate}</td>
              <td>{book.author}</td>
              <td>
                <CopiesCell bookId={book.id} isbn={book.isbn} onAdd={onAddCopies} />
              </td>
              <td className="actions">
                <button type="button" className="btn-secondary" onClick={() => onEdit(book)}>
                  Editar
                </button>
                <button type="button" className="btn-danger" onClick={() => onDelete(book)}>
                  Eliminar
                </button>
                <button type="button" className="btn-secondary" onClick={() => onExport(book.id, 'csv')}>
                  Exportar
                </button>
                <a
                  className="btn-secondary"
                  href={coverUrl(book.id, 'placeholder.txt')}
                  target="_blank"
                  rel="noreferrer"
                >
                  Portada
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
