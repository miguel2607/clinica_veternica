import { useEffect, useState } from 'react';
import { historiaClinicaService, evolucionClinicaService, vacunacionService, mascotaService, propietarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function HistoriasClinicasPage() {
  const { user } = useAuth();
  const [misMascotas, setMisMascotas] = useState([]);
  const [selectedMascota, setSelectedMascota] = useState(null);
  const [historiaClinica, setHistoriaClinica] = useState(null);
  const [evoluciones, setEvoluciones] = useState([]);
  const [vacunaciones, setVacunaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingDetalle, setLoadingDetalle] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadMisMascotas();
  }, []);

  const loadMisMascotas = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener el perfil del propietario
      console.log('🔍 Obteniendo mis mascotas...');
      const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
      const propietario = propietarioRes.data;

      console.log('✅ Propietario:', propietario);

      if (!propietario?.idPropietario) {
        throw new Error('El perfil del propietario no contiene un idPropietario válido');
      }

      // Obtener las mascotas del propietario
      const mascotasRes = await mascotaService.getByPropietario(propietario.idPropietario);
      console.log('✅ Mis mascotas:', mascotasRes.data);

      setMisMascotas(mascotasRes.data || []);
    } catch (error) {
      console.error('❌ Error al cargar mascotas:', error);
      setError(`Error al cargar tus mascotas: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectMascota = async (mascota) => {
    try {
      setLoadingDetalle(true);
      setSelectedMascota(mascota);
      setError('');
      setHistoriaClinica(null);
      setEvoluciones([]);
      setVacunaciones([]);

      console.log('🔍 Cargando historia clínica de:', mascota.nombre);

      // Obtener historia clínica de la mascota
      const historiaRes = await historiaClinicaService.getByMascota(mascota.idMascota);

      // El backend puede devolver un objeto o una lista; normalizamos a una lista
      const historiaData = historiaRes.data;
      const historias = Array.isArray(historiaData)
        ? historiaData
        : historiaData
          ? [historiaData]
          : [];

      if (historias.length > 0) {
        const historia = historias[0]; // Tomar la primera historia activa
        setHistoriaClinica(historia);

        console.log('✅ Historia clínica:', historia);

        // Cargar evoluciones y vacunaciones
        try {
          const [evolucionesRes, vacunacionesRes] = await Promise.all([
            evolucionClinicaService.getByHistoriaClinica(historia.idHistoriaClinica),
            vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica)
          ]);

          setEvoluciones(evolucionesRes.data || []);
          setVacunaciones(vacunacionesRes.data || []);

          console.log('✅ Evoluciones:', evolucionesRes.data);
          console.log('✅ Vacunaciones:', vacunacionesRes.data);
        } catch (error) {
          console.log('⚠️ No se pudieron cargar evoluciones o vacunaciones:', error);
          // No lanzar error, simplemente dejar vacíos
        }
      } else {
        console.log('⚠️ No se encontró historia clínica');
      }
    } catch (error) {
      console.error('❌ Error al cargar historia:', error);

      // Si es un 404, significa que no tiene historia clínica aún
      if (error.response?.status === 404) {
        console.log('ℹ️ La mascota aún no tiene historia clínica registrada');
        setHistoriaClinica(null);
        setEvoluciones([]);
        setVacunaciones([]);
        // No mostrar error, ya que esto es esperado para mascotas sin historia
      } else {
        // Otros errores sí son problemáticos
        setError(`Error al cargar la historia clínica: ${error.response?.data?.message || error.message}`);
      }
    } finally {
      setLoadingDetalle(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando información...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Historias Clínicas de mis Mascotas</h2>
      </div>

      {error && !selectedMascota && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Panel izquierdo - Lista de mascotas */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow p-4">
            <h3 className="font-semibold mb-3">Selecciona una Mascota</h3>

            <div className="space-y-2">
              {misMascotas.map((mascota) => (
                <div
                  key={mascota.idMascota}
                  onClick={() => handleSelectMascota(mascota)}
                  className={`p-3 rounded-lg border cursor-pointer transition-colors ${
                    selectedMascota?.idMascota === mascota.idMascota
                      ? 'bg-primary-50 border-primary-500'
                      : 'bg-white border-gray-200 hover:bg-gray-50'
                  }`}
                >
                  <div className="font-medium text-gray-900">{mascota.nombre}</div>
                  <div className="text-sm text-gray-600">
                    {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                  </div>
                  <div className="text-xs text-gray-500 mt-1">
                    {mascota.sexo} {mascota.peso && `- ${mascota.peso} kg`}
                  </div>
                </div>
              ))}

              {misMascotas.length === 0 && (
                <div className="text-center text-gray-500 py-4 text-sm">
                  No tienes mascotas registradas
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Panel derecho - Detalles de la historia clínica */}
        <div className="lg:col-span-2">
          {selectedMascota ? (
            loadingDetalle ? (
              <div className="bg-white rounded-lg shadow p-8 text-center">
                <div className="text-gray-500">Cargando historia clínica...</div>
              </div>
            ) : historiaClinica ? (
              <div className="space-y-6">
                {/* Encabezado */}
                <div className="bg-white rounded-lg shadow p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-900">
                        Historia Clínica - {selectedMascota.nombre}
                      </h3>
                      <p className="text-gray-600">HC #{historiaClinica.idHistoriaClinica}</p>
                    </div>
                  </div>

                  {/* Información básica */}
                  <div className="grid grid-cols-2 gap-4 bg-gray-50 p-4 rounded-lg">
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Fecha de Creación</span>
                      <span className="font-medium">{historiaClinica.fechaCreacion || 'N/A'}</span>
                    </div>
                    {historiaClinica.motivoConsulta && (
                      <div className="col-span-2">
                        <span className="text-sm text-gray-600 block mb-1">Motivo de Consulta</span>
                        <span className="font-medium">{historiaClinica.motivoConsulta}</span>
                      </div>
                    )}
                    {historiaClinica.antecedentesMedicos && (
                      <div className="col-span-2">
                        <span className="text-sm text-gray-600 block mb-1">Antecedentes Médicos</span>
                        <p className="text-sm">{historiaClinica.antecedentesMedicos}</p>
                      </div>
                    )}
                    {historiaClinica.alergias && (
                      <div className="col-span-2">
                        <span className="text-sm text-gray-600 block mb-1">Alergias</span>
                        <p className="text-sm text-red-600 font-medium">{historiaClinica.alergias}</p>
                      </div>
                    )}
                  </div>
                </div>

                {/* Evoluciones Clínicas */}
                {evoluciones.length > 0 && (
                  <div className="bg-white rounded-lg shadow p-6">
                    <h4 className="font-semibold text-lg mb-4">Historial de Evoluciones</h4>

                    <div className="space-y-4">
                      {evoluciones.map((evolucion, index) => {
                        const fecha = evolucion.fechaEvolucion || evolucion.fecha;
                        const tipo = evolucion.tipoEvolucion || 'Evolución';
                        const doctor =
                          evolucion.veterinario?.nombreCompleto ||
                          `${evolucion.veterinario?.nombres || ''} ${evolucion.veterinario?.apellidos || ''}`.trim();

                        return (
                          <div key={evolucion.idEvolucionClinica || index} className="border-l-4 border-primary-500 pl-4 py-2">
                            <div className="flex justify-between items-start mb-2">
                              <div>
                                <div className="text-xs uppercase text-primary-600 font-semibold">{tipo}</div>
                                <div className="font-medium text-gray-900">
                                  {fecha ? new Date(fecha).toLocaleString('es-ES') : 'Sin fecha'}
                                </div>
                              </div>
                              {doctor && (
                                <div className="text-sm text-gray-600 text-right">
                                  Dr(a). {doctor}
                                </div>
                              )}
                            </div>

                            {evolucion.motivoConsulta && (
                              <div className="mt-2">
                                <div className="text-sm font-medium text-gray-700">Motivo de Consulta:</div>
                                <div className="text-sm text-gray-600 whitespace-pre-wrap">{evolucion.motivoConsulta}</div>
                              </div>
                            )}

                            {evolucion.hallazgosExamen && (
                              <div className="mt-2">
                                <div className="text-sm font-medium text-gray-700">Hallazgos del Examen:</div>
                                <div className="text-sm text-gray-600 whitespace-pre-wrap">{evolucion.hallazgosExamen}</div>
                              </div>
                            )}

                            {evolucion.diagnostico && (
                              <div className="mt-2">
                                <div className="text-sm font-medium text-gray-700">Diagnóstico:</div>
                                <div className="text-sm text-gray-600 whitespace-pre-wrap">{evolucion.diagnostico}</div>
                              </div>
                            )}

                            {evolucion.planTratamiento && (
                              <div className="mt-2">
                                <div className="text-sm font-medium text-gray-700">Plan de Tratamiento:</div>
                                <div className="text-sm text-gray-600 whitespace-pre-wrap">{evolucion.planTratamiento}</div>
                              </div>
                            )}

                            {(evolucion.peso ||
                              evolucion.temperatura ||
                              evolucion.frecuenciaCardiaca ||
                              evolucion.frecuenciaRespiratoria) && (
                              <div className="mt-3 grid grid-cols-2 gap-2 bg-gray-50 rounded p-2 text-sm">
                                {evolucion.peso && <div><span className="font-medium">Peso:</span> {evolucion.peso} kg</div>}
                                {evolucion.temperatura && <div><span className="font-medium">Temperatura:</span> {evolucion.temperatura} °C</div>}
                                {evolucion.frecuenciaCardiaca && <div><span className="font-medium">FC:</span> {evolucion.frecuenciaCardiaca} bpm</div>}
                                {evolucion.frecuenciaRespiratoria && <div><span className="font-medium">FR:</span> {evolucion.frecuenciaRespiratoria} rpm</div>}
                              </div>
                            )}

                            {evolucion.observaciones && (
                              <div className="mt-2">
                                <div className="text-sm font-medium text-gray-700">Observaciones:</div>
                                <div className="text-sm text-gray-600 whitespace-pre-wrap">{evolucion.observaciones}</div>
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}

                {/* Vacunaciones */}
                {vacunaciones.length > 0 && (
                  <div className="bg-white rounded-lg shadow p-6">
                    <h4 className="font-semibold text-lg mb-4">Registro de Vacunaciones</h4>

                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Vacuna</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Lote</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Próxima Dosis</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                          {vacunaciones.map((vacuna, index) => (
                            <tr key={vacuna.idVacunacion || index}>
                              <td className="px-4 py-3 text-sm">{vacuna.fechaAplicacion}</td>
                              <td className="px-4 py-3 text-sm font-medium">{vacuna.nombreVacuna}</td>
                              <td className="px-4 py-3 text-sm text-gray-600">{vacuna.lote || 'N/A'}</td>
                              <td className="px-4 py-3 text-sm">
                                {vacuna.proximaDosis ? (
                                  <span className="text-orange-600 font-medium">{vacuna.proximaDosis}</span>
                                ) : (
                                  <span className="text-gray-400">-</span>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {evoluciones.length === 0 && vacunaciones.length === 0 && (
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    <div className="flex items-start gap-3">
                      <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <div className="flex-1">
                        <p className="text-sm text-blue-800">
                          La historia clínica está registrada pero aún no tiene evoluciones ni vacunaciones.
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                {historiaClinica.observaciones && (
                  <div className="bg-white rounded-lg shadow p-6">
                    <h4 className="font-semibold text-lg mb-2">Observaciones</h4>
                    <p className="text-sm text-gray-700">{historiaClinica.observaciones}</p>
                  </div>
                )}
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow p-8 text-center">
                <svg className="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p className="text-lg font-medium text-gray-900 mb-2">
                  {selectedMascota.nombre} aún no tiene historia clínica
                </p>
                <p className="text-sm text-gray-500">
                  La historia clínica se creará cuando tu mascota tenga su primera consulta
                </p>
              </div>
            )
          ) : (
            <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
              <svg className="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122" />
              </svg>
              <p className="text-lg font-medium mb-2">Selecciona una mascota</p>
              <p className="text-sm">Elige una mascota del panel izquierdo para ver su historia clínica</p>
            </div>
          )}
        </div>
      </div>

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información sobre Historias Clínicas</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Aquí puedes ver el historial médico completo de tus mascotas</li>
              <li>• Las evoluciones son registros de las consultas y seguimientos realizados</li>
              <li>• El registro de vacunaciones muestra todas las vacunas aplicadas y próximas dosis</li>
              <li>• Si tienes dudas sobre el historial médico, consulta con tu veterinario</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
