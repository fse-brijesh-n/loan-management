import { useState } from 'react';
import { FiLogIn, FiUserPlus, FiShield, FiMail, FiLock, FiUser } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const initialForm = {
  fullName: '',
  email: '',
  password: '',
};

export function AuthPage() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login, register } = useAuth();

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (mode === 'login') {
        await login({ email: form.email, password: form.password });
      } else {
        await register(form);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <section className="hero-panel">
        <div className="eyebrow">
          <FiShield />
          Secure lending workspace
        </div>
        <h1>Loan operations with approval control, JWT security, and H2-backed speed.</h1>
        <p>
          Customers apply for loans, administrators review each application, and every request is
          protected by role-aware Spring Security.
        </p>
        <div className="hero-stats">
          <div>
            <strong>JWT</strong>
            <span>Authentication</span>
          </div>
          <div>
            <strong>Admin</strong>
            <span>Approve / Reject</span>
          </div>
          <div>
            <strong>Swagger</strong>
            <span>API exploration</span>
          </div>
        </div>
      </section>

      <section className="auth-card">
        <div className="auth-toggle">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
            <FiLogIn /> Sign in
          </button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
            <FiUserPlus /> Create account
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {mode === 'register' && (
            <label>
              <span><FiUser /> Full name</span>
              <input
                value={form.fullName}
                onChange={(event) => setForm((current) => ({ ...current, fullName: event.target.value }))}
                placeholder="Jordan Lee"
              />
            </label>
          )}

          <label>
            <span><FiMail /> Email</span>
            <input
              type="email"
              value={form.email}
              onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
              placeholder="user@example.com"
            />
          </label>

          <label>
            <span><FiLock /> Password</span>
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
              placeholder="••••••••"
            />
          </label>

          {error && <div className="error-banner">{error}</div>}

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Please wait...' : mode === 'login' ? 'Sign in' : 'Create account'}
          </button>
        </form>

        <div className="demo-note">
          <strong>Demo admin</strong>
          <span>admin@loan.com / Admin@123</span>
        </div>
      </section>
    </div>
  );
}