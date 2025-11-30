import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import { LogIn, KeyRound, UserPlus, User, Lock, Mail, Phone, MapPin, FileText, Heart } from 'lucide-react';

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
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 flex items-center justify-center p-4 py-8 relative overflow-hidden">
      {/* Decorative background elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-br from-blue-400/20 to-purple-400/20 rounded-full blur-3xl"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-gradient-to-tr from-indigo-400/20 to-pink-400/20 rounded-full blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-gradient-to-br from-cyan-400/10 to-blue-400/10 rounded-full blur-3xl"></div>
      </div>

      <div className={`bg-white/80 backdrop-blur-xl rounded-3xl shadow-2xl p-8 w-full max-w-md relative z-10 border border-white/20 ${view === 'register' ? 'max-h-[90vh] flex flex-col' : ''}`}>
        {/* Logo and Header */}
        <div className="text-center mb-8">
          <div className="relative inline-block mb-4">
            <div className="absolute inset-0 bg-gradient-to-r from-blue-600 to-purple-600 rounded-full blur-xl opacity-40 animate-pulse"></div>
            <div className="relative inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-blue-500 via-indigo-500 to-purple-600 rounded-full shadow-lg transform hover:scale-105 transition-transform duration-300">
              {view === 'login' && <LogIn className="w-10 h-10 text-white" strokeWidth={2.5} />}
              {view === 'reset' && <KeyRound className="w-10 h-10 text-white" strokeWidth={2.5} />}
              {view === 'register' && <UserPlus className="w-10 h-10 text-white" strokeWidth={2.5} />}
            </div>
          </div>
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent mb-2">
            Clínica Veterinaria
          </h1>
          <div className="flex items-center justify-center gap-2 mb-3">
            <Heart className="w-5 h-5 text-pink-500 fill-pink-500 animate-pulse" />
            <p className="text-sm text-gray-600 font-medium">Cuidando a tus mejores amigos</p>
            <Heart className="w-5 h-5 text-pink-500 fill-pink-500 animate-pulse" />
          </div>
          <p className="text-gray-600 text-sm">
            {view === 'login' && 'Inicia sesión en tu cuenta'}
            {view === 'reset' && 'Recupera el acceso a tu cuenta'}
            {view === 'register' && 'Únete a nuestra familia'}
          </p>
        </div>

        {/* Tabs with modern design */}
        <div className="flex bg-gray-100 rounded-xl p-1 mb-6">
          <button
            onClick={() => { setView('login'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2.5 px-4 text-sm font-semibold text-center rounded-lg transition-all duration-300 ${
              view === 'login'
                ? 'bg-white text-blue-600 shadow-md'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Iniciar Sesión
          </button>
          <button
            onClick={() => { setView('reset'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2.5 px-4 text-sm font-semibold text-center rounded-lg transition-all duration-300 ${
              view === 'reset'
                ? 'bg-white text-blue-600 shadow-md'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Recuperar
          </button>
          <button
            onClick={() => { setView('register'); setError(''); setSuccess(''); }}
            className={`flex-1 py-2.5 px-4 text-sm font-semibold text-center rounded-lg transition-all duration-300 ${
              view === 'register'
                ? 'bg-white text-blue-600 shadow-md'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Registrarse
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border-l-4 border-red-500 text-red-700 px-4 py-3 rounded-lg mb-4 shadow-sm animate-fadeIn">
            <div className="flex items-center gap-2">
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <span className="font-medium">{error}</span>
            </div>
          </div>
        )}

        {success && (
          <div className="bg-green-50 border-l-4 border-green-500 text-green-700 px-4 py-3 rounded-lg mb-4 shadow-sm animate-fadeIn">
            <div className="flex items-center gap-2">
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              <span className="font-medium">{success}</span>
            </div>
          </div>
        )}

        {/* Login Form */}
        {view === 'login' && (
          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label htmlFor="username" className="block text-sm font-semibold text-gray-700 mb-2">
                Usuario
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="username"
                  type="text"
                  required
                  value={credentials.username}
                  onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400"
                  placeholder="Ingresa tu usuario"
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-semibold text-gray-700 mb-2">
                Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="password"
                  type="password"
                  required
                  value={credentials.password}
                  onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400"
                  placeholder="Ingresa tu contraseña"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-3 px-4 rounded-xl hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Iniciando sesión...
                </span>
              ) : 'Iniciar Sesión'}
            </button>
          </form>
        )}

        {/* Reset Password Form */}
        {view === 'reset' && (
          <form onSubmit={handleResetPassword} className="space-y-5">
            <div>
              <label htmlFor="reset-username" className="block text-sm font-semibold text-gray-700 mb-2">
                Nombre de Usuario
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="reset-username"
                  type="text"
                  required
                  value={resetData.username}
                  onChange={(e) => setResetData({ ...resetData, username: e.target.value })}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400"
                  placeholder="Ingresa tu nombre de usuario"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reset-password" className="block text-sm font-semibold text-gray-700 mb-2">
                Nueva Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="reset-password"
                  type="password"
                  required
                  value={resetData.nuevaPassword}
                  onChange={(e) => setResetData({ ...resetData, nuevaPassword: e.target.value })}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400"
                  placeholder="Ingresa tu nueva contraseña"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reset-confirm" className="block text-sm font-semibold text-gray-700 mb-2">
                Confirmar Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="reset-confirm"
                  type="password"
                  required
                  value={resetData.confirmPassword}
                  onChange={(e) => setResetData({ ...resetData, confirmPassword: e.target.value })}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400"
                  placeholder="Confirma tu nueva contraseña"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-3 px-4 rounded-xl hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Cambiando contraseña...
                </span>
              ) : 'Cambiar Contraseña'}
            </button>
          </form>
        )}

        {/* Register Form */}
        {view === 'register' && (
          <form onSubmit={handleRegister} className="space-y-4 flex-1 overflow-y-auto pr-2 min-h-0">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label htmlFor="reg-tipo-doc" className="block text-sm font-semibold text-gray-700 mb-2">
                  Tipo Doc
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <FileText className="h-4 w-4 text-gray-400" />
                  </div>
                  <select
                    id="reg-tipo-doc"
                    required
                    value={registerData.tipoDocumento}
                    onChange={(e) => setRegisterData({ ...registerData, tipoDocumento: e.target.value })}
                    className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  >
                    <option value="CC">CC</option>
                    <option value="CE">CE</option>
                    <option value="TI">TI</option>
                    <option value="PASAPORTE">Pasaporte</option>
                  </select>
                </div>
              </div>
              <div>
                <label htmlFor="reg-documento" className="block text-sm font-semibold text-gray-700 mb-2">
                  Documento
                </label>
                <input
                  id="reg-documento"
                  type="text"
                  required
                  value={registerData.documento}
                  onChange={(e) => setRegisterData({ ...registerData, documento: e.target.value })}
                  className="w-full px-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="1234567890"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-nombres" className="block text-sm font-semibold text-gray-700 mb-2">
                Nombres
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-nombres"
                  type="text"
                  required
                  value={registerData.nombres}
                  onChange={(e) => setRegisterData({ ...registerData, nombres: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="Juan"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-apellidos" className="block text-sm font-semibold text-gray-700 mb-2">
                Apellidos
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-apellidos"
                  type="text"
                  required
                  value={registerData.apellidos}
                  onChange={(e) => setRegisterData({ ...registerData, apellidos: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="Pérez García"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-username" className="block text-sm font-semibold text-gray-700 mb-2">
                Nombre de Usuario
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-username"
                  type="text"
                  required
                  value={registerData.username}
                  onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="juan.perez"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-email" className="block text-sm font-semibold text-gray-700 mb-2">
                Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Mail className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-email"
                  type="email"
                  required
                  value={registerData.email}
                  onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="juan.perez@example.com"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-telefono" className="block text-sm font-semibold text-gray-700 mb-2">
                Teléfono
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Phone className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-telefono"
                  type="tel"
                  required
                  value={registerData.telefono}
                  onChange={(e) => setRegisterData({ ...registerData, telefono: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="3001234567"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-direccion" className="block text-sm font-semibold text-gray-700 mb-2">
                Dirección (Opcional)
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <MapPin className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-direccion"
                  type="text"
                  value={registerData.direccion}
                  onChange={(e) => setRegisterData({ ...registerData, direccion: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="Calle 123 #45-67"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-password" className="block text-sm font-semibold text-gray-700 mb-2">
                Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-password"
                  type="password"
                  required
                  value={registerData.password}
                  onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="Mínimo 6 caracteres"
                />
              </div>
            </div>

            <div>
              <label htmlFor="reg-confirm-password" className="block text-sm font-semibold text-gray-700 mb-2">
                Confirmar Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  id="reg-confirm-password"
                  type="password"
                  required
                  value={registerData.confirmPassword}
                  onChange={(e) => setRegisterData({ ...registerData, confirmPassword: e.target.value })}
                  className="w-full pl-9 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:border-gray-400 text-sm"
                  placeholder="Confirma tu contraseña"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-3 px-4 rounded-xl hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 mt-2"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Registrando...
                </span>
              ) : 'Registrarse'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

