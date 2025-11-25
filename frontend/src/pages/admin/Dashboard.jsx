import { useEffect, useState } from 'react';
import { dashboardService } from '../../services/api';
import { Calendar, Users, Package, AlertTriangle, TrendingUp } from 'lucide-react';

export default function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const response = await dashboardService.getDashboard();
      setDashboard(response.data);
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

  const stats = [
    {
      title: 'Citas del Día',
      value: dashboard?.totalCitasHoy || 0,
      icon: <Calendar className="w-8 h-8" />,
      color: 'bg-blue-500',
      delay: '0ms',
    },
    {
      title: 'Citas Programadas',
      value: dashboard?.totalCitasProgramadas || 0,
      icon: <Users className="w-8 h-8" />,
      color: 'bg-green-500',
      delay: '100ms',
    },
    {
      title: 'Stock Bajo',
      value: dashboard?.totalStockBajo || 0,
      icon: <AlertTriangle className="w-8 h-8" />,
      color: 'bg-yellow-500',
      delay: '200ms',
    },
    {
      title: 'Notificaciones',
      value: dashboard?.totalNotificacionesRecientes || 0,
      icon: <Package className="w-8 h-8" />,
      color: 'bg-red-500',
      delay: '300ms',
    },
  ];

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <div
            key={index}
            className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-all duration-300 animate-scaleIn"
            style={{ animationDelay: stat.delay }}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
              </div>
              <div className={`${stat.color} text-white p-3 rounded-lg transform hover:scale-110 transition-transform duration-300`}>
                {stat.icon}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-all duration-300 animate-slideUp">
          <h2 className="text-xl font-semibold mb-4 text-gray-900">Citas de Hoy</h2>
          <div className="space-y-3">
            {dashboard?.citasHoy && Array.isArray(dashboard.citasHoy) && dashboard.citasHoy.length > 0 ? (
              dashboard.citasHoy.slice(0, 5).map((cita, index) => (
                <div
                  key={cita.idCita || index}
                  className="border-b pb-3 hover:bg-gray-50 p-2 rounded transition-colors duration-200"
                  style={{ animationDelay: `${index * 50}ms` }}
                >
                  <p className="font-medium text-gray-900">
                    {cita.mascota?.nombre || 'Sin mascota'}
                  </p>
                  <p className="text-sm text-gray-600">
                    {cita.fechaCita || ''} {cita.horaCita ? `- ${cita.horaCita}` : ''}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    Estado: {cita.estado || 'N/A'}
                  </p>
                </div>
              ))
            ) : (
              <p className="text-gray-500">No hay citas para hoy</p>
            )}
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-all duration-300 animate-slideUp" style={{ animationDelay: '100ms' }}>
          <h2 className="text-xl font-semibold mb-4 text-gray-900">Stock Bajo</h2>
          <div className="space-y-3">
            {dashboard?.stockBajo && Array.isArray(dashboard.stockBajo) && dashboard.stockBajo.length > 0 ? (
              dashboard.stockBajo.slice(0, 5).map((inventario, index) => {
                const nombreInsumo = inventario.insumo?.nombre || inventario.nombre || 'Insumo sin nombre';
                const cantidadActual = inventario.cantidadActual || 0;
                const stockMinimo = inventario.stockMinimo || inventario.insumo?.stockMinimo || 0;
                
                return (
                  <div
                    key={inventario.idInventario || index}
                    className="border-b pb-3 hover:bg-red-50 p-2 rounded transition-colors duration-200"
                    style={{ animationDelay: `${index * 50}ms` }}
                  >
                    <p className="font-medium text-red-600">
                      {nombreInsumo}
                    </p>
                    <p className="text-sm text-gray-600">
                      Stock actual: <span className="font-bold text-red-600">{cantidadActual}</span> | Mínimo: {stockMinimo}
                    </p>
                  </div>
                );
              })
            ) : (
              <p className="text-gray-500">No hay alertas de stock bajo</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

