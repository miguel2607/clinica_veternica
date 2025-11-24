import { useEffect, useState } from 'react';
import { horarioService, veterinarioService } from '../../services/api';
import { Plus, Edit, Trash2, Search, Clock } from 'lucide-react';
import Modal from '../../components/Modal';

const DIAS_SEMANA = [
  { value: 'MONDAY', label: 'Lunes' },
  { value: 'TUESDAY', label: 'Martes' },
  { value: 'WEDNESDAY', label: 'Miércoles' },
  { value: 'THURSDAY', label: 'Jueves' },
  { value: 'FRIDAY', label: 'Viernes' },
  { value: 'SATURDAY', label: 'Sábado' },
  { value: 'SUNDAY', label: 'Domingo' },
];

export default function HorariosPage() {
  const [horarios, setHorarios] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingHorario, setEditingHorario] = useState(null);
  const [formData, setFormData] = useState({
    idVeterinario: '',
    diaSemana: 'MONDAY',
    horaInicio: '',
    horaFin: '',
    activo: true,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [horariosRes, veterinariosRes] = await Promise.all([
        horarioService.getAll(),
        veterinarioService.getAll(),
      ]);
      setHorarios(horariosRes.data || []);
      const veterinariosData = veterinariosRes.data || [];
      setVeterinarios(veterinariosData);
      console.log('Veterinarios cargados:', veterinariosData); // Debug
      setError('');
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (horario = null) => {
    if (horario) {
      setEditingHorario(horario);
      // Convertir hora de formato "HH:mm:ss" o "HH:mm" a "HH:mm" para el input type="time"
      const formatHora = (hora) => {
        if (!hora) return '';
        // Si viene como "HH:mm:ss", tomar solo "HH:mm"
        if (typeof hora === 'string' && hora.includes(':')) {
          return hora.substring(0, 5); // "HH:mm"
        }
        return hora;
      };
      
      setFormData({
        idVeterinario: horario.veterinario?.idVeterinario || horario.idVeterinario ? String(horario.veterinario?.idVeterinario || horario.idVeterinario) : '',
        diaSemana: horario.diaSemana || 'MONDAY',
        horaInicio: formatHora(horario.horaInicio),
        horaFin: formatHora(horario.horaFin),
        activo: horario.activo !== undefined ? horario.activo : true,
      });
    } else {
      setEditingHorario(null);
      setFormData({
        idVeterinario: '',
        diaSemana: 'MONDAY',
        horaInicio: '',
        horaFin: '',
        activo: true,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingHorario(null);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      // Validaciones
      if (!formData.idVeterinario) {
        setError('Debe seleccionar un veterinario');
        return;
      }

      if (!formData.horaInicio || !formData.horaFin) {
        setError('Las horas de inicio y fin son obligatorias');
        return;
      }

      // Validar que el ID del veterinario sea un número válido
      const idVeterinario = parseInt(formData.idVeterinario);
      if (isNaN(idVeterinario) || idVeterinario <= 0) {
        setError('Debe seleccionar un veterinario válido');
        return;
      }

      // Convertir el formato de hora si es necesario (asegurar formato HH:mm:00 para LocalTime)
      const horaInicio = formData.horaInicio.includes(':') 
        ? (formData.horaInicio.split(':').length === 2 ? `${formData.horaInicio}:00` : formData.horaInicio)
        : `${formData.horaInicio}:00:00`;
      const horaFin = formData.horaFin.includes(':')
        ? (formData.horaFin.split(':').length === 2 ? `${formData.horaFin}:00` : formData.horaFin)
        : `${formData.horaFin}:00:00`;

      const data = {
        idVeterinario: Number(idVeterinario), // Asegurar que sea número (Long en el backend)
        diaSemana: formData.diaSemana, // Ya está en formato correcto (MONDAY, etc.)
        horaInicio: horaInicio,
        horaFin: horaFin,
        activo: formData.activo !== undefined ? formData.activo : true,
      };

      console.log('Enviando datos al backend:', JSON.stringify(data, null, 2)); // Debug

      if (editingHorario) {
        const response = await horarioService.update(editingHorario.idHorario, data);
        console.log('Respuesta del backend (update):', response.data);
        setSuccess('Horario actualizado exitosamente');
      } else {
        const response = await horarioService.create(data);
        console.log('Respuesta del backend (create):', response.data);
        setSuccess('Horario creado exitosamente');
      }
      await loadData();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al guardar horario:', error);
      console.error('Error completo:', JSON.stringify(error.response?.data, null, 2));
      
      let errorMessage = 'Error al guardar horario';
      
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

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de que desea eliminar este horario?')) return;
    try {
      setError('');
      await horarioService.delete(id);
      setSuccess('Horario eliminado exitosamente');
      await loadData();
    } catch (error) {
      console.error('Error al eliminar horario:', error);
      setError(error.response?.data?.message || 'Error al eliminar horario');
    }
  };

  const filteredHorarios = horarios.filter(
    (h) => 
      h.veterinario?.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      DIAS_SEMANA.find(d => d.value === h.diaSemana)?.label.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Horarios</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Horario
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
              placeholder="Buscar horarios..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Día</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora Inicio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora Fin</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredHorarios.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron horarios
                  </td>
                </tr>
              ) : (
                filteredHorarios.map((horario) => (
                  <tr key={horario.idHorario} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {horario.veterinario?.nombreCompleto || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {DIAS_SEMANA.find(d => d.value === horario.diaSemana)?.label || horario.diaSemana}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{horario.horaInicio || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{horario.horaFin || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        horario.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {horario.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleOpenModal(horario)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Editar"
                        >
                          <Edit className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => handleDelete(horario.idHorario)}
                          className="text-red-600 hover:text-red-900 transition-colors"
                          title="Eliminar"
                        >
                          <Trash2 className="w-5 h-5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title={editingHorario ? 'Editar Horario' : 'Nuevo Horario'}
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
              Veterinario *
            </label>
            <select
              required
              value={formData.idVeterinario}
              onChange={(e) => setFormData({ ...formData, idVeterinario: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Seleccione un veterinario</option>
              {veterinarios && veterinarios.length > 0 ? (
                veterinarios.map((v) => {
                  const id = v.idVeterinario || v.idPersonal;
                  return (
                    <option key={id} value={String(id)}>
                      {v.nombreCompleto || `${v.personal?.nombres || ''} ${v.personal?.apellidos || ''}`}
                    </option>
                  );
                })
              ) : (
                <option value="" disabled>No hay veterinarios disponibles</option>
              )}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Día de la Semana *
            </label>
            <select
              required
              value={formData.diaSemana}
              onChange={(e) => setFormData({ ...formData, diaSemana: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              {DIAS_SEMANA.map((dia) => (
                <option key={dia.value} value={dia.value}>
                  {dia.label}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora Inicio *
              </label>
              <input
                type="time"
                required
                value={formData.horaInicio}
                onChange={(e) => setFormData({ ...formData, horaInicio: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora Fin *
              </label>
              <input
                type="time"
                required
                value={formData.horaFin}
                onChange={(e) => setFormData({ ...formData, horaFin: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="activo"
              checked={formData.activo}
              onChange={(e) => setFormData({ ...formData, activo: e.target.checked })}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label htmlFor="activo" className="ml-2 block text-sm text-gray-700">
              Activo
            </label>
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
              {editingHorario ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

