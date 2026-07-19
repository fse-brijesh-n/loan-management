import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('lm_token'));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('lm_user');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (token) {
      localStorage.setItem('lm_token', token);
    } else {
      localStorage.removeItem('lm_token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('lm_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('lm_user');
    }
  }, [user]);

  const value = useMemo(() => ({
    token,
    user,
    isAuthenticated: Boolean(token),
    async login(credentials) {
      const response = await api.login(credentials);
      setToken(response.token);
      setUser(response);
      return response;
    },
    async register(payload) {
      const response = await api.register(payload);
      setToken(response.token);
      setUser(response);
      return response;
    },
    logout() {
      setToken(null);
      setUser(null);
    },
  }), [token, user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}