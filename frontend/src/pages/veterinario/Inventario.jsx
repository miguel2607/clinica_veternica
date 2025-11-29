import { useEffect, useState } from 'react';
import { inventarioService, insumoService, tipoInsumoService } from '../../services/api';
import { Package, Search, AlertTriangle, CheckCircle, Archive, Layers } from 'lucide-react';

export default function InventarioPage() {
  const [inventario, setInventario] = useState([]);
  const [tipos, setTipos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [tipoFilter, setTipoFilter] = useState('TODOS');
  const [estadoFilter, setEstadoFilter] = useState('TODOS'); // DISPONIBLE, BAJO, AGOTADO

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      const [inventarioRes, tiposRes] = await Promise.all([
        inventarioService.getAll(),
        tipoInsumoService.getActivos()
      ]);

      console.log('✅ Inventario cargado:', inventarioRes.data);
      console.log('✅ Tipos cargados:', tiposRes.data);

      setInventario(inventarioRes.data || []);
      setTipos(tiposRes.data || []);
    } catch (error) {
      console.error('❌ Error al cargar inventario:', error);
      setError(`Error al cargar inventario: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const getEstadoStock = (item) => {
    const cantidad = item.cantidadActual || 0;
    const minimo = item.stockMinimo || item.insumo?.stockMinimo || 0;

    if (cantidad === 0) return { label: 'Agotado', color: 'bg-red-100 text-red-800', icon: <Archive className="w-4 h-4" /> };
    if (cantidad <= minimo) return { label: 'Stock Bajo', color: 'bg-yellow-100 text-yellow-800', icon: <AlertTriangle className="w-4 h-4" /> };
    return { label: 'Disponible', color: 'bg-green-100 text-green-800', icon: <CheckCircle className="w-4 h-4" /> };
  };

  const inventarioFiltrado = inventario.filter(item => {
    const matchSearch = (item.nombreInsumo || item.insumo?.nombre || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
                       item.insumo?.descripcion?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchTipo = tipoFilter === 'TODOS' ||
                     item.insumo?.tipoInsumo?.idTipoInsumo?.toString() === tipoFilter;

    let matchEstado = true;
    if (estadoFilter !== 'TODOS') {
      const estado = getEstadoStock(item);
      if (estadoFilter === 'DISPONIBLE') matchEstado = estado.label === 'Disponible';
      if (estadoFilter === 'BAJO') matchEstado = estado.label === 'Stock Bajo';
      if (estadoFilter === 'AGOTADO') matchEstado = estado.label === 'Agotado';
    }

    return matchSearch && matchTipo && matchEstado;
  });

  // Calcular estadísticas
  const disponibles = inventario.filter(item => {
    const estado = getEstadoStock(item);
    return estado.label === 'Disponible';
  }).length;

  const stockBajo = inventario.filter(item => {
    const estado = getEstadoStock(item);
    return estado.label === 'Stock Bajo';
  }).length;

  const agotados = inventario.filter(item => {
    const estado = getEstadoStock(item);
    return estado.label === 'Agotado';
  }).length;

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
        <h2 className="text-2xl font-bold">Inventario de Insumos</h2>
        <button
          onClick={loadData}
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

      {/* Estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Insumos</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{inventario.length}</p>
            </div>
            <div className="bg-blue-500 text-white p-3 rounded-lg">
              <Package className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Disponibles</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{disponibles}</p>
            </div>
            <div className="bg-green-500 text-white p-3 rounded-lg">
              <CheckCircle className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Stock Bajo</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{stockBajo}</p>
            </div>
            <div className="bg-yellow-500 text-white p-3 rounded-lg">
              <AlertTriangle className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Agotados</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{agotados}</p>
            </div>
            <div className="bg-red-500 text-white p-3 rounded-lg">
              <Archive className="w-6 h-6" />
            </div>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Buscador */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar insumo..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Filtro por tipo */}
          <div>
            <select
              value={tipoFilter}
              onChange={(e) => setTipoFilter(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODOS">Todos los tipos</option>
              {tipos.map(tipo => (
                <option key={tipo.idTipoInsumo} value={tipo.idTipoInsumo.toString()}>
                  {tipo.nombre}
                </option>
              ))}
            </select>
          </div>

          {/* Filtro por estado */}
          <div>
            <select
              value={estadoFilter}
              onChange={(e) => setEstadoFilter(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODOS">Todos los estados</option>
              <option value="DISPONIBLE">Disponible</option>
              <option value="BAJO">Stock Bajo</option>
              <option value="AGOTADO">Agotado</option>
            </select>
          </div>
        </div>
      </div>

      {/* Vista de tarjetas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {inventarioFiltrado.map((item) => {
          const estado = getEstadoStock(item);
          return (
            <div key={item.idInventario} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow">
              <div className="p-6">
                <div className="flex justify-between items-start mb-3">
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg text-gray-900">{item.nombreInsumo || item.insumo?.nombre || 'Sin nombre'}</h3>
                    {item.insumo?.tipoInsumo && (
                      <div className="flex items-center gap-1 mt-1">
                        <Layers className="w-3 h-3 text-gray-400" />
                        <span className="text-xs text-gray-500">{item.insumo.tipoInsumo.nombre}</span>
                      </div>
                    )}
                  </div>
                  <div className={`flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${estado.color}`}>
                    {estado.icon}
                    {estado.label}
                  </div>
                </div>

                {item.insumo?.descripcion && (
                  <p className="text-sm text-gray-600 mb-3 line-clamp-2">{item.insumo.descripcion}</p>
                )}

                <div className="space-y-2">
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-gray-600">Stock Actual:</span>
                    <span className={`font-bold ${
                      item.cantidadActual === 0 ? 'text-red-600' :
                      item.cantidadActual <= (item.stockMinimo || 0) ? 'text-yellow-600' :
                      'text-green-600'
                    }`}>
                      {item.cantidadActual || 0} unidades
                    </span>
                  </div>

                  <div className="flex justify-between items-center text-sm">
                    <span className="text-gray-600">Stock Mínimo:</span>
                    <span className="font-medium text-gray-900">
                      {item.stockMinimo || item.insumo?.stockMinimo || 0} unidades
                    </span>
                  </div>

                  {item.insumo?.unidadMedida && (
                    <div className="flex justify-between items-center text-sm">
                      <span className="text-gray-600">Unidad:</span>
                      <span className="font-medium text-gray-900">{item.insumo.unidadMedida}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {inventarioFiltrado.length === 0 && (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          <Package className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-lg font-medium">No se encontraron insumos</p>
          <p className="text-sm mt-2">Intenta ajustar los filtros de búsqueda</p>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <Package className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información para Veterinarios</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Consulta el inventario disponible antes de una atención</li>
              <li>• Verifica el stock de medicamentos e insumos necesarios</li>
              <li>• Si un insumo está agotado o con stock bajo, contacta al auxiliar</li>
              <li>• Esta vista es de solo consulta, no puedes modificar el inventario</li>
              <li>• El auxiliar es responsable de la gestión del inventario</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
