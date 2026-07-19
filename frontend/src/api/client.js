const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const token = localStorage.getItem('lm_token');
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
    ...options,
  });

  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json') ? await response.json() : null;

  if (!response.ok) {
    throw new Error(payload?.message || 'Request failed');
  }

  return payload;
}

export const api = {
  register: (data) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login: (data) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  getLoans: () => request('/api/loans'),
  applyLoan: (data) => request('/api/loans', { method: 'POST', body: JSON.stringify(data) }),
  approveLoan: (id, reason) => request(`/api/loans/${id}/approve`, {
    method: 'PUT',
    body: JSON.stringify(reason ? { reason } : {}),
  }),
  rejectLoan: (id, reason) => request(`/api/loans/${id}/reject`, {
    method: 'PUT',
    body: JSON.stringify(reason ? { reason } : {}),
  }),
};