import { useEffect, useState } from 'react';
import { vacunacionService, historiaClinicaService, mascotaService } from '../../services/api';
import { Syringe, Search, AlertCircle, Calendar, CheckCircle, Clock } from 'lucide-react';

export default function VacunacionesPage() {
  const [vacunaciones, setVacunaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filtroProximaDosis, setFiltroProximaDosis] = useState('TODAS'); // TODAS, PROXIMAS, SIN_PROXIMA

  useEffect(() => {
    loadVacunaciones();
  }, []);

  const loadVacunaciones = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener todas las historias clínicas
      const historiasRes = await historiaClinicaService.getActivas();
      const historias = historiasRes.data || [];

      // Cargar vacunaciones de todas las historias
      const todasVacunaciones = [];
      for (const historia of historias) {
        try {
          const vacunasRes = await vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica);
          const vacunas = vacunasRes.data || [];

          // Agregar información de la mascota a cada vacuna
          vacunas.forEach(vacuna => {
            todasVacunaciones.push({
              ...vacuna,
              mascota: historia.mascota,
              historiaClinica: historia
            });
          });
        } catch (error) {
          console.error(`Error al cargar vacunaciones de historia ${historia.idHistoriaClinica}:`, error);
        }
      }

      // Ordenar por fecha de aplicación (más reciente primero)
      todasVacunaciones.sort((a, b) => {
        const fechaA = new Date(a.fechaAplicacion || '');
        const fechaB = new Date(b.fechaAplicacion || '');
        return fechaB - fechaA;
      });

      console.log('✅ Vacunaciones cargadas:', todasVacunaciones);
      setVacunaciones(todasVacunaciones);
    } catch (error) {
      console.error('❌ Error al cargar vacunaciones:', error);
      setError(`Error al cargar vacunaciones: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const vacunacionesFiltradas = vacunaciones.filter(vacuna => {
    const matchSearch = vacuna.nombreVacuna?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       vacuna.mascota?.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       vacuna.lote?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       vacuna.veterinario?.nombres?.toLowerCase().includes(searchTerm.toLowerCase());

    let matchProximaDosis = true;
    if (filtroProximaDosis === 'PROXIMAS') {
      matchProximaDosis = vacuna.proximaDosis != null && vacuna.proximaDosis !== '';
    } else if (filtroProximaDosis === 'SIN_PROXIMA') {
      matchProximaDosis = !vacuna.proximaDosis;
    }

    return matchSearch && matchProximaDosis;
  });

  // Calcular estadísticas
  const hoy = new Date().toISOString().split('T')[0];
  const proximasPendientes = vacunaciones.filter(v =>
    v.proximaDosis && v.proximaDosis >= hoy
  ).length;

  const conProximaDosis = vacunaciones.filter(v => v.proximaDosis).length;
  const sinProximaDosis = vacunaciones.length - conProximaDosis;

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
        <h2 className="text-2xl font-bold">Gestión de Vacunaciones</h2>
        <button
          onClick={loadVacunaciones}
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
              <p className="text-sm font-medium text-gray-600">Total Vacunaciones</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{vacunaciones.length}</p>
            </div>
            <div className="bg-blue-500 text-white p-3 rounded-lg">
              <Syringe className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Próximas Dosis</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{proximasPendientes}</p>
            </div>
            <div className="bg-orange-500 text-white p-3 rounded-lg">
              <Clock className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Con Próxima Dosis</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{conProximaDosis}</p>
            </div>
            <div className="bg-green-500 text-white p-3 rounded-lg">
              <CheckCircle className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Sin Próxima Dosis</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{sinProximaDosis}</p>
            </div>
            <div className="bg-gray-500 text-white p-3 rounded-lg">
              <Calendar className="w-6 h-6" />
            </div>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Buscador */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar por mascota, vacuna, lote o veterinario..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Filtro por próxima dosis */}
          <div>
            <select
              value={filtroProximaDosis}
              onChange={(e) => setFiltroProximaDosis(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODAS">Todas las vacunaciones</option>
              <option value="PROXIMAS">Con próxima dosis programada</option>
              <option value="SIN_PROXIMA">Sin próxima dosis</option>
            </select>
          </div>
        </div>
      </div>

      {/* Tabla de vacunaciones */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Propietario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Vacuna</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha Aplicación</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Lote</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Próxima Dosis</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Veterinario</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {vacunacionesFiltradas.map((vacuna, index) => (
                <tr key={vacuna.idVacunacion || index} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{vacuna.mascota?.nombre || 'N/A'}</div>
                    <div className="text-xs text-gray-500">
                      {vacuna.mascota?.raza?.nombre} - {vacuna.mascota?.raza?.especie?.nombre}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {vacuna.mascota?.propietario?.nombres || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center gap-2">
                      <Syringe className="w-4 h-4 text-primary-600" />
                      <span className="text-sm font-medium text-gray-900">{vacuna.nombreVacuna}</span>
                    </div>
                    {vacuna.observaciones && (
                      <div className="text-xs text-gray-500 mt-1 line-clamp-1">{vacuna.observaciones}</div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {vacuna.fechaAplicacion || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {vacuna.lote || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {vacuna.proximaDosis ? (
                      <div className="flex items-center gap-2">
                        <Clock className="w-4 h-4 text-orange-500" />
                        <span className="font-medium text-orange-600">{vacuna.proximaDosis}</span>
                      </div>
                    ) : (
                      <span className="text-gray-400">-</span>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {vacuna.veterinario?.nombres ? (
                      `Dr(a). ${vacuna.veterinario.nombres}`
                    ) : (
                      'N/A'
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {vacunacionesFiltradas.length === 0 && (
          <div className="text-center py-8 text-gray-500">
            <Syringe className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <p className="text-lg font-medium">No se encontraron vacunaciones</p>
            <p className="text-sm mt-2">Intenta ajustar los filtros de búsqueda</p>
          </div>
        )}
      </div>

      {/* Próximas vacunaciones pendientes */}
      {proximasPendientes > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
          <div className="flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
            <div className="flex-1">
              <h4 className="font-semibold text-yellow-900 mb-2">
                Próximas Dosis Programadas ({proximasPendientes})
              </h4>
              <p className="text-sm text-yellow-800 mb-3">
                Hay {proximasPendientes} vacunaciones con próximas dosis programadas. Recuerda notificar a los propietarios.
              </p>
              <div className="space-y-2 max-h-64 overflow-y-auto">
                {vacunaciones
                  .filter(v => v.proximaDosis && v.proximaDosis >= hoy)
                  .sort((a, b) => (a.proximaDosis || '').localeCompare(b.proximaDosis || ''))
                  .slice(0, 10)
                  .map((vacuna, index) => (
                    <div key={index} className="bg-white rounded p-3 text-sm">
                      <div className="flex justify-between items-start">
                        <div>
                          <span className="font-medium">{vacuna.mascota?.nombre}</span> - {vacuna.nombreVacuna}
                          <p className="text-gray-600 text-xs mt-1">
                            Propietario: {vacuna.mascota?.propietario?.nombres} - Tel: {vacuna.mascota?.propietario?.telefono || 'N/A'}
                          </p>
                        </div>
                        <span className="text-orange-600 font-medium whitespace-nowrap ml-4">
                          {vacuna.proximaDosis}
                        </span>
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <Syringe className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información para Auxiliares</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Puedes consultar todas las vacunaciones registradas en el sistema</li>
              <li>• Utiliza los filtros para encontrar vacunaciones específicas</li>
              <li>• Las próximas dosis programadas aparecen destacadas</li>
              <li>• Contacta a los propietarios para recordar las próximas dosis</li>
              <li>• El registro de vacunaciones es responsabilidad del veterinario</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
