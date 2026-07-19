const statusMeta = {
  PENDING: { label: 'Pending', tone: 'pending' },
  APPROVED: { label: 'Approved', tone: 'approved' },
  REJECTED: { label: 'Rejected', tone: 'rejected' },
};

export function StatusBadge({ status }) {
  const meta = statusMeta[status] || { label: status, tone: 'pending' };
  return <span className={`status-badge ${meta.tone}`}>{meta.label}</span>;
}