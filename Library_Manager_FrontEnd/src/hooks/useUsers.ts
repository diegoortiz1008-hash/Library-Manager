import { useCallback, useEffect, useState } from 'react';
import { apiFetch } from '../api/config';
import type { User, UserInput } from '../types';

export function useUsers() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await apiFetch<User[]>('/users/all');
      setUsers(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los usuarios');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const createUser = async (input: UserInput) => {
    await apiFetch<User>('/users', {
      method: 'POST',
      body: JSON.stringify(input),
    });
    await fetchUsers();
  };

  const updateUser = async (id: number, input: UserInput) => {
    await apiFetch<User>(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(input),
    });
    await fetchUsers();
  };

  const deleteUser = async (id: number) => {
    await apiFetch<void>(`/users/${id}`, { method: 'DELETE' });
    await fetchUsers();
  };

  return { users, loading, error, createUser, updateUser, deleteUser, refresh: fetchUsers };
}
