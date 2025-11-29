import { useEffect, useState } from 'react';
import { horarioService, veterinarioService } from '../../services/api';

export default function HorariosPage() {
  const [horarios, setHorarios] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [veterinarioSeleccionado, setVeterinarioSeleccionado] = useState('TODOS');
  const [diaSeleccionado, setDiaSeleccionado] = useState('TODOS');
  const [soloActivos, setSoloActivos] = useState(true);

  const diasSemana = {
    LUNES: 'Lunes',
    MARTES: 'Martes',
    MIERCOLES: 'Miércoles',
    JUEVES: 'Jueves',
    VIERNES: 'Viernes',
    SABADO: 'Sábado',
    DOMINGO: 'Domingo',
    MONDAY: 'Lunes',
    TUESDAY: 'Martes',
    WEDNESDAY: 'Miércoles',
    THURSDAY: 'Jueves',
    FRIDAY: 'Viernes',
    SATURDAY: 'Sábado',
    SUNDAY: 'Domingo'
  };

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      // Cargar veterinarios y horarios en paralelo
      const [veterinariosRes, horariosRes] = await Promise.all([
        veterinarioService.getActivos(),
        horarioService.getAll()
      ]);

      console.log('✅ Veterinarios cargados:', veterinariosRes.data);
      console.log('✅ Horarios cargados:', horariosRes.data);

      setVeterinarios(veterinariosRes.data || []);

      // Ordenar horarios por veterinario y día de la semana
      const horariosOrdenados = (horariosRes.data || []).sort((a, b) => {
        // Primero ordenar por veterinario
        const nombreA = a.veterinario?.nombreCompleto || '';
        const nombreB = b.veterinario?.nombreCompleto || '';
        if (nombreA !== nombreB) {
          return nombreA.localeCompare(nombreB);
        }
        // Luego por día de la semana
        const ordenDias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO', 
                           'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
        const indexA = ordenDias.indexOf(a.diaSemana);
        const indexB = ordenDias.indexOf(b.diaSemana);
        // Si no se encuentra en el orden, ponerlo al final
        const finalIndexA = indexA === -1 ? 999 : indexA;
        const finalIndexB = indexB === -1 ? 999 : indexB;
        return finalIndexA - finalIndexB;
      });

      setHorarios(horariosOrdenados);
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar los datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const horariosFiltrados = horarios.filter(horario => {
    // Filtrar por veterinario
    if (veterinarioSeleccionado !== 'TODOS') {
      if (horario.veterinario?.idPersonal?.toString() !== veterinarioSeleccionado) {
        return false;
      }
    }

    // Filtrar por día
    if (diaSeleccionado !== 'TODOS') {
      if (horario.diaSemana !== diaSeleccionado) {
        return false;
      }
    }

    // Filtrar por estado activo
    return !soloActivos || horario.activo;
  });

  // Agrupar horarios por veterinario para la vista de tarjetas
  const horariosPorVeterinario = horariosFiltrados.reduce((acc, horario) => {
    const vetId = horario.veterinario?.idPersonal || 'sin-veterinario';
    if (!acc[vetId]) {
      acc[vetId] = {
        veterinario: horario.veterinario,
        horarios: []
      };
    }
    acc[vetId].horarios.push(horario);
    return acc;
  }, {});

  if (loading) {
    return <div className="text-center py-8">Cargando horarios...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Horarios de Veterinarios</h2>
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

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label htmlFor="veterinario-filter-select" className="block text-sm font-medium text-gray-700 mb-2">
              Veterinario
            </label>
            <select
              id="veterinario-filter-select"
              value={veterinarioSeleccionado}
              onChange={(e) => setVeterinarioSeleccionado(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODOS">Todos los veterinarios</option>
              {veterinarios.map((vet) => (
                <option key={vet.idPersonal} value={vet.idPersonal.toString()}>
                  {vet.nombres} {vet.apellidos} - {vet.especialidad}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="dia-semana-filter-select" className="block text-sm font-medium text-gray-700 mb-2">
              Día de la semana
            </label>
            <select
              id="dia-semana-filter-select"
              value={diaSeleccionado}
              onChange={(e) => setDiaSeleccionado(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODOS">Todos los días</option>
              {Object.entries(diasSemana).map(([key, value]) => (
                <option key={key} value={key}>{value}</option>
              ))}
            </select>
          </div>

          <div className="flex items-end">
            <label htmlFor="solo-activos-checkbox" className="flex items-center gap-2 cursor-pointer">
              <input
                id="solo-activos-checkbox"
                type="checkbox"
                checked={soloActivos}
                onChange={(e) => setSoloActivos(e.target.checked)}
                className="rounded"
              />
              <span className="text-sm text-gray-700">Solo mostrar horarios activos</span>
            </label>
          </div>
        </div>
      </div>

      {horariosFiltrados.length > 0 ? (
        <>
          {/* Vista de Tabla */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Veterinario</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Especialidad</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Día</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Inicio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Fin</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Disponibilidad</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {horariosFiltrados.map((horario) => (
                    <tr key={horario.idHorario} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {horario.veterinario?.nombreCompleto || 'N/A'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                        {horario.veterinario?.especialidad || 'N/A'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {diasSemana[horario.diaSemana] || horario.diaSemana || 'N/A'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {horario.horaInicio}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {horario.horaFin}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                          horario.activo
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {horario.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {horario.activo && horario.veterinario?.activo ? (
                          <span className="inline-flex items-center gap-1 text-xs text-green-600">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                            </svg>
                            Disponible
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 text-xs text-gray-500">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                            No disponible
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Vista de Tarjetas por Veterinario */}
          {veterinarioSeleccionado === 'TODOS' && (
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">Vista por Veterinario</h3>
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                {Object.entries(horariosPorVeterinario).map(([vetId, data]) => (
                  <div key={vetId} className="bg-white rounded-lg shadow p-6">
                    <div className="mb-4">
                      <h4 className="font-semibold text-lg text-gray-900">
                        {data.veterinario?.nombreCompleto || 'N/A'}
                      </h4>
                      <p className="text-sm text-gray-600">
                        {data.veterinario?.especialidad || 'N/A'}
                      </p>
                    </div>
                    <div className="space-y-2">
                      {data.horarios.map((horario) => (
                        <div
                          key={horario.idHorario}
                          className={`flex justify-between items-center p-3 rounded-lg ${
                            horario.activo
                              ? 'bg-green-50 border border-green-200'
                              : 'bg-gray-50 border border-gray-200'
                          }`}
                        >
                          <div>
                            <div className="font-medium text-sm">
                              {diasSemana[horario.diaSemana] || horario.diaSemana || 'N/A'}
                            </div>
                            <div className="text-xs text-gray-600">
                              {horario.horaInicio} - {horario.horaFin}
                            </div>
                          </div>
                          {horario.activo ? (
                            <span className="text-xs text-green-600 font-medium">✓ Disponible</span>
                          ) : (
                            <span className="text-xs text-gray-500">No disponible</span>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
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
                <h4 className="font-semibold text-blue-900 mb-1">Información sobre horarios</h4>
                <ul className="text-sm text-blue-800 space-y-1">
                  <li>• Los horarios marcados como "Disponible" están activos para agendar citas.</li>
                  <li>• Usa los filtros para encontrar disponibilidad de veterinarios específicos.</li>
                  <li>• Los horarios inactivos no permiten agendar citas en esos bloques.</li>
                  <li>• Mostrando {horariosFiltrados.length} horario(s) según los filtros seleccionados.</li>
                </ul>
              </div>
            </div>
          </div>
        </>
      ) : (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          No se encontraron horarios con los filtros seleccionados.
        </div>
      )}
    </div>
  );
}
