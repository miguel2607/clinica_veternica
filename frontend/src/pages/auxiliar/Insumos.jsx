import { useEffect, useState } from 'react';
import { insumoService, tipoInsumoService } from '../../services/api';

const ESTADOS_INSUMO = ['DISPONIBLE', 'AGOTADO', 'EN_PEDIDO'];

export default function InsumosPage() {
  const [insumos, setInsumos] = useState([]);
  const [tiposInsumo, setTiposInsumo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('TODOS');
  const [mostrarStockBajo, setMostrarStockBajo] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMovimiento, setModalMovimiento] = useState(false);
  const [editingInsumo, setEditingInsumo] = useState(null);
  const [movimientoData, setMovimientoData] = useState({
    tipo: 'ENTRADA', // ENTRADA o SALIDA
    cantidad: '',
    observacion: ''
  });
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
        tipoInsumoService.getActivos(),
      ]);
      setInsumos(insumosRes.data || []);
      setTiposInsumo(tiposRes.data || []);

      if (tiposRes.data?.length === 0) {
        setError('No hay tipos de insumo disponibles. Por favor, cree al menos un tipo de insumo primero.');
      } else {
        setError('');
      }
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
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

  const handleOpenMovimiento = (insumo) => {
    setEditingInsumo(insumo);
    setMovimientoData({
      tipo: 'ENTRADA',
      cantidad: '',
      observacion: ''
    });
    setModalMovimiento(true);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.idTipoInsumo) {
      setError('Debe seleccionar un tipo de insumo');
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
      };

      if (editingInsumo) {
        await insumoService.update(editingInsumo.idInsumo, data);
        setSuccess('Insumo actualizado exitosamente');
      } else {
        await insumoService.create(data);
        setSuccess('Insumo creado exitosamente');
      }

      await loadData();
      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar insumo:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleMovimiento = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!movimientoData.cantidad || movimientoData.cantidad <= 0) {
      setError('La cantidad debe ser mayor a 0');
      return;
    }

    try {
      const cantidadActual = editingInsumo.cantidadStock || 0;
      let nuevaCantidad;

      if (movimientoData.tipo === 'ENTRADA') {
        nuevaCantidad = cantidadActual + parseInt(movimientoData.cantidad);
      } else {
        nuevaCantidad = cantidadActual - parseInt(movimientoData.cantidad);
        if (nuevaCantidad < 0) {
          setError('No hay suficiente stock para realizar esta salida');
          return;
        }
      }

      const data = {
        ...editingInsumo,
        cantidadStock: nuevaCantidad,
        idTipoInsumo: editingInsumo.tipoInsumo?.idTipoInsumo,
        observaciones: `${editingInsumo.observaciones || ''}\n[${new Date().toLocaleDateString()}] ${movimientoData.tipo}: ${movimientoData.cantidad} ${editingInsumo.unidadMedida}. ${movimientoData.observacion}`
      };

      await insumoService.update(editingInsumo.idInsumo, data);
      setSuccess(`${movimientoData.tipo === 'ENTRADA' ? 'Entrada' : 'Salida'} registrada exitosamente`);

      await loadData();
      setTimeout(() => {
        setModalMovimiento(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al registrar movimiento:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este insumo?')) return;

    try {
      await insumoService.delete(id);
      setSuccess('Insumo eliminado exitosamente');
      await loadData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error al eliminar insumo:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleToggleActivo = async (insumo) => {
    try {
      if (insumo.activo) {
        await insumoService.desactivar(insumo.idInsumo);
        setSuccess('Insumo desactivado');
      } else {
        await insumoService.activar(insumo.idInsumo);
        setSuccess('Insumo activado');
      }
      await loadData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error al cambiar estado:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const insumosFiltrados = insumos.filter(insumo => {
    const matchSearch = insumo.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       insumo.codigo?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchEstado = filtroEstado === 'TODOS' || insumo.estado === filtroEstado;

    const matchStockBajo = !mostrarStockBajo || (insumo.cantidadStock <= insumo.stockMinimo);

    return matchSearch && matchEstado && matchStockBajo;
  });

  if (loading) {
    return <div className="text-center py-8">Cargando insumos...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Gestión de Insumos</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors inline-flex items-center gap-2"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nuevo Insumo
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

      {/* Alertas de Stock Bajo */}
      {insumos.filter(i => i.cantidadStock <= i.stockMinimo && i.activo).length > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <svg className="w-5 h-5 text-yellow-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <div className="flex-1">
              <h4 className="font-semibold text-yellow-900 mb-1">Alertas de Stock Bajo</h4>
              <p className="text-sm text-yellow-800">
                Hay {insumos.filter(i => i.cantidadStock <= i.stockMinimo && i.activo).length} insumo(s) con stock bajo o agotado.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Buscar insumo
            </label>
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar por nombre o código..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Estado
            </label>
            <select
              value={filtroEstado}
              onChange={(e) => setFiltroEstado(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODOS">Todos los estados</option>
              {ESTADOS_INSUMO.map(estado => (
                <option key={estado} value={estado}>{estado}</option>
              ))}
            </select>
          </div>

          <div className="flex items-end">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={mostrarStockBajo}
                onChange={(e) => setMostrarStockBajo(e.target.checked)}
                className="rounded"
              />
              <span className="text-sm text-gray-700">Solo stock bajo</span>
            </label>
          </div>
        </div>
      </div>

      {/* Tabla de insumos */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Código</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ubicación</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {insumosFiltrados.map((insumo) => (
                <tr key={insumo.idInsumo} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {insumo.codigo}
                  </td>
                  <td className="px-6 py-4">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{insumo.nombre}</div>
                      {insumo.lote && (
                        <div className="text-xs text-gray-500">Lote: {insumo.lote}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {insumo.tipoInsumo?.nombre || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm">
                      <div className={`font-semibold ${
                        insumo.cantidadStock <= insumo.stockMinimo ? 'text-red-600' : 'text-gray-900'
                      }`}>
                        {insumo.cantidadStock} {insumo.unidadMedida}
                      </div>
                      <div className="text-xs text-gray-500">
                        Mín: {insumo.stockMinimo} {insumo.stockMaximo && `| Máx: ${insumo.stockMaximo}`}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {insumo.ubicacion || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                      insumo.estado === 'DISPONIBLE'
                        ? 'bg-green-100 text-green-800'
                        : insumo.estado === 'AGOTADO'
                        ? 'bg-red-100 text-red-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {insumo.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleOpenMovimiento(insumo)}
                        className="text-blue-600 hover:text-blue-900"
                        title="Registrar movimiento"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleOpenModal(insumo)}
                        className="text-indigo-600 hover:text-indigo-900"
                        title="Editar"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleToggleActivo(insumo)}
                        className={insumo.activo ? 'text-yellow-600 hover:text-yellow-900' : 'text-green-600 hover:text-green-900'}
                        title={insumo.activo ? 'Desactivar' : 'Activar'}
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={insumo.activo ? "M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" : "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"} />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleDelete(insumo.idInsumo)}
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

        {insumosFiltrados.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            No se encontraron insumos con los filtros seleccionados.
          </div>
        )}
      </div>

      {/* Modal de crear/editar insumo */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">
                {editingInsumo ? 'Editar Insumo' : 'Nuevo Insumo'}
              </h3>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Código *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.codigo}
                      onChange={(e) => setFormData({...formData, codigo: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
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
                      onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Descripción
                    </label>
                    <textarea
                      value={formData.descripcion}
                      onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                      rows="2"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Tipo de Insumo *
                    </label>
                    <select
                      required
                      value={formData.idTipoInsumo}
                      onChange={(e) => setFormData({...formData, idTipoInsumo: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Seleccione un tipo</option>
                      {tiposInsumo.map(tipo => (
                        <option key={tipo.idTipoInsumo} value={tipo.idTipoInsumo}>
                          {tipo.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Unidad de Medida *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.unidadMedida}
                      onChange={(e) => setFormData({...formData, unidadMedida: e.target.value})}
                      placeholder="ej: unidades, ml, gr"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
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
                      onChange={(e) => setFormData({...formData, cantidadStock: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
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
                      onChange={(e) => setFormData({...formData, stockMinimo: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
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
                      onChange={(e) => setFormData({...formData, stockMaximo: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Ubicación
                    </label>
                    <input
                      type="text"
                      value={formData.ubicacion}
                      onChange={(e) => setFormData({...formData, ubicacion: e.target.value})}
                      placeholder="ej: Estante A, Refrigerador B"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Lote
                    </label>
                    <input
                      type="text"
                      value={formData.lote}
                      onChange={(e) => setFormData({...formData, lote: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Fecha de Vencimiento
                    </label>
                    <input
                      type="date"
                      value={formData.fechaVencimiento}
                      onChange={(e) => setFormData({...formData, fechaVencimiento: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Estado
                    </label>
                    <select
                      value={formData.estado}
                      onChange={(e) => setFormData({...formData, estado: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      {ESTADOS_INSUMO.map(estado => (
                        <option key={estado} value={estado}>{estado}</option>
                      ))}
                    </select>
                  </div>

                  <div className="flex items-center gap-4">
                    <label className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={formData.requiereRefrigeracion}
                        onChange={(e) => setFormData({...formData, requiereRefrigeracion: e.target.checked})}
                        className="rounded"
                      />
                      <span className="text-sm text-gray-700">Refrigeración</span>
                    </label>

                    <label className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={formData.requiereReceta}
                        onChange={(e) => setFormData({...formData, requiereReceta: e.target.checked})}
                        className="rounded"
                      />
                      <span className="text-sm text-gray-700">Requiere receta</span>
                    </label>
                  </div>
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
                    {editingInsumo ? 'Actualizar' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Modal de movimiento (Entrada/Salida) */}
      {modalMovimiento && editingInsumo && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">
                Registrar Movimiento - {editingInsumo.nombre}
              </h3>

              <div className="bg-gray-50 p-3 rounded-lg mb-4">
                <div className="text-sm text-gray-600">Stock actual:</div>
                <div className="text-2xl font-bold text-gray-900">
                  {editingInsumo.cantidadStock} {editingInsumo.unidadMedida}
                </div>
              </div>

              <form onSubmit={handleMovimiento} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo de movimiento
                  </label>
                  <select
                    value={movimientoData.tipo}
                    onChange={(e) => setMovimientoData({...movimientoData, tipo: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="ENTRADA">Entrada (agregar stock)</option>
                    <option value="SALIDA">Salida (reducir stock)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Cantidad *
                  </label>
                  <input
                    type="number"
                    required
                    min="1"
                    value={movimientoData.cantidad}
                    onChange={(e) => setMovimientoData({...movimientoData, cantidad: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observación
                  </label>
                  <textarea
                    value={movimientoData.observacion}
                    onChange={(e) => setMovimientoData({...movimientoData, observacion: e.target.value})}
                    placeholder="Motivo del movimiento..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    rows="3"
                  />
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
                    onClick={() => setModalMovimiento(false)}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                  >
                    Registrar
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
