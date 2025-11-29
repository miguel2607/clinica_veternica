import { useEffect, useState } from 'react';
import { inventarioService, insumoService } from '../../services/api';
import { AlertTriangle, Package, Plus, Minus, ArrowUpDown } from 'lucide-react';

export default function InventarioPage() {
  const [inventario, setInventario] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [modalMovimiento, setModalMovimiento] = useState(false);
  const [itemSeleccionado, setItemSeleccionado] = useState(null);
  const [movimientoData, setMovimientoData] = useState({
    tipo: 'ENTRADA',
    cantidad: '',
    observacion: ''
  });

  useEffect(() => {
    loadInventario();
  }, []);

  const loadInventario = async () => {
    try {
      setLoading(true);
      const response = await inventarioService.getAll();
      console.log('📦 Datos del inventario recibidos:', response.data);
      setInventario(response.data || []);
      setError('');
    } catch (error) {
      console.error('Error al cargar inventario:', error);
      setError('Error al cargar el inventario');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenMovimiento = (item) => {
    setItemSeleccionado(item);
    setMovimientoData({
      tipo: 'ENTRADA',
      cantidad: '',
      observacion: ''
    });
    setModalMovimiento(true);
    setError('');
    setSuccess('');
  };

  const handleRegistrarMovimiento = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!movimientoData.cantidad || movimientoData.cantidad <= 0) {
      setError('La cantidad debe ser mayor a 0');
      return;
    }

    try {
      const cantidadActual = itemSeleccionado.cantidadActual || 0;
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

      // Actualizar el insumo con la nueva cantidad
      const insumoId = itemSeleccionado.insumo?.idInsumo;
      if (!insumoId) {
        setError('No se encontró el ID del insumo');
        return;
      }

      const insumoActualizado = {
        ...itemSeleccionado.insumo,
        cantidadStock: nuevaCantidad,
        idTipoInsumo: itemSeleccionado.insumo?.tipoInsumo?.idTipoInsumo,
        observaciones: `${itemSeleccionado.insumo?.observaciones || ''}\n[${new Date().toLocaleDateString()}] ${movimientoData.tipo}: ${movimientoData.cantidad} unidades. ${movimientoData.observacion}`
      };

      await insumoService.update(insumoId, insumoActualizado);
      setSuccess(`${movimientoData.tipo === 'ENTRADA' ? 'Entrada' : 'Salida'} registrada exitosamente`);

      await loadInventario();
      setTimeout(() => {
        setModalMovimiento(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al registrar movimiento:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
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
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Gestión de Inventario</h2>
        <button
          onClick={loadInventario}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
        >
          Actualizar
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

      {/* Alertas de Stock */}
      {inventario.filter(item => {
        const cantidad = item.cantidadActual || 0;
        const minimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
        return cantidad <= minimo;
      }).length > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-yellow-600 mt-0.5" />
            <div className="flex-1">
              <h4 className="font-semibold text-yellow-900 mb-1">Alertas de Stock</h4>
              <p className="text-sm text-yellow-800">
                Hay {inventario.filter(item => {
                  const cantidad = item.cantidadActual || 0;
                  const minimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
                  return cantidad <= minimo;
                }).length} insumo(s) con stock bajo o agotado que requieren atención.
              </p>
            </div>
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Insumo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Actual</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Mínimo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {inventario.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No hay items en el inventario
                  </td>
                </tr>
              ) : (
                inventario.map((item) => {
                  const nombreInsumo = item.nombreInsumo || item.insumo?.nombre || item.nombre || 'Insumo sin nombre';
                  const tipoInsumo = item.insumo?.tipoInsumo?.nombre || 'N/A';
                  const cantidadActual = item.cantidadActual || 0;
                  const stockMinimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
                  const esStockBajo = cantidadActual > 0 && cantidadActual <= stockMinimo;

                  return (
                    <tr key={item.idInventario} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{nombreInsumo}</div>
                        {item.insumo?.codigo && (
                          <div className="text-xs text-gray-500">Código: {item.insumo.codigo}</div>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{tipoInsumo}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className={`text-sm font-semibold ${
                          cantidadActual === 0 ? 'text-red-600' :
                          esStockBajo ? 'text-yellow-600' : 'text-gray-900'
                        }`}>
                          {cantidadActual} {item.insumo?.unidadMedida || 'unidades'}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{stockMinimo}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {cantidadActual === 0 ? (
                          <span className="px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 inline-flex items-center gap-1">
                            <AlertTriangle className="w-3 h-3" />
                            Agotado
                          </span>
                        ) : esStockBajo ? (
                          <span className="px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 inline-flex items-center gap-1">
                            <AlertTriangle className="w-3 h-3" />
                            Stock Bajo
                          </span>
                        ) : (
                          <span className="px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                            Normal
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                          onClick={() => handleOpenMovimiento(item)}
                          className="text-primary-600 hover:text-primary-900 inline-flex items-center gap-1"
                          title="Registrar movimiento"
                        >
                          <ArrowUpDown className="w-5 h-5" />
                          Movimiento
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de movimiento */}
      {modalMovimiento && itemSeleccionado && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">
                Registrar Movimiento de Inventario
              </h3>

              <div className="bg-gray-50 p-4 rounded-lg mb-4">
                <div className="text-sm text-gray-600">Insumo:</div>
                <div className="text-lg font-bold text-gray-900 mb-2">
                  {itemSeleccionado.nombreInsumo || itemSeleccionado.insumo?.nombre}
                </div>
                <div className="text-sm text-gray-600">Stock actual:</div>
                <div className="text-2xl font-bold text-gray-900">
                  {itemSeleccionado.cantidadActual || 0} {itemSeleccionado.insumo?.unidadMedida || 'unidades'}
                </div>
                <div className="text-xs text-gray-500 mt-1">
                  Stock mínimo: {itemSeleccionado.stockMinimo || itemSeleccionado.insumo?.stockMinimo || 0}
                </div>
              </div>

              <form onSubmit={handleRegistrarMovimiento} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo de movimiento
                  </label>
                  <div className="grid grid-cols-2 gap-2">
                    <button
                      type="button"
                      onClick={() => setMovimientoData({...movimientoData, tipo: 'ENTRADA'})}
                      className={`flex items-center justify-center gap-2 px-4 py-3 rounded-lg border-2 transition-colors ${
                        movimientoData.tipo === 'ENTRADA'
                          ? 'border-green-500 bg-green-50 text-green-700'
                          : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
                      }`}
                    >
                      <Plus className="w-5 h-5" />
                      Entrada
                    </button>
                    <button
                      type="button"
                      onClick={() => setMovimientoData({...movimientoData, tipo: 'SALIDA'})}
                      className={`flex items-center justify-center gap-2 px-4 py-3 rounded-lg border-2 transition-colors ${
                        movimientoData.tipo === 'SALIDA'
                          ? 'border-red-500 bg-red-50 text-red-700'
                          : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
                      }`}
                    >
                      <Minus className="w-5 h-5" />
                      Salida
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Cantidad *
                  </label>
                  <input
                    type="number"
                    required
                    min="1"
                    max={movimientoData.tipo === 'SALIDA' ? itemSeleccionado.cantidadActual : undefined}
                    value={movimientoData.cantidad}
                    onChange={(e) => setMovimientoData({...movimientoData, cantidad: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    placeholder="Cantidad a ingresar/retirar"
                  />
                  {movimientoData.tipo === 'SALIDA' && movimientoData.cantidad > itemSeleccionado.cantidadActual && (
                    <p className="text-xs text-red-600 mt-1">
                      La cantidad no puede ser mayor al stock actual
                    </p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observación
                  </label>
                  <textarea
                    value={movimientoData.observacion}
                    onChange={(e) => setMovimientoData({...movimientoData, observacion: e.target.value})}
                    placeholder="Motivo del movimiento (opcional)..."
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

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <Package className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Gestión de Inventario</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Puedes registrar entradas y salidas de stock para cada insumo</li>
              <li>• Las entradas incrementan el stock disponible</li>
              <li>• Las salidas reducen el stock (no puedes retirar más de lo disponible)</li>
              <li>• Los movimientos quedan registrados con fecha y observaciones</li>
              <li>• El sistema te alertará cuando un insumo esté bajo o agotado</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

