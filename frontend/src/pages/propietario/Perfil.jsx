import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { propietarioService, usuarioService } from '../../services/api';

export default function PerfilPage() {
  const { user } = useAuth();
  const [propietario, setPropietario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [modoEdicion, setModoEdicion] = useState(false);
  const [cambiarPassword, setCambiarPassword] = useState(false);

  const [formData, setFormData] = useState({
    nombres: '',
    apellidos: '',
    tipoDocumento: '',
    documento: '',
    telefono: '',
    email: '',
    direccion: '',
    observaciones: ''
  });

  const [passwordData, setPasswordData] = useState({
    passwordActual: '',
    passwordNueva: '',
    confirmPassword: ''
  });

  const tiposDocumento = ['CC', 'CE', 'TI', 'PASAPORTE'];

  useEffect(() => {
    loadPerfil();
  }, []);

  const loadPerfil = async () => {
    try {
      setLoading(true);
      setError('');

      console.log('🔍 Obteniendo perfil del propietario...');
      const response = await propietarioService.obtenerOCrearMiPerfil();
      const perfilData = response.data;

      console.log('✅ Perfil obtenido:', perfilData);
      setPropietario(perfilData);

      // Cargar datos en el formulario (solo campos que se pueden actualizar)
      setFormData({
        nombres: perfilData.nombres || '',
        apellidos: perfilData.apellidos || '',
        tipoDocumento: perfilData.tipoDocumento || 'CC',
        documento: perfilData.documento || '',
        telefono: perfilData.telefono || '',
        email: perfilData.email || '',
        direccion: perfilData.direccion || '',
        observaciones: perfilData.observaciones || ''
      });
    } catch (error) {
      console.error('❌ Error al cargar perfil:', error);
      setError(`Error al cargar el perfil: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      if (!propietario?.idPropietario) {
        throw new Error('No se encontró el identificador del propietario para actualizar');
      }

      // Mapear los datos al formato que espera el backend
      const datosActualizacion = {
        documento: formData.documento,
        tipoDocumento: formData.tipoDocumento,
        nombres: formData.nombres,
        apellidos: formData.apellidos,
        telefono: formData.telefono,
        email: formData.email,
        direccion: formData.direccion || null,
        observaciones: formData.observaciones || null
      };
      
      await propietarioService.update(propietario.idPropietario, datosActualizacion);
      setSuccess('Perfil actualizado exitosamente');
      setModoEdicion(false);
      await loadPerfil();

      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error al actualizar perfil:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleCambiarPassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validaciones
    if (passwordData.passwordNueva.length < 6) {
      setError('La nueva contraseña debe tener al menos 6 caracteres');
      return;
    }

    if (passwordData.passwordNueva !== passwordData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    try {
      // Obtener el ID del usuario desde el contexto
      if (!user?.idUsuario) {
        setError('No se pudo identificar el usuario');
        return;
      }

      await usuarioService.cambiarPassword(user.idUsuario, {
        passwordActual: passwordData.passwordActual,
        passwordNueva: passwordData.passwordNueva
      });

      setSuccess('Contraseña cambiada exitosamente');
      setCambiarPassword(false);
      setPasswordData({
        passwordActual: '',
        passwordNueva: '',
        confirmPassword: ''
      });

      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error al cambiar contraseña:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando perfil...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Mi Perfil</h2>
        <div className="flex gap-3">
          {!modoEdicion && !cambiarPassword && (
            <>
              <button
                onClick={() => setCambiarPassword(true)}
                className="bg-gray-600 text-white px-4 py-2 rounded-lg hover:bg-gray-700 transition-colors"
              >
                Cambiar Contraseña
              </button>
              <button
                onClick={() => setModoEdicion(true)}
                className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
              >
                Editar Perfil
              </button>
            </>
          )}
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {success}
        </div>
      )}

      {!cambiarPassword ? (
        <div className="bg-white rounded-lg shadow">
          <div className="p-6">
            {/* Información del usuario */}
            <div className="mb-6 pb-6 border-b">
              <h3 className="text-lg font-semibold mb-4">Información de Usuario</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-600 mb-1">
                    Correo Electrónico
                  </label>
                  <div className="text-gray-900">{user?.email || 'N/A'}</div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-600 mb-1">
                    Usuario
                  </label>
                  <div className="text-gray-900">{user?.username || 'N/A'}</div>
                </div>
              </div>
            </div>

            {modoEdicion ? (
              <form onSubmit={handleSubmit} className="space-y-6">
                <h3 className="text-lg font-semibold">Información Personal</h3>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombres *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.nombres}
                      onChange={(e) => setFormData({...formData, nombres: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Apellidos *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.apellidos}
                      onChange={(e) => setFormData({...formData, apellidos: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Tipo de Documento *
                    </label>
                    <select
                      required
                      value={formData.tipoDocumento}
                      onChange={(e) => setFormData({...formData, tipoDocumento: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      {tiposDocumento.map(tipo => (
                        <option key={tipo} value={tipo}>{tipo}</option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Número de Documento *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.documento}
                      onChange={(e) => setFormData({...formData, documento: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Teléfono *
                    </label>
                    <input
                      type="tel"
                      required
                      value={formData.telefono}
                      onChange={(e) => setFormData({...formData, telefono: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Correo Electrónico *
                    </label>
                    <input
                      type="email"
                      required
                      value={formData.email}
                      onChange={(e) => setFormData({...formData, email: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Dirección
                    </label>
                    <input
                      type="text"
                      value={formData.direccion}
                      onChange={(e) => setFormData({...formData, direccion: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                      placeholder="Ej: Calle Principal 123, Ciudad"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Observaciones
                    </label>
                    <textarea
                      value={formData.observaciones}
                      onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                      rows="3"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                      placeholder="Notas adicionales sobre el propietario"
                    />
                  </div>
                </div>

                <div className="flex justify-end gap-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => {
                      setModoEdicion(false);
                      loadPerfil(); // Recargar datos originales
                    }}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                  >
                    Guardar Cambios
                  </button>
                </div>
              </form>
            ) : (
              <div className="space-y-6">
                <h3 className="text-lg font-semibold">Información Personal</h3>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Nombres
                    </label>
                    <div className="text-gray-900">{propietario?.nombres || 'N/A'}</div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Apellidos
                    </label>
                    <div className="text-gray-900">{propietario?.apellidos || 'N/A'}</div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Documento
                    </label>
                    <div className="text-gray-900">
                      {propietario?.tipoDocumento} {propietario?.documento || 'N/A'}
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Teléfono
                    </label>
                    <div className="text-gray-900">{propietario?.telefono || 'N/A'}</div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Correo Electrónico
                    </label>
                    <div className="text-gray-900">{propietario?.email || 'N/A'}</div>
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-600 mb-1">
                      Dirección
                    </label>
                    <div className="text-gray-900">{propietario?.direccion || 'N/A'}</div>
                  </div>

                  {propietario?.observaciones && (
                    <div className="md:col-span-2">
                      <label className="block text-sm font-medium text-gray-600 mb-1">
                        Observaciones
                      </label>
                      <div className="text-gray-900">{propietario.observaciones}</div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      ) : (
        // Formulario de cambio de contraseña
        <div className="bg-white rounded-lg shadow">
          <div className="p-6">
            <h3 className="text-lg font-semibold mb-6">Cambiar Contraseña</h3>

            <form onSubmit={handleCambiarPassword} className="space-y-4 max-w-md">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Contraseña Actual *
                </label>
                <input
                  type="password"
                  required
                  value={passwordData.passwordActual}
                  onChange={(e) => setPasswordData({...passwordData, passwordActual: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nueva Contraseña *
                </label>
                <input
                  type="password"
                  required
                  minLength={6}
                  value={passwordData.passwordNueva}
                  onChange={(e) => setPasswordData({...passwordData, passwordNueva: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
                <p className="text-xs text-gray-500 mt-1">Mínimo 6 caracteres</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Confirmar Nueva Contraseña *
                </label>
                <input
                  type="password"
                  required
                  minLength={6}
                  value={passwordData.confirmPassword}
                  onChange={(e) => setPasswordData({...passwordData, confirmPassword: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t">
                <button
                  type="button"
                  onClick={() => {
                    setCambiarPassword(false);
                    setPasswordData({
                      passwordActual: '',
                      passwordNueva: '',
                      confirmPassword: ''
                    });
                    setError('');
                  }}
                  className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  Cambiar Contraseña
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información importante</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Mantén tu información actualizada para que podamos contactarte en caso de emergencias.</li>
              <li>• Cambia tu contraseña periódicamente para mantener tu cuenta segura.</li>
              <li>• Si tienes problemas para actualizar tu perfil, contacta al administrador.</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
