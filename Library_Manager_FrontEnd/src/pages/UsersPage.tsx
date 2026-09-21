import { useState } from 'react';
import { useUsers } from '../hooks/useUsers';
import UserForm from '../components/users/UserForm';
import UserTable from '../components/users/UserTable';
import ConfirmDialog from '../components/ConfirmDialog';
import type { User, UserInput } from '../types';

export default function UsersPage() {
  const { users, loading, error, createUser, updateUser, deleteUser } = useUsers();
  const [showForm, setShowForm] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [deletingUser, setDeletingUser] = useState<User | null>(null);
  const [deleteError, setDeleteError] = useState('');

  const closeForm = () => {
    setShowForm(false);
    setEditingUser(null);
  };

  const handleEdit = (user: User) => {
    setEditingUser(user);
    setShowForm(true);
  };

  // Emails duplicados
  const assertEmailAvailable = (data: UserInput) => {
    const duplicate = users.some(
      (u) => u.email.trim().toLowerCase() === data.email.toLowerCase() && u.id !== editingUser?.id,
    );
    if (duplicate) {
      throw new Error(`Ya existe un usuario registrado con el email ${data.email}.`);
    }
  };

  const handleCreate = async (data: UserInput) => {
    assertEmailAvailable(data);
    await createUser(data);
  };

  const handleUpdate = async (data: UserInput) => {
    if (!editingUser) return;
    assertEmailAvailable(data);
    await updateUser(editingUser.id, data);
  };

  const handleConfirmDelete = async () => {
    if (!deletingUser) return;
    setDeleteError('');
    try {
      await deleteUser(deletingUser.id);
      setDeletingUser(null);
    } catch (err) {
      setDeleteError(err instanceof Error ? err.message : 'No se pudo eliminar el usuario');
    }
  };

  return (
    <section>
      <div className="page-header">
        <h1>Usuarios</h1>
        {!showForm && (
          <button type="button" className="btn-primary" onClick={() => setShowForm(true)}>
            Nuevo usuario
          </button>
        )}
      </div>

      {error && <p className="error-message">{error}</p>}
      {deleteError && <p className="error-message">{deleteError}</p>}

      {showForm && (
        <UserForm
          initialData={editingUser ?? undefined}
          onSubmit={editingUser ? handleUpdate : handleCreate}
          onSuccess={closeForm}
          onCancel={closeForm}
        />
      )}

      {loading ? (
        <p>Cargando usuarios...</p>
      ) : (
        <UserTable users={users} onEdit={handleEdit} onDelete={setDeletingUser} />
      )}

      {deletingUser && (
        <ConfirmDialog
          message={`¿Eliminar a ${deletingUser.firstName} ${deletingUser.lastName}?`}
          onConfirm={handleConfirmDelete}
          onCancel={() => setDeletingUser(null)}
        />
      )}
    </section>
  );
}
