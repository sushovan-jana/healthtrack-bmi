import React, { createContext, useState, useEffect, useContext } from 'react';
import apiClient from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const checkSession = async () => {
    // If no token in localStorage, skip network call — user is not logged in
    const token = localStorage.getItem('jwt-token');
    if (!token) {
      setLoading(false);
      return;
    }
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000); // 10 second timeout
    try {
      const response = await apiClient.get('/doctors/auth/me', {
        signal: controller.signal,
      });
      setUser(response.data);
    } catch (error) {
      setUser(null);
      localStorage.removeItem('jwt-token'); // Clear invalid token
    } finally {
      clearTimeout(timeoutId);
      setLoading(false);
    }
  };

  useEffect(() => {
    checkSession();

    // Listen for unauthorized interceptor events
    const handleUnauthorized = () => {
      setUser(null);
      localStorage.removeItem('jwt-token');
    };

    window.addEventListener('auth-unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('auth-unauthorized', handleUnauthorized);
    };
  }, []);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await apiClient.post('/auth/login', { email, password });
      // Store JWT in localStorage for cross-domain Bearer token auth
      if (response.data.token) {
        localStorage.setItem('jwt-token', response.data.token);
      }
      setUser(response.data);
      return response.data;
    } catch (error) {
      setUser(null);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (email, password, name) => {
    setLoading(true);
    try {
      const response = await apiClient.post('/auth/register', { email, password, name });
      // Store JWT in localStorage for cross-domain Bearer token auth
      if (response.data.token) {
        localStorage.setItem('jwt-token', response.data.token);
      }
      setUser(response.data);
      return response.data;
    } catch (error) {
      setUser(null);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    setLoading(true);
    try {
      await apiClient.post('/doctors/auth/logout');
    } catch (error) {
      console.error('Logout error', error);
    } finally {
      setUser(null);
      localStorage.removeItem('jwt-token'); // Clear token on logout
      setLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, checkSession }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
