import { useEffect, useState } from 'react';
import { servicioService } from '../../services/api';
import { Plus, Edit, Trash2, Search, Scissors } from 'lucide-react';
import Modal from '../../components/Modal';

const TIPOS_SERVICIO = [
  { value: 'CONSULTA_GENERAL', label: 'Consulta General' },
  { value: 'VACUNACION', label: 'Vacunación' },
  { value: 'DESPARASITACION', label: 'Desparasitación' },
  { value: 'CIRUGIA', label: 'Cirugía' },
  { value: 'BANO', label: 'Baño' },
  { value: 'PELUQUERIA', label: 'Peluquería' },
  { value: 'CONTROL_SALUD', label: 'Control de Salud' },
  { value: 'EXAMEN_LABORATORIO', label: 'Examen de Laboratorio' },
  { value: 'RADIOGRAFIA', label: 'Radiografía' },
  { value: 'ECOGRAFIA', label: 'Ecografía' },
  { value: 'ESTERILIZACION', label: 'Esterilización' },
  { value: 'LIMPIEZA_DENTAL', label: 'Limpieza Dental' },
  { value: 'HOSPITALIZACION', label: 'Hospitalización' },
  { value: 'CONSULTA', label: 'Consulta' },
  { value: 'EMERGENCIA', label: 'Emergencia' },
];

const CATEGORIAS_SERVICIO = [
  { value: 'CLINICO', label: 'Clínico' },
  { value: 'QUIRURGICO', label: 'Quirúrgico' },
  { value: 'ESTETICO', label: 'Estético' },
  { value: 'EMERGENCIA', label: 'Emergencia' },
];

