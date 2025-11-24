import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(() => {
    try {
      const storedToken = localStorage.getItem('token');
      return storedToken && storedToken !== 'undefined' && storedToken !== 'null' ? storedToken : null;
    } catch (error) {
      return null;
    }
  });

  useEffect(() => {
    try {
      const storedUser = localStorage.getItem('user');
      if (storedUser && storedUser !== 'undefined' && storedUser !== 'null') {
        const parsedUser = JSON.parse(storedUser);
        if (parsedUser && typeof parsedUser === 'object') {
          setUser(parsedUser);
        }
      }
    } catch (error) {
      console.error('Error al parsear usuario desde localStorage:', error);
      // Limpiar localStorage si hay datos corruptos
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    } finally {
      setLoading(false);
    }
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);
      const { token: newToken, username, email, rol, idUsuario } = response.data;
      
      // Construir objeto usuario con los datos de la respuesta
      const usuario = {
        idUsuario,
        username,
        email,
        rol,
        nombre: username, // Usar username como nombre por defecto
      };
      
      setToken(newToken);
      setUser(usuario);
      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(usuario));
      
      return { success: true };
    } catch (error) {
      console.error('Error en login:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          error.message || 
                          'Error al iniciar sesión. Verifica tus credenciales.';
      return { 
        success: false, 
        error: errorMessage
      };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const isAuthenticated = () => {
    return !!token && !!user;
  };

  const hasRole = (role) => {
    return user?.rol === role;
  };

  const hasAnyRole = (roles) => {
    return roles.includes(user?.rol);
  };

  const value = {
    user,
    token,
    login,
    logout,
    isAuthenticated,
    hasRole,
    hasAnyRole,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

