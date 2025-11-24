import { useEffect, useState } from 'react';
import { citaService, mascotaService, veterinarioService, servicioService } from '../../services/api';
import { Plus, Edit, Trash2, Search, CheckCircle, XCircle, Clock, Eye } from 'lucide-react';
import Modal from '../../components/Modal';

export default function CitasPage() {
  const [citas, setCitas] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [actionModalOpen, setActionModalOpen] = useState(false);
  const [selectedCita, setSelectedCita] = useState(null);
  const [actionType, setActionType] = useState(''); // 'confirmar', 'cancelar', 'atender'
  const [cancelMotivo, setCancelMotivo] = useState('');
  const [editingCita, setEditingCita] = useState(null);
  const [formData, setFormData] = useState({
    idMascota: '',
    idVeterinario: '',
    idServicio: '',
    fechaCita: '',
    horaCita: '',
    motivo: '',
    observaciones: '',
    esEmergencia: false,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [citasRes, mascotasRes, veterinariosRes, serviciosRes] = await Promise.all([
        citaService.getAll(),
        mascotaService.getAll(),
        veterinarioService.getAll(),
        servicioService.getAll(),
      ]);
      setCitas(citasRes.data);
      setMascotas(mascotasRes.data);
      setVeterinarios(veterinariosRes.data);
      setServicios(serviciosRes.data);
      console.log('Veterinarios cargados:', veterinariosRes.data); // Debug
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (cita = null) => {
    if (cita) {
      setEditingCita(cita);
      // Formatear fecha: convertir de formato "dd/MM/yyyy" o "yyyy-MM-dd" a "yyyy-MM-dd"
      let fechaFormateada = cita.fechaCita || '';
      if (fechaFormateada && fechaFormateada.includes('/')) {
        const [dia, mes, anio] = fechaFormateada.split('/');
        fechaFormateada = `${anio}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
      }

      // Formatear hora: convertir de "HH:mm:ss" a "HH:mm" para el input type="time"
      let horaFormateada = cita.horaCita || '';
      if (horaFormateada && horaFormateada.includes(':')) {
        horaFormateada = horaFormateada.substring(0, 5); // Tomar solo HH:mm
      }

      setFormData({
        idMascota: cita.mascota?.idMascota ? String(cita.mascota.idMascota) : '',
        idVeterinario: cita.veterinario?.idPersonal || cita.veterinario?.idVeterinario ? String(cita.veterinario.idPersonal || cita.veterinario.idVeterinario) : '',
        idServicio: cita.servicio?.idServicio ? String(cita.servicio.idServicio) : '',
        fechaCita: fechaFormateada,
        horaCita: horaFormateada,
        motivo: cita.motivoConsulta || cita.motivo || '',
        observaciones: cita.observaciones || '',
        esEmergencia: cita.esEmergencia || false,
      });
    } else {
      setEditingCita(null);
      setFormData({
        idMascota: '',
        idVeterinario: '',
        idServicio: '',
        fechaCita: '',
        horaCita: '',
        motivo: '',
        observaciones: '',
        esEmergencia: false,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingCita(null);
    setError('');
    setSuccess('');
  };

  const handleViewCita = (cita) => {
    setSelectedCita(cita);
    setViewModalOpen(true);
  };

  const handleAction = (cita, type) => {
    setSelectedCita(cita);
    setActionType(type);
    setCancelMotivo('');
    setActionModalOpen(true);
    setError('');
  };

  const handleConfirmAction = async () => {
    try {
      setError('');
      setSuccess('');

      switch (actionType) {
        case 'confirmar':
          await citaService.confirmar(selectedCita.idCita);
          setSuccess('Cita confirmada exitosamente');
          break;
        case 'cancelar':
          if (!cancelMotivo.trim()) {
            setError('El motivo de cancelación es obligatorio');
            return;
          }
          await citaService.cancelar(selectedCita.idCita, cancelMotivo);
          setSuccess('Cita cancelada exitosamente');
          break;
        case 'atender':
          await citaService.atender(selectedCita.idCita);
          setSuccess('Cita marcada como atendida');
          break;
        default:
          break;
      }

      await loadData();
      setTimeout(() => {
        setActionModalOpen(false);
        setSelectedCita(null);
        setActionType('');
      }, 1000);
    } catch (error) {
      console.error('Error al ejecutar acción:', error);
      setError(error.response?.data?.message || 'Error al ejecutar la acción');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      console.log('FormData antes de validar:', formData); // Debug
      
      // Validaciones
      if (!formData.idMascota || formData.idMascota === '' || formData.idMascota === '0') {
        setError('Debe seleccionar una mascota');
        return;
      }

      if (!formData.idVeterinario || formData.idVeterinario === '' || formData.idVeterinario === '0') {
        setError('Debe seleccionar un veterinario');
        return;
      }

      if (!formData.idServicio || formData.idServicio === '' || formData.idServicio === '0') {
        setError('Debe seleccionar un servicio');
        return;
      }

      if (!formData.fechaCita) {
        setError('La fecha es obligatoria');
        return;
      }

      // Validar que la fecha no sea en el pasado
      const fechaSeleccionada = new Date(formData.fechaCita);
      const hoy = new Date();
      hoy.setHours(0, 0, 0, 0); // Resetear horas para comparar solo fechas
      fechaSeleccionada.setHours(0, 0, 0, 0);
      
      if (fechaSeleccionada < hoy) {
        setError('La fecha de la cita no puede ser en el pasado');
        return;
      }

      if (!formData.horaCita) {
        setError('La hora es obligatoria');
        return;
      }

      if (!formData.motivo || formData.motivo.trim().length < 5) {
        setError('El motivo de consulta debe tener al menos 5 caracteres');
        return;
      }

      // Formatear hora: convertir "HH:mm" a "HH:mm:00" si es necesario
      let horaFormateada = formData.horaCita;
      if (horaFormateada && horaFormateada.length === 5) {
        horaFormateada = horaFormateada + ':00';
      }

      // Validar que los IDs sean números válidos
      const idMascota = parseInt(formData.idMascota, 10);
      const idVeterinario = parseInt(formData.idVeterinario, 10);
      const idServicio = parseInt(formData.idServicio, 10);

      if (isNaN(idMascota) || idMascota <= 0) {
        setError('El ID de la mascota no es válido');
        return;
      }

      if (isNaN(idVeterinario) || idVeterinario <= 0) {
        setError('El ID del veterinario no es válido');
        return;
      }

      if (isNaN(idServicio) || idServicio <= 0) {
        setError('El ID del servicio no es válido');
        return;
      }

      // Preparar datos según el formato que espera el backend
      // Los IDs deben ser números (Long en el backend)
      const data = {
        idMascota: Number(idMascota),
        idVeterinario: Number(idVeterinario),
        idServicio: Number(idServicio),
        fechaCita: formData.fechaCita, // Ya viene en formato YYYY-MM-DD del input type="date"
        horaCita: horaFormateada, // Formato HH:mm:ss
        motivo: formData.motivo.trim(),
        esEmergencia: formData.esEmergencia || false,
      };

      // Solo incluir observaciones si tiene valor
      if (formData.observaciones && formData.observaciones.trim()) {
        data.observaciones = formData.observaciones.trim();
      }

      console.log('Enviando datos al backend:', JSON.stringify(data, null, 2)); // Debug mejorado

      if (editingCita) {
        const response = await citaService.update(editingCita.idCita, data);
        console.log('Respuesta del backend (update):', response.data);
        setSuccess('Cita actualizada exitosamente');
      } else {
        const response = await citaService.create(data);
        console.log('Respuesta del backend (create):', response.data);
        setSuccess('Cita creada exitosamente');
      }

      await loadData();
      setTimeout(() => {
        handleCloseModal();
      }, 1000);
    } catch (error) {
      console.error('Error al guardar cita:', error);
      console.error('Error completo:', JSON.stringify(error.response?.data, null, 2));
      
      let errorMessage = 'Error al guardar cita';
      
      if (error.response?.data) {
        const errorData = error.response.data;
        
        // Si hay validationErrors (Map de errores por campo)
        if (errorData.validationErrors && typeof errorData.validationErrors === 'object') {
          const validationMessages = Object.entries(errorData.validationErrors)
            .map(([field, msg]) => `${field}: ${msg}`)
            .join(', ');
          errorMessage = errorData.message || 'Error de validación';
          if (validationMessages) {
            errorMessage += ' - ' + validationMessages;
          }
        }
        // Si hay errors (Lista de strings)
        else if (Array.isArray(errorData.errors) && errorData.errors.length > 0) {
          errorMessage = errorData.message || 'Error de validación';
          errorMessage += ' - ' + errorData.errors.join(', ');
        }
        // Mensaje directo
        else if (errorData.message) {
          errorMessage = errorData.message;
        } else if (errorData.error) {
          errorMessage = errorData.error;
        } else if (typeof errorData === 'string') {
          errorMessage = errorData;
        } else {
          errorMessage = JSON.stringify(errorData);
        }
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      setError(errorMessage);
    }
  };

  const filteredCitas = citas.filter(
    (c) =>
      c.mascota?.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.veterinario?.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.motivoConsulta?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getEstadoColor = (estado) => {
    const colors = {
      PROGRAMADA: 'bg-blue-100 text-blue-800',
      CONFIRMADA: 'bg-green-100 text-green-800',
      ATENDIDA: 'bg-gray-100 text-gray-800',
      CANCELADA: 'bg-red-100 text-red-800',
      EN_ATENCION: 'bg-yellow-100 text-yellow-800',
    };
    return colors[estado] || 'bg-gray-100 text-gray-800';
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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Citas</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Cita
        </button>
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
              placeholder="Buscar citas..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Servicio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredCitas.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron citas
                  </td>
                </tr>
              ) : (
                filteredCitas.map((cita) => (
                  <tr key={cita.idCita} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{cita.mascota?.nombre}</div>
                      <div className="text-sm text-gray-500">{cita.mascota?.propietario?.nombreCompleto}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.veterinario?.nombreCompleto || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.servicio?.nombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.fechaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.horaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(cita.estado)}`}>
                        {cita.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleViewCita(cita)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Ver detalles"
                        >
                          <Eye className="w-5 h-5" />
                        </button>
                        {cita.estado === 'PROGRAMADA' && (
                          <>
                            <button
                              onClick={() => handleAction(cita, 'confirmar')}
                              className="text-green-600 hover:text-green-900 transition-colors"
                              title="Confirmar"
                            >
                              <CheckCircle className="w-5 h-5" />
                            </button>
                            <button
                              onClick={() => handleAction(cita, 'cancelar')}
                              className="text-red-600 hover:text-red-900 transition-colors"
                              title="Cancelar"
                            >
                              <XCircle className="w-5 h-5" />
                            </button>
                          </>
                        )}
                        {(cita.estado === 'CONFIRMADA' || cita.estado === 'PROGRAMADA') && (
                          <button
                            onClick={() => handleAction(cita, 'atender')}
                            className="text-blue-600 hover:text-blue-900 transition-colors"
                            title="Atender"
                          >
                            <Clock className="w-5 h-5" />
                          </button>
                        )}
                      </div>
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
        title={editingCita ? 'Editar Cita' : 'Nueva Cita'}
        size="lg"
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

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mascota *
              </label>
              <select
                required
                value={formData.idMascota}
                onChange={(e) => setFormData({ ...formData, idMascota: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione una mascota</option>
                {mascotas && mascotas.map((m) => (
                  <option key={m.idMascota} value={String(m.idMascota)}>
                    {m.nombre} - {m.propietario?.nombreCompleto}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Veterinario *
              </label>
              <select
                required
                value={formData.idVeterinario}
                onChange={(e) => {
                  const selectedValue = e.target.value;
                  console.log('Veterinario seleccionado:', selectedValue, 'Tipo:', typeof selectedValue);
                  setFormData({ ...formData, idVeterinario: selectedValue });
                }}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione un veterinario</option>
                {veterinarios && veterinarios.map((v) => {
                  const id = v.idPersonal || v.idVeterinario; // El backend usa idPersonal
                  return (
                    <option key={id} value={String(id)}>
                      {v.nombreCompleto || `${v.nombres || ''} ${v.apellidos || ''}`.trim()}
                    </option>
                  );
                })}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Servicio *
              </label>
              <select
                required
                value={formData.idServicio}
                onChange={(e) => setFormData({ ...formData, idServicio: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione un servicio</option>
                {servicios && servicios.map((s) => (
                  <option key={s.idServicio} value={String(s.idServicio)}>
                    {s.nombre} - ${s.precio}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha *
              </label>
              <input
                type="date"
                required
                value={formData.fechaCita}
                onChange={(e) => setFormData({ ...formData, fechaCita: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora *
              </label>
              <input
                type="time"
                required
                value={formData.horaCita}
                onChange={(e) => setFormData({ ...formData, horaCita: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div className="flex items-center">
              <input
                type="checkbox"
                id="esEmergencia"
                checked={formData.esEmergencia}
                onChange={(e) => setFormData({ ...formData, esEmergencia: e.target.checked })}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label htmlFor="esEmergencia" className="ml-2 block text-sm text-gray-700">
                Es emergencia
              </label>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo de Consulta *
            </label>
            <textarea
              required
              value={formData.motivo}
              onChange={(e) => setFormData({ ...formData, motivo: e.target.value })}
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Describa el motivo de la consulta"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Observaciones
            </label>
            <textarea
              value={formData.observaciones}
              onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
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
              {editingCita ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal de ver detalles */}
      <Modal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        title="Detalles de la Cita"
        size="md"
      >
        {selectedCita && (
          <div className="space-y-4">
            <div>
              <h3 className="text-sm font-medium text-gray-500">Mascota</h3>
              <p className="text-lg font-semibold text-gray-900">{selectedCita.mascota?.nombre}</p>
              <p className="text-sm text-gray-600">Propietario: {selectedCita.mascota?.propietario?.nombreCompleto}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Veterinario</h3>
              <p className="text-lg text-gray-900">{selectedCita.veterinario?.nombreCompleto || 'N/A'}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Servicio</h3>
              <p className="text-lg text-gray-900">{selectedCita.servicio?.nombre || 'N/A'}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Fecha y Hora</h3>
              <p className="text-lg text-gray-900">{selectedCita.fechaCita} a las {selectedCita.horaCita}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Estado</h3>
              <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(selectedCita.estado)}`}>
                {selectedCita.estado}
              </span>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Motivo</h3>
              <p className="text-gray-900">{selectedCita.motivoConsulta || 'N/A'}</p>
            </div>
            {selectedCita.observaciones && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Observaciones</h3>
                <p className="text-gray-900">{selectedCita.observaciones}</p>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Modal de acciones */}
      <Modal
        isOpen={actionModalOpen}
        onClose={() => {
          setActionModalOpen(false);
          setSelectedCita(null);
          setActionType('');
          setCancelMotivo('');
        }}
        title={
          actionType === 'confirmar' ? 'Confirmar Cita' :
          actionType === 'cancelar' ? 'Cancelar Cita' :
          actionType === 'atender' ? 'Atender Cita' : 'Acción'
        }
        size="md"
      >
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm mb-4">
            {error}
          </div>
        )}

        {success && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm mb-4">
            {success}
          </div>
        )}

        {actionType === 'cancelar' && (
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo de Cancelación *
            </label>
            <textarea
              required
              value={cancelMotivo}
              onChange={(e) => setCancelMotivo(e.target.value)}
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ingrese el motivo de cancelación"
            />
          </div>
        )}

        {actionType === 'confirmar' && (
          <p className="mb-4 text-gray-700">
            ¿Está seguro de que desea confirmar esta cita?
          </p>
        )}

        {actionType === 'atender' && (
          <p className="mb-4 text-gray-700">
            ¿Está seguro de que desea marcar esta cita como atendida?
          </p>
        )}

        <div className="flex justify-end space-x-3 pt-4 border-t">
          <button
            type="button"
            onClick={() => {
              setActionModalOpen(false);
              setSelectedCita(null);
              setActionType('');
              setCancelMotivo('');
            }}
            className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={handleConfirmAction}
            className={`px-4 py-2 rounded-lg text-white transition-colors ${
              actionType === 'cancelar' ? 'bg-red-600 hover:bg-red-700' :
              actionType === 'confirmar' ? 'bg-green-600 hover:bg-green-700' :
              'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {actionType === 'confirmar' ? 'Confirmar' :
             actionType === 'cancelar' ? 'Cancelar' :
             'Atender'}
          </button>
        </div>
      </Modal>
    </div>
  );
}