export default function ServiciosPage() {
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingServicio, setEditingServicio] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    tipoServicio: 'CONSULTA_GENERAL',
    categoria: 'CLINICO',
    precio: '',
    duracionMinutos: '',
    requierePreparacion: false,
    instruccionesPreparacion: '',
    activo: true,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadServicios();
  }, []);

  const loadServicios = async () => {
    try {
      setLoading(true);
      const response = await servicioService.getAll();
      setServicios(response.data || []);
      setError('');
    } catch (error) {
      console.error('Error al cargar servicios:', error);
      setError('Error al cargar servicios');
      setServicios([]);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (servicio = null) => {
    if (servicio) {
      setEditingServicio(servicio);
      // Asegurar que el precio se muestre correctamente (puede venir como número o string)
      const precio = servicio.precio ? (typeof servicio.precio === 'number' ? servicio.precio.toString() : servicio.precio) : '';
      setFormData({
        nombre: servicio.nombre || '',
        descripcion: servicio.descripcion || '',
        tipoServicio: servicio.tipoServicio || 'CONSULTA_GENERAL',
        categoria: servicio.categoria || 'CLINICO',
        precio: precio,
        duracionMinutos: servicio.duracionEstimadaMinutos || servicio.duracionMinutos || '',
        requierePreparacion: servicio.requierePreparacion || false,
        instruccionesPreparacion: servicio.instruccionesPreparacion || '',
        activo: servicio.activo !== undefined ? servicio.activo : true,
      });
    } else {
      setEditingServicio(null);
      setFormData({
        nombre: '',
        descripcion: '',
        tipoServicio: 'CONSULTA_GENERAL',
        categoria: 'CLINICO',
        precio: '',
        duracionMinutos: '',
        requierePreparacion: false,
        instruccionesPreparacion: '',
        activo: true,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingServicio(null);
    setError('');
    setSuccess('');
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      // Validaciones
      if (!formData.nombre || formData.nombre.trim().length < 3) {
        setError('El nombre debe tener al menos 3 caracteres');
        return;
      }

      const precio = parseFloat(formData.precio);
      if (isNaN(precio) || precio <= 0) {
        setError('El precio debe ser mayor a 0');
        return;
      }

      const duracion = parseInt(formData.duracionMinutos);
      if (isNaN(duracion) || duracion < 1) {
        setError('La duración debe ser al menos 1 minuto');
        return;
      }

      // Preparar datos según el formato que espera el backend
      const data = {
        nombre: formData.nombre.trim(),
        descripcion: formData.descripcion?.trim() || null,
        tipoServicio: formData.tipoServicio,
        categoria: formData.categoria,
        precio: precio,
        duracionMinutos: duracion,
        requierePreparacion: formData.requierePreparacion || false,
        instruccionesPreparacion: formData.instruccionesPreparacion?.trim() || null,
        activo: formData.activo !== undefined ? formData.activo : true,
      };

      console.log('Enviando datos al backend:', data); // Debug

      if (editingServicio) {
        await servicioService.update(editingServicio.idServicio, data);
        setSuccess('Servicio actualizado exitosamente');
      } else {
        await servicioService.create(data);
        setSuccess('Servicio creado exitosamente');
      }
      await loadServicios();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al guardar servicio:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          error.message || 
                          'Error al guardar servicio';
      setError(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de que desea eliminar este servicio?')) return;
    try {
      setError('');
      await servicioService.delete(id);
      setSuccess('Servicio eliminado exitosamente');
      await loadServicios();
    } catch (error) {
      console.error('Error al eliminar servicio:', error);
      setError(error.response?.data?.message || 'Error al eliminar servicio');
    }
  };

  const filteredServicios = servicios.filter(
    (s) =>
      s.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.tipoServicio?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.categoria?.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Servicios</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Servicio
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
              placeholder="Buscar servicios..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Categoría</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Precio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Duración</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredServicios.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron servicios
                  </td>
                </tr>
              ) : (
                filteredServicios.map((servicio) => (
                  <tr key={servicio.idServicio} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{servicio.nombre || 'N/A'}</div>
                      {servicio.descripcion && (
                        <div className="text-xs text-gray-500 mt-1 truncate max-w-xs">{servicio.descripcion}</div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {TIPOS_SERVICIO.find(t => t.value === servicio.tipoServicio)?.label || servicio.tipoServicio || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {CATEGORIAS_SERVICIO.find(c => c.value === servicio.categoria)?.label || servicio.categoria || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      ${servicio.precio ? parseFloat(servicio.precio).toLocaleString('es-CO') : '0'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {servicio.duracionEstimadaMinutos || servicio.duracionMinutos || 0} min
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        servicio.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {servicio.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleOpenModal(servicio)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Editar"
                        >
                          <Edit className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => handleDelete(servicio.idServicio)}
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
        title={editingServicio ? 'Editar Servicio' : 'Nuevo Servicio'}
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

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nombre del Servicio *
            </label>
            <input
              type="text"
              required
              value={formData.nombre}
              onChange={handleChange}
              name="nombre"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ej: Consulta General"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              value={formData.descripcion}
              onChange={handleChange}
              name="descripcion"
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Descripción detallada del servicio..."
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tipo de Servicio *
              </label>
              <select
                required
                value={formData.tipoServicio}
                onChange={handleChange}
                name="tipoServicio"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                {TIPOS_SERVICIO.map((tipo) => (
                  <option key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Categoría *
              </label>
              <select
                required
                value={formData.categoria}
                onChange={handleChange}
                name="categoria"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                {CATEGORIAS_SERVICIO.map((cat) => (
                  <option key={cat.value} value={cat.value}>
                    {cat.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Precio *
              </label>
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={formData.precio}
                onChange={handleChange}
                name="precio"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="0.00"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Duración (minutos) *
              </label>
              <input
                type="number"
                required
                min="1"
                value={formData.duracionMinutos}
                onChange={handleChange}
                name="duracionMinutos"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="30"
              />
            </div>
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="requierePreparacion"
              name="requierePreparacion"
              checked={formData.requierePreparacion}
              onChange={handleChange}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label htmlFor="requierePreparacion" className="ml-2 block text-sm text-gray-700">
              Requiere preparación previa
            </label>
          </div>

          {formData.requierePreparacion && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Instrucciones de Preparación
              </label>
              <textarea
                value={formData.instruccionesPreparacion}
                onChange={handleChange}
                name="instruccionesPreparacion"
                rows="3"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ej: Ayuno de 12 horas, no dar comida..."
              />
            </div>
          )}

          <div className="flex items-center">
            <input
              type="checkbox"
              id="activo"
              name="activo"
              checked={formData.activo}
              onChange={handleChange}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label htmlFor="activo" className="ml-2 block text-sm text-gray-700">
              Servicio activo
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
              {editingServicio ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

