import { useEffect, useState } from 'react';
import { insumoService, tipoInsumoService } from '../../services/api';
import { Plus, Edit, Trash2, Search, Package, AlertTriangle } from 'lucide-react';
import Modal from '../../components/Modal';

const ESTADOS_INSUMO = ['DISPONIBLE', 'AGOTADO', 'EN_PEDIDO'];

export default function InsumosPage() {
  const [insumos, setInsumos] = useState([]);
  const [tiposInsumo, setTiposInsumo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingInsumo, setEditingInsumo] = useState(null);
  const [formData, setFormData] = useState({
    codigo: '',
    nombre: '',
    descripcion: '',
    idTipoInsumo: '',
    unidadMedida: '',
    cantidadStock: '',
    stockMinimo: '',
    stockMaximo: '',
    precioCompra: '',
    precioVenta: '',
    lote: '',
    fechaVencimiento: '',
    ubicacion: '',
    estado: 'DISPONIBLE',
    requiereRefrigeracion: false,
    requiereReceta: false,
    observaciones: '',
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
      const [insumosRes, tiposRes] = await Promise.all([
        insumoService.getAll(),
        tipoInsumoService.getAll(),
      ]);
      setInsumos(insumosRes.data || []);
      const tipos = tiposRes.data || [];
      setTiposInsumo(tipos);
      
      if (tipos.length === 0) {
        setError('No hay tipos de insumo disponibles. Por favor, cree al menos un tipo de insumo antes de crear insumos.');
      } else {
        setError('');
      }
    } catch (error) {
      console.error('Error al cargar datos:', error);
      const errorMessage = error.response?.data?.message || 'Error al cargar datos';
      setError(`Error al cargar datos: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (insumo = null) => {
    if (insumo) {
      setEditingInsumo(insumo);
      setFormData({
        codigo: insumo.codigo || '',
        nombre: insumo.nombre || '',
        descripcion: insumo.descripcion || '',
        idTipoInsumo: insumo.tipoInsumo?.idTipoInsumo || '',
        unidadMedida: insumo.unidadMedida || '',
        cantidadStock: insumo.cantidadStock || '',
        stockMinimo: insumo.stockMinimo || '',
        stockMaximo: insumo.stockMaximo || '',
        precioCompra: insumo.precioCompra || '',
        precioVenta: insumo.precioVenta || '',
        lote: insumo.lote || '',
        fechaVencimiento: insumo.fechaVencimiento || '',
        ubicacion: insumo.ubicacion || '',
        estado: insumo.estado || 'DISPONIBLE',
        requiereRefrigeracion: insumo.requiereRefrigeracion || false,
        requiereReceta: insumo.requiereReceta || false,
        observaciones: insumo.observaciones || '',
        activo: insumo.activo !== undefined ? insumo.activo : true,
      });
    } else {
      setEditingInsumo(null);
      setFormData({
        codigo: '',
        nombre: '',
        descripcion: '',
        idTipoInsumo: '',
        unidadMedida: '',
        cantidadStock: '',
        stockMinimo: '',
        stockMaximo: '',
        precioCompra: '',
        precioVenta: '',
        lote: '',
        fechaVencimiento: '',
        ubicacion: '',
        estado: 'DISPONIBLE',
        requiereRefrigeracion: false,
        requiereReceta: false,
        observaciones: '',
        activo: true,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingInsumo(null);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validar que haya un tipo de insumo seleccionado
    if (!formData.idTipoInsumo || formData.idTipoInsumo === '') {
      setError('Debe seleccionar un tipo de insumo');
      return;
    }

    // Validar que haya tipos de insumo disponibles
    if (tiposInsumo.length === 0) {
      setError('No hay tipos de insumo disponibles. Por favor, cree al menos un tipo de insumo primero.');
      return;
    }

    try {
      const data = {
        ...formData,
        idTipoInsumo: parseInt(formData.idTipoInsumo),
        cantidadStock: formData.cantidadStock ? parseInt(formData.cantidadStock) : 0,
        stockMinimo: formData.stockMinimo ? parseInt(formData.stockMinimo) : 0,
        stockMaximo: formData.stockMaximo ? parseInt(formData.stockMaximo) : null,
        precioCompra: formData.precioCompra ? parseFloat(formData.precioCompra) : null,
        precioVenta: formData.precioVenta ? parseFloat(formData.precioVenta) : null,
        fechaVencimiento: formData.fechaVencimiento || null,
      };

      if (editingInsumo) {
        await insumoService.update(editingInsumo.idInsumo, data);
        setSuccess('Insumo actualizado exitosamente');
      } else {
        await insumoService.create(data);
        setSuccess('Insumo creado exitosamente');
      }
      await loadData();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al guardar insumo:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al guardar insumo';
      setError(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de que desea eliminar este insumo?')) return;
    try {
      setError('');
      await insumoService.delete(id);
      setSuccess('Insumo eliminado exitosamente');
      await loadData();
    } catch (error) {
      console.error('Error al eliminar insumo:', error);
      setError(error.response?.data?.message || 'Error al eliminar insumo');
    }
  };

  const filteredInsumos = insumos.filter(
    (i) => 
      i.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      i.codigo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      i.tipoInsumo?.nombre?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'DISPONIBLE':
        return 'bg-green-100 text-green-800';
      case 'AGOTADO':
        return 'bg-red-100 text-red-800';
      case 'EN_PEDIDO':
        return 'bg-yellow-100 text-yellow-800';
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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Insumos</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Insumo
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
              placeholder="Buscar insumos..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Código</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredInsumos.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron insumos
                  </td>
                </tr>
              ) : (
                filteredInsumos.map((insumo) => (
                  <tr key={insumo.idInsumo} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{insumo.codigo}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{insumo.nombre}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{insumo.tipoInsumo?.nombre || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {insumo.cantidadStock} {insumo.unidadMedida}
                      {insumo.cantidadStock <= insumo.stockMinimo && (
                        <AlertTriangle className="inline w-4 h-4 text-yellow-600 ml-1" />
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(insumo.estado)}`}>
                        {insumo.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleOpenModal(insumo)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Editar"
                        >
                          <Edit className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => handleDelete(insumo.idInsumo)}
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
        title={editingInsumo ? 'Editar Insumo' : 'Nuevo Insumo'}
        size="xl"
      >
        <form onSubmit={handleSubmit} className="space-y-4 max-h-[70vh] overflow-y-auto">
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
                Código *
              </label>
              <input
                type="text"
                required
                value={formData.codigo}
                onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

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
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tipo de Insumo *
              </label>
              <select
                required
                value={formData.idTipoInsumo}
                onChange={(e) => setFormData({ ...formData, idTipoInsumo: e.target.value })}
                disabled={tiposInsumo.length === 0}
                className={`w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent ${
                  tiposInsumo.length === 0 ? 'bg-gray-100 cursor-not-allowed' : ''
                }`}
              >
                <option value="">
                  {tiposInsumo.length === 0 ? 'No hay tipos disponibles' : 'Seleccione un tipo'}
                </option>
                {tiposInsumo.map((tipo) => (
                  <option key={tipo.idTipoInsumo} value={tipo.idTipoInsumo}>
                    {tipo.nombre}
                  </option>
                ))}
              </select>
              {tiposInsumo.length === 0 && (
                <p className="mt-1 text-sm text-red-600">
                  Debe crear al menos un tipo de insumo primero. Vaya a la sección de Tipos de Insumo.
                </p>
              )}
              {tiposInsumo.length === 0 && (
                <p className="mt-1 text-sm text-red-600">
                  Debe crear al menos un tipo de insumo primero
                </p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Unidad de Medida *
              </label>
              <input
                type="text"
                required
                value={formData.unidadMedida}
                onChange={(e) => setFormData({ ...formData, unidadMedida: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ej: Unidad, Caja, Kg, Litro"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Cantidad en Stock *
              </label>
              <input
                type="number"
                required
                min="0"
                value={formData.cantidadStock}
                onChange={(e) => setFormData({ ...formData, cantidadStock: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Stock Mínimo *
              </label>
              <input
                type="number"
                required
                min="0"
                value={formData.stockMinimo}
                onChange={(e) => setFormData({ ...formData, stockMinimo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Stock Máximo
              </label>
              <input
                type="number"
                min="0"
                value={formData.stockMaximo}
                onChange={(e) => setFormData({ ...formData, stockMaximo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Precio de Compra *
              </label>
              <input
                type="number"
                required
                step="0.01"
                min="0"
                value={formData.precioCompra}
                onChange={(e) => setFormData({ ...formData, precioCompra: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Precio de Venta
              </label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.precioVenta}
                onChange={(e) => setFormData({ ...formData, precioVenta: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Estado *
              </label>
              <select
                required
                value={formData.estado}
                onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                {ESTADOS_INSUMO.map((estado) => (
                  <option key={estado} value={estado}>
                    {estado}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Lote
              </label>
              <input
                type="text"
                value={formData.lote}
                onChange={(e) => setFormData({ ...formData, lote: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de Vencimiento
              </label>
              <input
                type="date"
                value={formData.fechaVencimiento}
                onChange={(e) => setFormData({ ...formData, fechaVencimiento: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Ubicación
              </label>
              <input
                type="text"
                value={formData.ubicacion}
                onChange={(e) => setFormData({ ...formData, ubicacion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              value={formData.descripcion}
              onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
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

          <div className="flex items-center space-x-4">
            <div className="flex items-center">
              <input
                type="checkbox"
                id="requiereRefrigeracion"
                checked={formData.requiereRefrigeracion}
                onChange={(e) => setFormData({ ...formData, requiereRefrigeracion: e.target.checked })}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label htmlFor="requiereRefrigeracion" className="ml-2 block text-sm text-gray-700">
                Requiere Refrigeración
              </label>
            </div>

            <div className="flex items-center">
              <input
                type="checkbox"
                id="requiereReceta"
                checked={formData.requiereReceta}
                onChange={(e) => setFormData({ ...formData, requiereReceta: e.target.checked })}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label htmlFor="requiereReceta" className="ml-2 block text-sm text-gray-700">
                Requiere Receta
              </label>
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
              {editingInsumo ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

