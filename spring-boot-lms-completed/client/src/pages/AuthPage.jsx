import { useState } from 'react';
import { FiLogIn, FiUserPlus, FiShield, FiMail, FiLock, FiUser, FiCheckCircle, FiFileText, FiClock } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const initialForm = {
  fullName: '',
  email: '',
  password: '',
  role: 'CUSTOMER',
  organizationName: '',
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
        await login({ email: form.email, password: form.password, role: form.role });
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
        <div className="portal-header">
          <div className="portal-mark">
            <FiShield />
          </div>
          <div>
            <div className="portal-kicker">Public Loan Services</div>
            <h1>Loan Services Portal</h1>
          </div>
        </div>

        <p className="portal-summary">Secure access for loan applications, account access, and review workflows.</p>

        <ul className="service-list">
          <li><FiFileText /> Submit loan applications</li>
          <li><FiClock /> Track application status</li>
          <li><FiCheckCircle /> Review and decision workflow</li>
        </ul>

        <div className="portal-footnote">Authenticated access only</div>
      </section>

      <section className="auth-card">
        <div className="auth-toggle">
          <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
            <FiLogIn /> Sign in
          </button>
          <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
            <FiUserPlus /> Register
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {mode === 'register' && (
            <label>
              <span><FiUser /> Applicant name</span>
              <input
                value={form.fullName}
                onChange={(event) => setForm((current) => ({ ...current, fullName: event.target.value }))}
                placeholder="Enter full name"
              />
            </label>
          )}

          <label>
            <span><FiShield /> Account type</span>
            <select
              value={form.role}
              onChange={(event) => setForm((current) => ({ ...current, role: event.target.value }))}
            >
              <option value="CUSTOMER">Customer account</option>
              <option value="ADMIN">Admin account</option>
            </select>
            <small className="field-hint">Select the same account type you used to register.</small>
          </label>

          {mode === 'register' && form.role === 'ADMIN' && (
            <label>
              <span><FiShield /> Organization name</span>
              <input
                value={form.organizationName}
                onChange={(event) => setForm((current) => ({ ...current, organizationName: event.target.value }))}
                placeholder="Organization or branch name"
              />
            </label>
          )}

          <label>
            <span><FiMail /> Email address</span>
            <input
              type="email"
              value={form.email}
              onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
              placeholder="name@example.com"
            />
          </label>

          <label>
            <span><FiLock /> Password</span>
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
              placeholder="Enter password"
            />
          </label>

          {error && <div className="error-banner">{error}</div>}

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Processing...' : mode === 'login' ? 'Sign in' : 'Register'}
          </button>
        </form>
      </section>
    </div>
  );
}