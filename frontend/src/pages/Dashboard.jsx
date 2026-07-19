import { useEffect, useMemo, useState } from 'react';
import { FiLogOut, FiSend, FiCheck, FiX, FiClock, FiDollarSign, FiFileText, FiActivity, FiUsers } from 'react-icons/fi';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { StatusBadge } from '../components/StatusBadge';

const loanFormInitial = {
  amount: '',
  tenureMonths: '',
  purpose: '',
  assignedAdminId: '',
};

export function Dashboard() {
  const { user, logout } = useAuth();
  const [loans, setLoans] = useState([]);
  const [admins, setAdmins] = useState([]);
  const [loanForm, setLoanForm] = useState(loanFormInitial);
  const [decisionNotes, setDecisionNotes] = useState({});
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  async function loadLoans() {
    setError('');
    try {
      const response = await api.getLoans();
      setLoans(response);
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadAdmins() {
    try {
      const response = await api.getAdmins();
      setAdmins(response);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => {
    loadLoans();
    if (!user || user.role !== 'ADMIN') {
      loadAdmins();
    }
  }, []);

  const stats = useMemo(() => {
    const pending = loans.filter((loan) => loan.status === 'PENDING').length;
    const approved = loans.filter((loan) => loan.status === 'APPROVED').length;
    const rejected = loans.filter((loan) => loan.status === 'REJECTED').length;
    return { pending, approved, rejected, total: loans.length };
  }, [loans]);

  async function handleApply(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      await api.applyLoan({
        amount: Number(loanForm.amount),
        tenureMonths: Number(loanForm.tenureMonths),
        purpose: loanForm.purpose,
        assignedAdminId: Number(loanForm.assignedAdminId),
      });
      setLoanForm(loanFormInitial);
      await loadLoans();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleDecision(id, decision) {
    setBusy(true);
    setError('');
    try {
      const reason = decisionNotes[id] || '';
      if (decision === 'approve') {
        await api.approveLoan(id, reason);
      } else {
        await api.rejectLoan(id, reason);
      }
      await loadLoans();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="dashboard-shell">
      <header className="topbar">
        <div>
          <div className="eyebrow subtle">
            <FiActivity /> Loan control center
          </div>
          <h2>{isAdmin ? 'Admin review console' : 'Customer loan portal'}</h2>
          <p>Signed in as {user?.fullName} ({user?.email})</p>
        </div>
        <button className="secondary-button" onClick={logout}>
          <FiLogOut /> Logout
        </button>
      </header>

      <section className="stats-grid">
        <article><FiFileText /><strong>{stats.total}</strong><span>Total</span></article>
        <article><FiClock /><strong>{stats.pending}</strong><span>Pending</span></article>
        <article><FiCheck /><strong>{stats.approved}</strong><span>Approved</span></article>
        <article><FiX /><strong>{stats.rejected}</strong><span>Rejected</span></article>
      </section>

      {!isAdmin && (
        <section className="content-grid">
          <form className="panel form-panel" onSubmit={handleApply}>
            <h3><FiSend /> Apply for a loan</h3>
            <label>
              <span><FiUsers /> Assign to admin</span>
              <select
                value={loanForm.assignedAdminId || ''}
                onChange={(event) => setLoanForm((current) => ({ ...current, assignedAdminId: event.target.value }))}
                required
              >
                <option value="">Select an admin</option>
                {admins.map((admin) => (
                  <option key={admin.id} value={admin.id}>
                    {admin.fullName}{admin.organizationName ? ` - ${admin.organizationName}` : ''}
                  </option>
                ))}
              </select>
            </label>
            <label>
              <span><FiDollarSign /> Amount</span>
              <input
                type="number"
                min="1"
                step="0.01"
                value={loanForm.amount}
                onChange={(event) => setLoanForm((current) => ({ ...current, amount: event.target.value }))}
              />
            </label>
            <label>
              <span><FiClock /> Tenure in months</span>
              <input
                type="number"
                min="1"
                value={loanForm.tenureMonths}
                onChange={(event) => setLoanForm((current) => ({ ...current, tenureMonths: event.target.value }))}
              />
            </label>
            <label>
              <span><FiFileText /> Purpose</span>
              <textarea
                rows="4"
                value={loanForm.purpose}
                onChange={(event) => setLoanForm((current) => ({ ...current, purpose: event.target.value }))}
              />
            </label>
            {error && <div className="error-banner">{error}</div>}
            <button className="primary-button" type="submit" disabled={busy}>
              Submit application
            </button>
          </form>

          <section className="panel table-panel">
            <h3>My applications</h3>
            <LoanTable loans={loans} />
          </section>
        </section>
      )}

      {isAdmin && (
        <section className="panel table-panel admin-panel">
          <h3><FiUsers /> All applications</h3>
          {user?.organizationName && <p className="portal-summary">Organization: {user.organizationName}</p>}
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Customer</th>
                  <th>Assigned admin</th>
                  <th>Amount</th>
                  <th>Tenure</th>
                  <th>Purpose</th>
                  <th>Status</th>
                  <th>Decision note</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loans.map((loan) => (
                  <tr key={loan.id}>
                    <td>
                      <strong>{loan.customerName}</strong>
                      <span>{loan.customerEmail}</span>
                    </td>
                    <td>
                      <strong>{loan.assignedAdminName}</strong>
                      <span>{loan.assignedAdminOrganizationName || '-'}</span>
                    </td>
                    <td>${Number(loan.amount).toFixed(2)}</td>
                    <td>{loan.tenureMonths} months</td>
                    <td>{loan.purpose}</td>
                    <td><StatusBadge status={loan.status} /></td>
                    <td>
                      <input
                        className="inline-input"
                        placeholder="Optional reason"
                        value={decisionNotes[loan.id] || ''}
                        onChange={(event) => setDecisionNotes((current) => ({ ...current, [loan.id]: event.target.value }))}
                      />
                    </td>
                    <td className="action-cell">
                      <button
                        className="mini-button approve"
                        disabled={busy || loan.status !== 'PENDING'}
                        onClick={() => handleDecision(loan.id, 'approve')}
                      >
                        Approve
                      </button>
                      <button
                        className="mini-button reject"
                        disabled={busy || loan.status !== 'PENDING'}
                        onClick={() => handleDecision(loan.id, 'reject')}
                      >
                        Reject
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </div>
  );
}

function LoanTable({ loans }) {
  if (loans.length === 0) {
    return <div className="empty-state">No applications yet.</div>;
  }

  return (
    <div className="loan-list">
      {loans.map((loan) => (
        <article key={loan.id} className="loan-card">
          <div className="loan-card__header">
            <div>
              <strong>${Number(loan.amount).toFixed(2)}</strong>
              <span>{loan.tenureMonths} months</span>
            </div>
            <StatusBadge status={loan.status} />
          </div>
          <small>Assigned to {loan.assignedAdminName}{loan.assignedAdminOrganizationName ? ` - ${loan.assignedAdminOrganizationName}` : ''}</small>
          <p>{loan.purpose}</p>
          <small>Submitted {new Date(loan.submittedAt).toLocaleString()}</small>
          {loan.decisionReason && <small>Decision note: {loan.decisionReason}</small>}
        </article>
      ))}
    </div>
  );
}