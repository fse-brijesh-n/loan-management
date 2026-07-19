import { AuthPage } from './pages/AuthPage';
import { Dashboard } from './pages/Dashboard';
import { useAuth } from './context/AuthContext';

export default function App() {
  const { isAuthenticated } = useAuth();
  return <main className="app-frame">{isAuthenticated ? <Dashboard /> : <AuthPage />}</main>;
}