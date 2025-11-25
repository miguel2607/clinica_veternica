import { useEffect, useState } from 'react';
import { tipoInsumoService } from '../../services/api';
import { Plus, Edit, Trash2, Search, Package } from 'lucide-react';
import Modal from '../../components/Modal';

export default function TiposInsumoPage() {
  const [tiposInsumo, setTiposInsumo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTipo, setEditingTipo] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    categoria: '',
    activo: true,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadTiposInsumo();
  }, []);

  const loadTiposInsumo = async () => {
    try {
      setLoading(true);
      const response = await tipoInsumoService.getAll();
      setTiposInsumo(response.data || []);
      setError('');
    } catch (error) {
      console.error('Error al cargar tipos de insumo:', error);
      setError('Error al cargar tipos de insumo');
      setTiposInsumo([]);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (tipo = null) => {
    if (tipo) {
      setEditingTipo(tipo);
      setFormData({
        nombre: tipo.nombre || '',
        descripcion: tipo.descripcion || '',
        categoria: tipo.categoria || '',
        activo: tipo.activo !== undefined ? tipo.activo : true,
      });
    } else {
      setEditingTipo(null);
      setFormData({
        nombre: '',
        descripcion: '',
        categoria: '',
        activo: true,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingTipo(null);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      if (editingTipo) {
        await tipoInsumoService.update(editingTipo.idTipoInsumo, formData);
        setSuccess('Tipo de insumo actualizado exitosamente');
      } else {
        await tipoInsumoService.create(formData);
        setSuccess('Tipo de insumo creado exitosamente');
      }
      await loadTiposInsumo();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al guardar tipo de insumo:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al guardar tipo de insumo';
      setError(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de que desea eliminar este tipo de insumo?')) return;
    try {
      setError('');
      await tipoInsumoService.delete(id);
      setSuccess('Tipo de insumo eliminado exitosamente');
      await loadTiposInsumo();
    } catch (error) {
      console.error('Error al eliminar tipo de insumo:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al eliminar tipo de insumo';
      setError(errorMessage);
    }
  };

  const filteredTipos = tiposInsumo.filter(
    (t) => 
      t.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.descripcion?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.categoria?.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Tipos de Insumo</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Tipo de Insumo
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
              placeholder="Buscar tipos de insumo..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Descripción</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Categoría</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredTipos.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-gray-500">
                    {tiposInsumo.length === 0 
                      ? 'No hay tipos de insumo registrados. Cree uno nuevo para comenzar.'
                      : 'No se encontraron tipos de insumo'}
                  </td>
                </tr>
              ) : (
                filteredTipos.map((tipo) => (
                  <tr key={tipo.idTipoInsumo} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{tipo.nombre}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{tipo.descripcion || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{tipo.categoria || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        tipo.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {tipo.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleOpenModal(tipo)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Editar"
                        >
                          <Edit className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => handleDelete(tipo.idTipoInsumo)}
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
        title={editingTipo ? 'Editar Tipo de Insumo' : 'Nuevo Tipo de Insumo'}
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
              Nombre *
            </label>
            <input
              type="text"
              required
              value={formData.nombre}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ej: Medicamentos, Vacunas, Materiales"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              value={formData.descripcion}
              onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Descripción del tipo de insumo"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Categoría
            </label>
            <input
              type="text"
              value={formData.categoria}
              onChange={(e) => setFormData({ ...formData, categoria: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ej: MEDICAMENTO, MATERIAL_QUIRURGICO, ALIMENTO"
            />
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
              {editingTipo ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

