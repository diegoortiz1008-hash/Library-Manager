import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react';
import type { User, UserInput } from '../../types';
import { today } from '../../utils/date';

interface UserFormProps {
  initialData?: User;
  onSubmit: (data: UserInput) => Promise<void>;
  onSuccess: () => void;
  onCancel: () => void;
}

const emptyForm: UserInput = { firstName: '', lastName: '', email: '', birthDate: '', password: '' };

export default function UserForm({ initialData, onSubmit, onSuccess, onCancel }: UserFormProps) {
  const [form, setForm] = useState<UserInput>(emptyForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const maxBirthDate = today();

  useEffect(() => {
    setForm(
      initialData
        ? {
            firstName: initialData.firstName,
            lastName: initialData.lastName,
            email: initialData.email,
            birthDate: initialData.birthDate,
            password: '',
          }
        : emptyForm,
    );
    setError('');
  }, [initialData]);

  const handleChange = (field: keyof UserInput) => (e: ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    if (form.birthDate > maxBirthDate) {
      setError('La fecha de nacimiento no puede ser una fecha futura');
      return;
    }
    setSubmitting(true);
    try {
      await onSubmit({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        birthDate: form.birthDate,
        password: form.password,
      });
      onSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el usuario');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <h2>{initialData ? 'Editar usuario' : 'Nuevo usuario'}</h2>
      <div className="form-grid">
        <label>
          Nombre
          <input value={form.firstName} onChange={handleChange('firstName')} required />
        </label>
        <label>
          Apellido
          <input value={form.lastName} onChange={handleChange('lastName')} required />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={handleChange('email')} required />
        </label>
        <label>
          Fecha de nacimiento
          <input
            type="date"
            value={form.birthDate}
            onChange={handleChange('birthDate')}
            max={maxBirthDate}
            required
          />
        </label>
        <label>
          {initialData ? 'Nueva contraseña (dejar vacío para no cambiarla)' : 'Contraseña de carnet'}
          <input
            type="password"
            value={form.password}
            onChange={handleChange('password')}
            required={!initialData}
          />
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
