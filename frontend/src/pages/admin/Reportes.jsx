import { useEffect, useState } from 'react';
import { reporteService } from '../../services/api';
import { FileText, Calendar, Package, Users, TrendingUp, Download } from 'lucide-react';

export default function ReportesPage() {
  const [reporteCitas, setReporteCitas] = useState(null);
  const [reporteInventario, setReporteInventario] = useState(null);
  const [reporteVeterinarios, setReporteVeterinarios] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fechaInicio, setFechaInicio] = useState(() => {
    const date = new Date();
    date.setMonth(date.getMonth() - 1);
    return date.toISOString().split('T')[0];
  });
  const [fechaFin, setFechaFin] = useState(() => {
    return new Date().toISOString().split('T')[0];
  });
  const [error, setError] = useState('');

  useEffect(() => {
    // Cargar todos los reportes automáticamente al montar el componente
    cargarTodosLosReportes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    // Recargar reporte de citas cuando cambien las fechas (solo si ya se cargó antes)
    if (reporteCitas !== null) {
      cargarReporteCitas();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fechaInicio, fechaFin]);

  const cargarTodosLosReportes = async () => {
    await Promise.all([
      cargarReporteCitas(),
      cargarReporteInventario(),
      cargarReporteVeterinarios()
    ]);
  };

  const cargarReporteCitas = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('📊 Cargando reporte de citas desde', fechaInicio, 'hasta', fechaFin);
      const response = await reporteService.getReporteCitas(fechaInicio, fechaFin);
      console.log('✅ Reporte de citas cargado:', response.data);
      setReporteCitas(response.data);
    } catch (error) {
      console.error('❌ Error al cargar reporte de citas:', error);
      console.error('❌ Detalles del error:', error.response?.data);
      setError(`Error al cargar reporte de citas: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const cargarReporteInventario = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('📦 Cargando reporte de inventario...');
      const response = await reporteService.getReporteInventario();
      console.log('✅ Reporte de inventario cargado:', response.data);
      setReporteInventario(response.data);
    } catch (error) {
      console.error('❌ Error al cargar reporte de inventario:', error);
      setError(`Error al cargar reporte de inventario: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const cargarReporteVeterinarios = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('👨‍⚕️ Cargando reporte de veterinarios...');
      const response = await reporteService.getReporteVeterinarios(fechaInicio, fechaFin);
      console.log('✅ Reporte de veterinarios cargado:', response.data);
      setReporteVeterinarios(response.data);
    } catch (error) {
      console.error('❌ Error al cargar reporte de veterinarios:', error);
      setError(`Error al cargar reporte de veterinarios: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Reportes</h2>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg animate-slideDown">
          {error}
        </div>
      )}

      {/* Filtros de fecha */}
      <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
        <h3 className="text-lg font-semibold mb-4">Filtros de Fecha</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha Inicio
            </label>
            <input
              type="date"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha Fin
            </label>
            <input
              type="date"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>
      </div>

      {/* Reporte de Citas */}
      <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold flex items-center">
            <Calendar className="w-5 h-5 mr-2 text-primary-600" />
            Reporte de Citas
          </h3>
          <button
            onClick={cargarReporteCitas}
            disabled={loading}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center gap-2 transition-colors disabled:opacity-50"
          >
            {loading ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                Cargando...
              </>
            ) : (
              <>
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                Actualizar Reporte
              </>
            )}
          </button>
        </div>

        {loading && !reporteCitas ? (
          <div className="text-center py-8 text-gray-500">
            Cargando reporte de citas...
          </div>
        ) : reporteCitas ? (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-4">
            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Citas</p>
              <p className="text-2xl font-bold text-blue-600">{reporteCitas.totalCitas || 0}</p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Citas Atendidas</p>
              <p className="text-2xl font-bold text-green-600">{reporteCitas.citasAtendidas || 0}</p>
            </div>
            <div className="bg-yellow-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Citas Canceladas</p>
              <p className="text-2xl font-bold text-yellow-600">{reporteCitas.citasCanceladas || 0}</p>
            </div>
            <div className="bg-purple-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Citas Programadas</p>
              <p className="text-2xl font-bold text-purple-600">{reporteCitas.citasProgramadas || 0}</p>
            </div>
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Haz clic en "Generar Reporte" para ver las estadísticas
          </div>
        )}
      </div>

      {/* Reporte de Inventario */}
      <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold flex items-center">
            <Package className="w-5 h-5 mr-2 text-primary-600" />
            Reporte de Inventario
          </h3>
          <button
            onClick={cargarReporteInventario}
            disabled={loading}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-colors disabled:opacity-50"
          >
            {loading ? 'Cargando...' : 'Actualizar Reporte'}
          </button>
        </div>

        {loading && !reporteInventario ? (
          <div className="text-center py-8 text-gray-500">
            Cargando reporte de inventario...
          </div>
        ) : reporteInventario ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Insumos</p>
              <p className="text-2xl font-bold text-blue-600">{reporteInventario.totalItems || 0}</p>
            </div>
            <div className="bg-red-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Stock Bajo</p>
              <p className="text-2xl font-bold text-red-600">{reporteInventario.totalStockBajo || 0}</p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Valor Total</p>
              <p className="text-2xl font-bold text-green-600">
                ${reporteInventario.valorTotalInventario?.toLocaleString() || '0'}
              </p>
            </div>
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Haz clic en "Actualizar Reporte" para ver las estadísticas
          </div>
        )}
      </div>

      {/* Reporte de Veterinarios */}
      <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold flex items-center">
            <Users className="w-5 h-5 mr-2 text-primary-600" />
            Reporte de Veterinarios
          </h3>
          <button
            onClick={cargarReporteVeterinarios}
            disabled={loading}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-colors disabled:opacity-50"
          >
            {loading ? 'Cargando...' : 'Actualizar Reporte'}
          </button>
        </div>

        {loading && !reporteVeterinarios ? (
          <div className="text-center py-8 text-gray-500">
            Cargando reporte de veterinarios...
          </div>
        ) : reporteVeterinarios ? (
          <div className="mt-4">
            <div className="mb-4">
              <p className="text-sm text-gray-600">Total de Atenciones</p>
              <p className="text-2xl font-bold text-primary-600">{reporteVeterinarios.totalAtenciones || 0}</p>
            </div>
            {reporteVeterinarios.estadisticasPorVeterinario && reporteVeterinarios.estadisticasPorVeterinario.length > 0 && (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Veterinario</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total Atenciones</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {reporteVeterinarios.estadisticasPorVeterinario.map((item, index) => (
                      <tr key={index} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {item.nombreVeterinario || 'N/A'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {item.totalCitasAtendidas || 0}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Haz clic en "Actualizar Reporte" para ver las estadísticas
          </div>
        )}
      </div>
    </div>
  );
}
