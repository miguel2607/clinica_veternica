import { useEffect, useState } from 'react';
import { inventarioService, insumoService, tipoInsumoService, historiaClinicaService } from '../../services/api';
import { AlertTriangle, Package, TrendingUp, TrendingDown, Archive, DollarSign, Activity, FileText } from 'lucide-react';

export default function AuxiliarDashboard() {
  const [stockBajo, setStockBajo] = useState([]);
  const [stockAgotado, setStockAgotado] = useState([]);
  const [estadisticas, setEstadisticas] = useState({
    totalInsumos: 0,
    stockBajo: 0,
    stockAgotado: 0,
    tiposInsumo: 0,
    valorTotal: 0,
    historiasActivas: 0
  });
  const [movimientosRecientes, setMovimientosRecientes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);

      // Cargar datos en paralelo
      const [inventarioRes, insumosRes, tiposRes, historiasRes] = await Promise.all([
        inventarioService.getAll(),
        insumoService.getAll(),
        tipoInsumoService.getActivos(),
        historiaClinicaService.getActivas()
      ]);

      const inventario = inventarioRes.data || [];
      const insumos = insumosRes.data || [];
      const tipos = tiposRes.data || [];
      const historias = historiasRes.data || [];

      // Calcular stock bajo y agotado
      const bajo = inventario.filter(item => {
        const cantidadActual = item.cantidadActual || 0;
        const stockMinimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
        return cantidadActual > 0 && cantidadActual <= stockMinimo;
      });

      const agotado = inventario.filter(item =>
        item.cantidadActual === 0
      );

      // Calcular valor total del inventario (si hay precio unitario)
      const valorTotal = inventario.reduce((sum, item) => {
        const precio = item.insumo?.precioUnitario || 0;
        const cantidad = item.cantidadActual || 0;
        return sum + (precio * cantidad);
      }, 0);

      // Simular movimientos recientes (los últimos 5 items del inventario ordenados por fecha)
      const movimientos = inventario
        .filter(item => item.fechaUltimaActualizacion || item.fechaCreacion)
        .sort((a, b) => {
          const fechaA = new Date(a.fechaUltimaActualizacion || a.fechaCreacion || '');
          const fechaB = new Date(b.fechaUltimaActualizacion || b.fechaCreacion || '');
          return fechaB - fechaA;
        })
        .slice(0, 5);

      setStockBajo(bajo);
      setStockAgotado(agotado);
      setMovimientosRecientes(movimientos);
      setEstadisticas({
        totalInsumos: insumos.length,
        stockBajo: bajo.length,
        stockAgotado: agotado.length,
        tiposInsumo: tipos.length,
        valorTotal: Math.round(valorTotal),
        historiasActivas: historias.length
      });
    } catch (error) {
      console.error('Error al cargar dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const statsCards = [
    {
      title: 'Total Insumos',
      value: estadisticas.totalInsumos,
      icon: <Package className="w-6 h-6" />,
      color: 'bg-blue-500'
    },
    {
      title: 'Stock Bajo',
      value: estadisticas.stockBajo,
      icon: <AlertTriangle className="w-6 h-6" />,
      color: 'bg-yellow-500'
    },
    {
      title: 'Stock Agotado',
      value: estadisticas.stockAgotado,
      icon: <Archive className="w-6 h-6" />,
      color: 'bg-red-500'
    },
    {
      title: 'Tipos de Insumo',
      value: estadisticas.tiposInsumo,
      icon: <TrendingUp className="w-6 h-6" />,
      color: 'bg-green-500'
    },
    {
      title: 'Historias Activas',
      value: estadisticas.historiasActivas,
      icon: <FileText className="w-6 h-6" />,
      color: 'bg-purple-500'
    }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Dashboard Auxiliar</h2>
        <p className="text-gray-600 mt-1">Gestión de inventario y soporte clínico</p>
      </div>

      {/* Tarjetas de estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {statsCards.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
              </div>
              <div className={`${stat.color} text-white p-3 rounded-lg`}>
                {stat.icon}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Alertas de Stock */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Stock Agotado */}
        {stockAgotado.length > 0 && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-6">
            <div className="flex items-center mb-4">
              <Archive className="w-6 h-6 text-red-600 mr-2" />
              <h3 className="text-xl font-semibold text-red-800">Stock Agotado ({stockAgotado.length})</h3>
            </div>
            <div className="space-y-2 max-h-64 overflow-y-auto">
              {stockAgotado.map((item) => (
                <div key={item.idInventario} className="bg-white rounded-lg p-3 border border-red-200">
                  <p className="font-semibold text-gray-900">{item.nombreInsumo || item.insumo?.nombre || 'Sin nombre'}</p>
                  <p className="text-sm text-gray-600">
                    Stock actual: <span className="font-bold text-red-600">{item.cantidadActual}</span> |
                    Mínimo: {item.stockMinimo || item.insumo?.stockMinimo || 0}
                  </p>
                  {item.insumo?.tipoInsumo && (
                    <p className="text-xs text-gray-500 mt-1">Tipo: {item.insumo.tipoInsumo.nombre}</p>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Stock Bajo */}
        {stockBajo.length > 0 && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
            <div className="flex items-center mb-4">
              <AlertTriangle className="w-6 h-6 text-yellow-600 mr-2" />
              <h3 className="text-xl font-semibold text-yellow-800">Stock Bajo ({stockBajo.length})</h3>
            </div>
            <div className="space-y-2 max-h-64 overflow-y-auto">
              {stockBajo.map((item) => {
                const nombreInsumo = item.nombreInsumo || item.insumo?.nombre || item.nombre || 'Insumo sin nombre';
                const cantidadActual = item.cantidadActual || 0;
                const stockMinimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
                
                return (
                  <div key={item.idInventario || item.idInsumo} className="bg-white rounded-lg p-3 border border-yellow-200">
                    <p className="font-semibold text-gray-900">{nombreInsumo}</p>
                    <p className="text-sm text-gray-600">
                      Stock actual: <span className="font-bold text-yellow-600">{cantidadActual}</span> | Mínimo: {stockMinimo}
                    </p>
                    {item.insumo?.tipoInsumo && (
                      <p className="text-xs text-gray-500 mt-1">Tipo: {item.insumo.tipoInsumo.nombre}</p>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Sin alertas */}
        {stockAgotado.length === 0 && stockBajo.length === 0 && (
          <div className="lg:col-span-2 bg-green-50 border border-green-200 rounded-lg p-8 text-center">
            <Package className="w-16 h-16 text-green-600 mx-auto mb-4" />
            <p className="text-xl font-semibold text-green-800 mb-2">Inventario en Buen Estado</p>
            <p className="text-green-600">No hay alertas de stock bajo o agotado</p>
          </div>
        )}
      </div>

      {/* Movimientos Recientes */}
      {movimientosRecientes.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Actividad Reciente</h3>
            <Activity className="w-5 h-5 text-gray-400" />
          </div>

          <div className="space-y-3">
            {movimientosRecientes.map((item, index) => (
              <div key={item.idInventario || index} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h4 className="font-semibold text-gray-900">{item.nombreInsumo || item.insumo?.nombre || 'Sin nombre'}</h4>
                    <p className="text-sm text-gray-600 mt-1">
                      Stock actual: {item.cantidadActual} unidades
                    </p>
                    {item.insumo?.tipoInsumo && (
                      <p className="text-xs text-gray-500 mt-1">
                        Tipo: {item.insumo.tipoInsumo.nombre}
                      </p>
                    )}
                  </div>
                  <div className="text-right ml-4">
                    <div className={`inline-flex items-center px-2 py-1 rounded-full text-xs ${
                      item.cantidadActual === 0 ? 'bg-red-100 text-red-800' :
                      item.cantidadActual <= (item.stockMinimo || 0) ? 'bg-yellow-100 text-yellow-800' :
                      'bg-green-100 text-green-800'
                    }`}>
                      {item.cantidadActual === 0 ? 'Agotado' :
                       item.cantidadActual <= (item.stockMinimo || 0) ? 'Stock Bajo' :
                       'Disponible'}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <TrendingUp className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Accesos Rápidos</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Gestiona el inventario desde "Inventario"</li>
              <li>• Administra insumos en "Gestión de Insumos"</li>
              <li>• Organiza categorías en "Tipos de Insumo"</li>
              <li>• Consulta y actualiza historias clínicas en "Historias Clínicas"</li>
              <li>• Registra vacunaciones en "Vacunaciones"</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
