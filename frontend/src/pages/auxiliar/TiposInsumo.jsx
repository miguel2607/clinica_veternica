import { useEffect, useState } from 'react';
import { tipoInsumoService } from '../../services/api';

export default function TiposInsumoPage() {
  const [tiposInsumo, setTiposInsumo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTipo, setEditingTipo] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    activo: true
  });

  useEffect(() => {
    loadTiposInsumo();
  }, []);

  const loadTiposInsumo = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await tipoInsumoService.getAll();
      console.log('✅ Tipos de insumo cargados:', response.data);
      setTiposInsumo(response.data || []);
    } catch (error) {
      console.error('❌ Error al cargar tipos de insumo:', error);
      setError(`Error al cargar tipos de insumo: ${error.response?.data?.message || error.message}`);
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
        activo: tipo.activo !== undefined ? tipo.activo : true
      });
    } else {
      setEditingTipo(null);
      setFormData({
        nombre: '',
        descripcion: '',
        activo: true
      });
    }
    setModalOpen(true);
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
      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar tipo de insumo:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este tipo de insumo? Esta acción no se puede deshacer.')) {
      return;
    }

    try {
      await tipoInsumoService.delete(id);
      setSuccess('Tipo de insumo eliminado exitosamente');
      await loadTiposInsumo();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error al eliminar:', error);
      setError(`Error al eliminar: ${error.response?.data?.message || error.message}`);
      setTimeout(() => setError(''), 5000);
    }
  };

  const tiposFiltrados = tiposInsumo.filter(tipo =>
    tipo.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    tipo.descripcion?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return <div className="text-center py-8">Cargando tipos de insumo...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Tipos de Insumo</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors inline-flex items-center gap-2"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nuevo Tipo
        </button>
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

      {/* Buscador */}
      <div className="bg-white rounded-lg shadow p-4">
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Buscar tipo de insumo..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
        />
      </div>

      {/* Tabla de tipos de insumo */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {tiposFiltrados.map((tipo) => (
                <tr key={tipo.idTipoInsumo} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{tipo.idTipoInsumo}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {tipo.nombre}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {tipo.descripcion || 'Sin descripción'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                      tipo.activo
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {tipo.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleOpenModal(tipo)}
                        className="text-indigo-600 hover:text-indigo-900"
                        title="Editar"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleDelete(tipo.idTipoInsumo)}
                        className="text-red-600 hover:text-red-900"
                        title="Eliminar"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {tiposFiltrados.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            {searchTerm ? 'No se encontraron tipos de insumo con ese criterio' : 'No hay tipos de insumo registrados'}
          </div>
        )}
      </div>

      {/* Vista de tarjetas (alternativa) */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {tiposFiltrados.map((tipo) => (
          <div key={tipo.idTipoInsumo} className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-start mb-3">
              <div className="flex-1">
                <h3 className="font-semibold text-lg text-gray-900">{tipo.nombre}</h3>
                <p className="text-xs text-gray-500">ID: #{tipo.idTipoInsumo}</p>
              </div>
              <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                tipo.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              }`}>
                {tipo.activo ? 'Activo' : 'Inactivo'}
              </span>
            </div>

            <p className="text-sm text-gray-600 mb-4">
              {tipo.descripcion || 'Sin descripción'}
            </p>

            <div className="flex gap-2">
              <button
                onClick={() => handleOpenModal(tipo)}
                className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 text-sm"
              >
                Editar
              </button>
              <button
                onClick={() => handleDelete(tipo.idTipoInsumo)}
                className="flex-1 border border-red-600 text-red-600 py-2 rounded-lg hover:bg-red-50 text-sm"
              >
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Modal de crear/editar */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">
                {editingTipo ? 'Editar Tipo de Insumo' : 'Nuevo Tipo de Insumo'}
              </h3>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.nombre}
                    onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                    placeholder="ej: Medicamentos, Material Quirúrgico, Alimentos..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Descripción
                  </label>
                  <textarea
                    value={formData.descripcion}
                    onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                    rows="3"
                    placeholder="Descripción del tipo de insumo..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div className="flex items-center">
                  <input
                    type="checkbox"
                    id="activo"
                    checked={formData.activo}
                    onChange={(e) => setFormData({...formData, activo: e.target.checked})}
                    className="rounded"
                  />
                  <label htmlFor="activo" className="ml-2 text-sm text-gray-700">
                    Activo
                  </label>
                </div>

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

                <div className="flex justify-end gap-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => setModalOpen(false)}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                  >
                    {editingTipo ? 'Actualizar' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
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
            <h4 className="font-semibold text-blue-900 mb-1">Información sobre tipos de insumo</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Los tipos de insumo se usan para categorizar y organizar el inventario</li>
              <li>• Ejemplos: Medicamentos, Material Quirúrgico, Alimentos, Higiene, etc.</li>
              <li>• Antes de eliminar un tipo, asegúrese de que no esté siendo usado por algún insumo</li>
              <li>• Los tipos inactivos no aparecerán al crear nuevos insumos</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
