import type { BookCopy } from '../../types';
import { badgeClass } from '../../utils/badge';

interface AvailableCopiesTableProps {
  copies: BookCopy[];
}

export default function AvailableCopiesTable({ copies }: AvailableCopiesTableProps) {
  return (
    <div className="table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            <th>ID Ejemplar</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {copies.map((copy) => (
            <tr key={copy.id}>
              <td>{copy.id}</td>
              <td>
                <span className={badgeClass(copy.status)}>{copy.status}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
