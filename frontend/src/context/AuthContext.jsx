import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  // En lugar de lanzar un error, retornar un objeto por defecto
  // Esto previene errores durante el renderizado inicial
  if (!context) {
    console.warn('useAuth se está usando fuera de AuthProvider, retornando valores por defecto');
    return {
      user: null,
      token: null,
      login: async () => ({ success: false, error: 'No autenticado' }),
      logout: () => {},
      isAuthenticated: () => false,
      hasRole: () => false,
      hasAnyRole: () => false,
      loading: true,
    };
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
      console.log('🔐 Intentando login con:', { 
        username: credentials.username,
        passwordLength: credentials.password?.length || 0 
      });
      
      // Validar que las credenciales no estén vacías
      if (!credentials.username || !credentials.password) {
        return {
          success: false,
          error: 'Por favor completa todos los campos'
        };
      }
      
      const response = await authService.login(credentials);
      console.log('✅ Respuesta del backend:', response.data);

      const { token: newToken, username, email, rol, idUsuario } = response.data;

      // Normalizar el rol a mayúsculas para consistencia
      const rolNormalizado = rol ? rol.toUpperCase() : rol;

      // Construir objeto usuario con los datos de la respuesta
      const usuario = {
        idUsuario,
        username,
        email,
        rol: rolNormalizado,
        nombre: username, // Usar username como nombre por defecto
      };

      console.log('👤 Usuario creado:', usuario);
      console.log('🎭 Rol del usuario (normalizado):', rolNormalizado);
      console.log('🔑 Token recibido:', newToken ? 'Presente' : 'Ausente');

      // Verificar que el token no sea null o undefined
      if (!newToken) {
        console.error('❌ Error: Token no recibido del backend');
        return {
          success: false,
          error: 'No se recibió token de autenticación'
        };
      }

      setToken(newToken);
      setUser(usuario);
      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(usuario));

      // Verificar que se guardó correctamente
      const tokenGuardado = localStorage.getItem('token');
      const userGuardado = localStorage.getItem('user');
      console.log('💾 Token guardado en localStorage:', tokenGuardado ? 'Sí' : 'No');
      console.log('💾 Usuario guardado en localStorage:', userGuardado ? 'Sí' : 'No');

      return { success: true };
    } catch (error) {
      console.error('❌ Error en login:', error);
      console.error('❌ Respuesta del error:', error.response?.data);
      
      let errorMessage = 'Error al iniciar sesión. Verifica tus credenciales.';
      
      if (error.response?.status === 401) {
        const backendMessage = error.response?.data?.message || '';
        if (backendMessage.includes('Credenciales inválidas')) {
          errorMessage = 'Usuario o contraseña incorrectos. Verifica tus credenciales o usa la opción "Cambiar Contraseña" si olvidaste tu contraseña.';
        } else if (backendMessage.includes('Usuario inactivo')) {
          errorMessage = 'Tu cuenta está inactiva. Contacta al administrador.';
        } else if (backendMessage.includes('Usuario bloqueado')) {
          errorMessage = `Tu cuenta está bloqueada: ${error.response?.data?.message || 'Contacta al administrador'}`;
        } else {
          errorMessage = backendMessage || 'Credenciales inválidas. Verifica tu usuario y contraseña.';
        }
      } else if (error.response?.status === 400) {
        errorMessage = error.response?.data?.message || 'Datos de entrada inválidos. Verifica que hayas completado todos los campos.';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
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
    const userRol = user?.rol?.toUpperCase();
    const roleToCheck = role?.toUpperCase();
    const result = userRol === roleToCheck;
    console.log(`🔍 hasRole - Comparando "${userRol}" con "${roleToCheck}": ${result}`);
    return result;
  };

  const hasAnyRole = (roles) => {
    const userRol = user?.rol?.toUpperCase();
    const rolesNormalizados = roles.map(r => r?.toUpperCase());
    const result = rolesNormalizados.includes(userRol);
    console.log(`🔍 hasAnyRole - Rol usuario: "${userRol}", Roles permitidos: [${rolesNormalizados.join(', ')}], Resultado: ${result}`);
    return result;
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

