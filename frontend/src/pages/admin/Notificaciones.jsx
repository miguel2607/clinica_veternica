import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { notificacionService, usuarioService } from '../../services/api';
import { Plus, Search, Mail, MessageSquare, Bell, Send } from 'lucide-react';
import Modal from '../../components/Modal';

const CANALES = ['EMAIL', 'SMS', 'WHATSAPP', 'PUSH'];

export default function NotificacionesPage() {
  const { user } = useAuth();
  const [notificaciones, setNotificaciones] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    idUsuario: '',
    canal: 'EMAIL',
    motivo: '',
    mensaje: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (user) {
      loadData();
    }
  }, [user]);

  const loadData = async () => {
    try {
      setLoading(true);
      let notificacionesRes;
      
      // Si es ADMIN, mostrar todas las notificaciones
      // Si es VETERINARIO, mostrar solo las suyas
      if (user?.rol === 'ADMIN') {
        notificacionesRes = await notificacionService.getAll();
      } else if (user?.rol === 'VETERINARIO' && user?.idUsuario) {
        notificacionesRes = await notificacionService.getByUsuario(user.idUsuario);
      } else {
        // Para otros roles, intentar obtener por usuario
        if (user?.idUsuario) {
          notificacionesRes = await notificacionService.getByUsuario(user.idUsuario);
        } else {
          notificacionesRes = { data: [] };
        }
      }
      
      setNotificaciones(notificacionesRes.data || []);
      
      // Solo cargar usuarios si es ADMIN (para el formulario de envío)
      if (user?.rol === 'ADMIN') {
        const usuariosRes = await usuarioService.getAll();
        setUsuarios(usuariosRes.data);
      }
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = () => {
    setFormData({
      idUsuario: '',
      canal: 'EMAIL',
      motivo: '',
      mensaje: '',
    });
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await notificacionService.create(formData);
      setSuccess('Notificación enviada exitosamente');
      await loadData();
      setTimeout(() => {
        handleCloseModal();
      }, 1000);
    } catch (error) {
      console.error('Error al enviar notificación:', error);
      setError(error.response?.data?.message || 'Error al enviar notificación');
    }
  };

  const filteredNotificaciones = notificaciones.filter(
    (n) =>
      n.nombreUsuario?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      n.emailUsuario?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      n.motivo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      n.canal?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getCanalIcon = (canal) => {
    switch (canal) {
      case 'EMAIL':
        return <Mail className="w-5 h-5" />;
      case 'SMS':
        return <MessageSquare className="w-5 h-5" />;
      case 'WHATSAPP':
        return <MessageSquare className="w-5 h-5" />;
      case 'PUSH':
        return <Bell className="w-5 h-5" />;
      default:
        return <Send className="w-5 h-5" />;
    }
  };

  const getCanalColor = (canal) => {
    switch (canal) {
      case 'EMAIL':
        return 'bg-blue-100 text-blue-800';
      case 'SMS':
        return 'bg-green-100 text-green-800';
      case 'WHATSAPP':
        return 'bg-green-100 text-green-800';
      case 'PUSH':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Notificaciones</h2>
        {user?.rol === 'ADMIN' && (
          <button
            onClick={handleOpenModal}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
          >
            <Plus className="w-5 h-5 mr-2" />
            Enviar Notificación
          </button>
        )}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg animate-slideDown">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg animate-slideDown">
          {success}
        </div>
      )}

      <div className="bg-white rounded-lg shadow-lg overflow-hidden animate-scaleIn">
        <div className="p-4 border-b bg-gray-50">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar notificaciones..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-smooth"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Destinatario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Canal</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Motivo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Asunto</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredNotificaciones.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron notificaciones
                  </td>
                </tr>
              ) : (
                filteredNotificaciones.map((notificacion) => (
                  <tr key={notificacion.idComunicacion} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {notificacion.nombreUsuario || 'N/A'}
                      </div>
                      <div className="text-sm text-gray-500">
                        {notificacion.emailUsuario || 'N/A'}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full flex items-center w-fit ${getCanalColor(notificacion.canal)}`}>
                        {getCanalIcon(notificacion.canal)}
                        <span className="ml-1">{notificacion.canal}</span>
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{notificacion.motivo || 'N/A'}</td>
                    <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">{notificacion.asunto}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        notificacion.enviada ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {notificacion.enviada ? 'Enviada' : 'Pendiente'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {notificacion.fechaCreacion 
                        ? new Date(notificacion.fechaCreacion).toLocaleDateString('es-ES', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit'
                          })
                        : notificacion.fechaEnvio 
                        ? new Date(notificacion.fechaEnvio).toLocaleDateString('es-ES', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit'
                          })
                        : 'N/A'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de formulario */}
      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title="Enviar Notificación"
        size="md"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm">
              {success}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Usuario Destinatario *
            </label>
            <select
              required
              value={formData.idUsuario}
              onChange={(e) => setFormData({ ...formData, idUsuario: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Seleccione un usuario</option>
              {usuarios.map((u) => (
                <option key={u.idUsuario} value={u.idUsuario}>
                  {u.username} - {u.email} ({u.rol})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Canal *
            </label>
            <select
              required
              value={formData.canal}
              onChange={(e) => setFormData({ ...formData, canal: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              {CANALES.map((canal) => (
                <option key={canal} value={canal}>
                  {canal}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo *
            </label>
            <input
              type="text"
              required
              value={formData.motivo}
              onChange={(e) => setFormData({ ...formData, motivo: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ej: Recordatorio de cita, Alerta de stock, etc."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Mensaje *
            </label>
            <textarea
              required
              value={formData.mensaje}
              onChange={(e) => setFormData({ ...formData, mensaje: e.target.value })}
              rows="5"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Escriba el mensaje de la notificación"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={handleCloseModal}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              Enviar
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

