import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import { LogIn, KeyRound, UserPlus } from 'lucide-react';

export default function Login() {
  const [view, setView] = useState('login'); // 'login', 'reset', 'register'
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [resetData, setResetData] = useState({ username: '', nuevaPassword: '', confirmPassword: '' });
  const [registerData, setRegisterData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    documento: '',
    tipoDocumento: 'CC',
    nombres: '',
    apellidos: '',
    telefono: '',
    direccion: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    const result = await login(credentials);
    
    if (result.success) {
      navigate('/');
    } else {
      setError(result.error);
    }
    
    setLoading(false);
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (resetData.nuevaPassword !== resetData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    if (resetData.nuevaPassword.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setLoading(true);
    try {
      await authService.resetPasswordByUsername({
        username: resetData.username,
        nuevaPassword: resetData.nuevaPassword
      });
      setSuccess('Contraseña cambiada exitosamente. Ahora puedes iniciar sesión.');
      setTimeout(() => {
        setView('login');
        setResetData({ username: '', nuevaPassword: '', confirmPassword: '' });
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Error al cambiar la contraseña');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (registerData.password !== registerData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    if (registerData.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setLoading(true);
    try {
      const { confirmPassword, ...dataToSend } = registerData;
      await authService.registerPropietario(dataToSend);
      setSuccess('Registro exitoso. Ahora puedes iniciar sesión.');
      setTimeout(() => {
        setView('login');
        setRegisterData({
          username: '',
          email: '',
          password: '',
          confirmPassword: '',
          documento: '',
          tipoDocumento: 'CC',
          nombres: '',
          apellidos: '',
          telefono: '',
          direccion: ''
        });
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Error al registrar');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-primary-100 flex items-center justify-center p-4 py-8">
      <div className={`bg-white rounded-lg shadow-xl p-8 w-full max-w-md ${view === 'register' ? 'max-h-[90vh] flex flex-col' : ''}`}>
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
            {view === 'login' && <LogIn className="w-8 h-8 text-primary-600" />}
            {view === 'reset' && <KeyRound className="w-8 h-8 text-primary-600" />}
            {view === 'register' && <UserPlus className="w-8 h-8 text-primary-600" />}
          </div>
          <h1 className="text-3xl font-bold text-gray-900">Clínica Veterinaria</h1>
          <p className="text-gray-600 mt-2">
            {view === 'login' && 'Inicia sesión en tu cuenta'}
            {view === 'reset' && 'Cambiar contraseña'}
            {view === 'register' && 'Registrarse como propietario'}
          </p>
        </div>

        {/* Tabs */}
        <div className="flex border-b mb-6">
          <button
            onClick={() => { setView('login'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2 px-4 text-sm font-medium text-center ${
              view === 'login'
                ? 'border-b-2 border-primary-600 text-primary-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Iniciar Sesión
          </button>
          <button
            onClick={() => { setView('reset'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2 px-4 text-sm font-medium text-center ${
              view === 'reset'
                ? 'border-b-2 border-primary-600 text-primary-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Cambiar Contraseña
          </button>
          <button
            onClick={() => { setView('register'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2 px-4 text-sm font-medium text-center ${
              view === 'register'
                ? 'border-b-2 border-primary-600 text-primary-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Registrarse
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {success && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4">
            {success}
          </div>
        )}

        {/* Login Form */}
        {view === 'login' && (
          <form onSubmit={handleLogin} className="space-y-6">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
                Usuario
              </label>
              <input
                id="username"
                type="text"
                required
                value={credentials.username}
                onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ingresa tu usuario"
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Contraseña
              </label>
              <input
                id="password"
                type="password"
                required
                value={credentials.password}
                onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ingresa tu contraseña"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary-600 text-white py-2 px-4 rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
          </form>
        )}

        {/* Reset Password Form */}
        {view === 'reset' && (
          <form onSubmit={handleResetPassword} className="space-y-6">
            <div>
              <label htmlFor="reset-username" className="block text-sm font-medium text-gray-700 mb-2">
                Nombre de Usuario
              </label>
              <input
                id="reset-username"
                type="text"
                required
                value={resetData.username}
                onChange={(e) => setResetData({ ...resetData, username: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ingresa tu nombre de usuario"
              />
            </div>

            <div>
              <label htmlFor="reset-password" className="block text-sm font-medium text-gray-700 mb-2">
                Nueva Contraseña
              </label>
              <input
                id="reset-password"
                type="password"
                required
                value={resetData.nuevaPassword}
                onChange={(e) => setResetData({ ...resetData, nuevaPassword: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ingresa tu nueva contraseña"
              />
            </div>

            <div>
              <label htmlFor="reset-confirm" className="block text-sm font-medium text-gray-700 mb-2">
                Confirmar Contraseña
              </label>
              <input
                id="reset-confirm"
                type="password"
                required
                value={resetData.confirmPassword}
                onChange={(e) => setResetData({ ...resetData, confirmPassword: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Confirma tu nueva contraseña"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary-600 text-white py-2 px-4 rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {loading ? 'Cambiando contraseña...' : 'Cambiar Contraseña'}
            </button>
          </form>
        )}

        {/* Register Form */}
        {view === 'register' && (
          <form onSubmit={handleRegister} className="space-y-4 flex-1 overflow-y-auto pr-2 min-h-0">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="reg-tipo-doc" className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo Doc
                </label>
                <select
                  id="reg-tipo-doc"
                  required
                  value={registerData.tipoDocumento}
                  onChange={(e) => setRegisterData({ ...registerData, tipoDocumento: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="CC">CC</option>
                  <option value="CE">CE</option>
                  <option value="TI">TI</option>
                  <option value="PASAPORTE">Pasaporte</option>
                </select>
              </div>
              <div>
                <label htmlFor="reg-documento" className="block text-sm font-medium text-gray-700 mb-2">
                  Documento
                </label>
                <input
                  id="reg-documento"
                  type="text"
                  required
                  value={registerData.documento}
                  onChange={(e) => setRegisterData({ ...registerData, documento: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="1234567890"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-nombres" className="block text-sm font-medium text-gray-700 mb-2">
                Nombres
              </label>
              <input
                id="reg-nombres"
                type="text"
                required
                value={registerData.nombres}
                onChange={(e) => setRegisterData({ ...registerData, nombres: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Juan"
              />
            </div>

            <div>
              <label htmlFor="reg-apellidos" className="block text-sm font-medium text-gray-700 mb-2">
                Apellidos
              </label>
              <input
                id="reg-apellidos"
                type="text"
                required
                value={registerData.apellidos}
                onChange={(e) => setRegisterData({ ...registerData, apellidos: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Pérez García"
              />
            </div>

            <div>
              <label htmlFor="reg-username" className="block text-sm font-medium text-gray-700 mb-2">
                Nombre de Usuario
              </label>
              <input
                id="reg-username"
                type="text"
                required
                value={registerData.username}
                onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="juan.perez"
              />
            </div>

            <div>
              <label htmlFor="reg-email" className="block text-sm font-medium text-gray-700 mb-2">
                Email
              </label>
              <input
                id="reg-email"
                type="email"
                required
                value={registerData.email}
                onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="juan.perez@example.com"
              />
            </div>

            <div>
              <label htmlFor="reg-telefono" className="block text-sm font-medium text-gray-700 mb-2">
                Teléfono
              </label>
              <input
                id="reg-telefono"
                type="tel"
                required
                value={registerData.telefono}
                onChange={(e) => setRegisterData({ ...registerData, telefono: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="3001234567"
              />
            </div>

            <div>
              <label htmlFor="reg-direccion" className="block text-sm font-medium text-gray-700 mb-2">
                Dirección (Opcional)
              </label>
              <input
                id="reg-direccion"
                type="text"
                value={registerData.direccion}
                onChange={(e) => setRegisterData({ ...registerData, direccion: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Calle 123 #45-67"
              />
            </div>

            <div>
              <label htmlFor="reg-password" className="block text-sm font-medium text-gray-700 mb-2">
                Contraseña
              </label>
              <input
                id="reg-password"
                type="password"
                required
                value={registerData.password}
                onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Mínimo 6 caracteres"
              />
            </div>

            <div>
              <label htmlFor="reg-confirm-password" className="block text-sm font-medium text-gray-700 mb-2">
                Confirmar Contraseña
              </label>
              <input
                id="reg-confirm-password"
                type="password"
                required
                value={registerData.confirmPassword}
                onChange={(e) => setRegisterData({ ...registerData, confirmPassword: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Confirma tu contraseña"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary-600 text-white py-2 px-4 rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors mt-4"
            >
              {loading ? 'Registrando...' : 'Registrarse'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

