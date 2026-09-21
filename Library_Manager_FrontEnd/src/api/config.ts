export const API_BASE_URL = import.meta.env.API_URL || 'http://localhost:8080';

export class ApiError extends Error {}


const STATUS_MESSAGES: Record<number, string> = {
  400: 'Los datos enviados no son válidos. Revisa el formulario e intenta de nuevo.',
  401: 'No tienes autorización para realizar esta acción.',
  403: 'No tienes permisos para realizar esta acción.',
  404: 'No se encontró la información solicitada.',
  409: 'La operación no se pudo completar porque entra en conflicto con datos existentes.',
  500: 'Ocurrió un error inesperado en el servidor. Intenta nuevamente en unos segundos.',
  502: 'El servidor no está disponible en este momento. Intenta más tarde.',
  503: 'El servidor no está disponible en este momento. Intenta más tarde.',
  504: 'El servidor no está disponible en este momento. Intenta más tarde.',
};

function defaultMessageForStatus(status: number): string {
  return STATUS_MESSAGES[status] ?? `No se pudo completar la solicitud (código ${status}).`;
}

export async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (!response.ok) {
    let message = defaultMessageForStatus(response.status);
    try {
      const body = await response.json();
      if (body?.message) message = body.message;
    } catch {
    }
    throw new ApiError(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}
