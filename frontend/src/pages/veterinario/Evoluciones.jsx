import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { evolucionClinicaService, historiaClinicaService, mascotaService, citaService, veterinarioService } from '../../services/api';
import { Eye } from 'lucide-react';

export default function EvolucionesPage() {
  const { user } = useAuth();
  const [evoluciones, setEvoluciones] = useState([]);
  const [historiasClinicas, setHistoriasClinicas] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [modalHistoriaOpen, setModalHistoriaOpen] = useState(false);
  const [selectedHistoria, setSelectedHistoria] = useState(null);
  const [historiaDetalle, setHistoriaDetalle] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [veterinario, setVeterinario] = useState(null);

  const [formData, setFormData] = useState({
    idHistoriaClinica: '',
    tipoEvolucion: 'CONSULTA',
    motivoConsulta: '',
    hallazgosExamen: '',
    diagnostico: '',
    planTratamiento: '',
    observaciones: '',
    peso: '',
    temperatura: '',
    frecuenciaCardiaca: '',
    frecuenciaRespiratoria: ''
  });

  useEffect(() => {
    if (user) {
      loadVeterinario();
    }
  }, [user]);

  useEffect(() => {
    if (veterinario) {
      loadData();
    }
  }, [veterinario]);

  const loadVeterinario = async () => {
    try {
      let vet = null;
      
      try {
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        vet = veterinarioRes.data;
      } catch (error404) {
        const todosVeterinarios = await veterinarioService.getAll();
        vet = todosVeterinarios.data.find(v => 
          v.correo?.toLowerCase() === user?.email?.toLowerCase() ||
          v.usuario?.email?.toLowerCase() === user?.email?.toLowerCase() ||
          v.usuario?.idUsuario === user?.idUsuario ||
          v.usuario?.username === user?.username
        );
      }

      if (vet?.idPersonal) {
        setVeterinario(vet);
      } else {
        setError('No se encontró el perfil del veterinario');
      }
    } catch (error) {
      console.error('Error al cargar veterinario:', error);
      setError('Error al cargar el perfil del veterinario');
    }
  };

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      if (!veterinario?.idPersonal) {
        setError('No se encontró el perfil del veterinario');
        setLoading(false);
        return;
      }

      // Obtener citas del veterinario
      console.log('🔍 Obteniendo citas del veterinario:', veterinario.idPersonal);
      const citasRes = await citaService.getByVeterinario(veterinario.idPersonal);
      const citas = citasRes.data || [];

      console.log('✅ Citas encontradas:', citas.length);

      // Obtener historias clínicas únicas de las mascotas de las citas
      const historiasUnicas = new Map();
      const mascotasUnicas = new Map();

      for (const cita of citas) {
        if (cita.mascota?.idMascota) {
          // Guardar la mascota
          if (!mascotasUnicas.has(cita.mascota.idMascota)) {
            mascotasUnicas.set(cita.mascota.idMascota, cita.mascota);
          }

          try {
            const historiaRes = await historiaClinicaService.getByMascota(cita.mascota.idMascota);
            const historia = historiaRes.data;
            if (historia && !historiasUnicas.has(historia.idHistoriaClinica)) {
              // Enriquecer con información de la mascota
              historia.mascota = cita.mascota;
              historiasUnicas.set(historia.idHistoriaClinica, historia);
              console.log(`✅ Historia clínica encontrada para mascota ${cita.mascota.nombre}`);
            }
          } catch (error) {
            // Error 404 es esperado si la mascota no tiene historia clínica aún
            if (error.response?.status === 404) {
              console.log(`ℹ️ Mascota ${cita.mascota.nombre} (ID: ${cita.mascota.idMascota}) aún no tiene historia clínica`);
            } else {
              console.error(`❌ Error al cargar historia de mascota ${cita.mascota.idMascota}:`, error);
            }
          }
        }
      }

      const historiasArray = Array.from(historiasUnicas.values());
      const mascotasArray = Array.from(mascotasUnicas.values());

      console.log('✅ Historias clínicas:', historiasArray);
      console.log('✅ Mascotas:', mascotasArray);

      setHistoriasClinicas(historiasArray);
      setMascotas(mascotasArray);

      console.log('✅ Estado actualizado - Historias:', historiasArray.length, 'Mascotas:', mascotasArray.length);

      if (historiasArray.length === 0) {
        setError('No se encontraron historias clínicas para las mascotas de tus citas. Las mascotas necesitan tener una primera consulta registrada para crear su historia clínica.');
      }
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      console.log('🏁 Finalizando carga - setting loading = false');
      setLoading(false);
    }
  };

  const loadEvolucionesByHistoria = async (idHistoriaClinica) => {
    try {
      setError('');
      const response = await evolucionClinicaService.getByHistoriaClinica(idHistoriaClinica);
      console.log('✅ Evoluciones cargadas:', response.data);
      setEvoluciones(response.data || []);
    } catch (error) {
      console.error('❌ Error al cargar evoluciones:', error);
      setError(`Error al cargar evoluciones: ${error.response?.data?.message || error.message}`);
      setEvoluciones([]);
    }
  };

  const handleSelectHistoria = async (historia) => {
    setSelectedHistoria(historia);
    await loadEvolucionesByHistoria(historia.idHistoriaClinica);
  };

  const handleVerHistoria = async (historia) => {
    try {
      const response = await historiaClinicaService.getById(historia.idHistoriaClinica);
      setHistoriaDetalle(response.data);
      
      // Cargar las evoluciones de esta historia clínica
      await loadEvolucionesByHistoria(historia.idHistoriaClinica);
      
      setModalHistoriaOpen(true);
    } catch (error) {
      console.error('Error al cargar historia clínica:', error);
      setError('Error al cargar los detalles de la historia clínica');
    }
  };

  const handleOpenModal = () => {
    if (!selectedHistoria) {
      setError('Primero seleccione una historia clínica');
      return;
    }

    if (!veterinario?.idPersonal) {
      setError('No se encontró el perfil del veterinario');
      return;
    }

    setFormData({
      idHistoriaClinica: selectedHistoria.idHistoriaClinica,
      tipoEvolucion: 'CONSULTA',
      motivoConsulta: '',
      hallazgosExamen: '',
      diagnostico: '',
      planTratamiento: '',
      observaciones: '',
      peso: '',
      temperatura: '',
      frecuenciaCardiaca: '',
      frecuenciaRespiratoria: ''
    });
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.motivoConsulta || formData.motivoConsulta.trim().length < 10) {
      setError('El motivo de consulta debe tener al menos 10 caracteres');
      return;
    }

    if (!formData.hallazgosExamen || formData.hallazgosExamen.trim().length < 10) {
      setError('Los hallazgos del examen deben tener al menos 10 caracteres');
      return;
    }

    try {
      const data = {
        idHistoriaClinica: formData.idHistoriaClinica,
        idVeterinario: veterinario.idPersonal,
        tipoEvolucion: formData.tipoEvolucion,
        motivoConsulta: formData.motivoConsulta.trim(),
        hallazgosExamen: formData.hallazgosExamen.trim(),
        diagnostico: formData.diagnostico?.trim() || null,
        planTratamiento: formData.planTratamiento?.trim() || null,
        observaciones: formData.observaciones?.trim() || null,
        peso: formData.peso ? parseFloat(formData.peso) : null,
        temperatura: formData.temperatura ? parseFloat(formData.temperatura) : null,
        frecuenciaCardiaca: formData.frecuenciaCardiaca ? parseInt(formData.frecuenciaCardiaca) : null,
        frecuenciaRespiratoria: formData.frecuenciaRespiratoria ? parseInt(formData.frecuenciaRespiratoria) : null,
      };

      await evolucionClinicaService.create(formData.idHistoriaClinica, data);
      setSuccess('Evolución clínica registrada exitosamente');

      // Recargar evoluciones
      await loadEvolucionesByHistoria(formData.idHistoriaClinica);

      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar evolución:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const getMascotaInfo = (idHistoriaClinica) => {
    const historia = historiasClinicas.find(h => h.idHistoriaClinica === idHistoriaClinica);
    if (!historia) return null;

    // La mascota ya está enriquecida en la historia
    if (historia.mascota) {
      return historia.mascota;
    }

    // Fallback: buscar en la lista de mascotas
    const mascota = mascotas.find(m => m.idMascota === historia.idMascota);
    return mascota;
  };

  const historiasFiltradas = historiasClinicas.filter(historia => {
    const mascota = getMascotaInfo(historia.idHistoriaClinica);
    console.log('🔍 Filtrando historia:', historia.idHistoriaClinica, 'Mascota encontrada:', mascota);
    if (!mascota) {
      console.log('⚠️ No se encontró mascota para historia:', historia.idHistoriaClinica);
      return false;
    }

    const matches = mascota.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.apellidos?.toLowerCase().includes(searchTerm.toLowerCase());

    console.log('🔍 Mascota', mascota.nombre, 'matches search term "' + searchTerm + '":', matches);
    return matches;
  });

  console.log('✅ Historias filtradas final:', historiasFiltradas.length, 'de', historiasClinicas.length);
  console.log('🎨 Render - loading:', loading, 'historiasClinicas.length:', historiasClinicas.length, 'historiasFiltradas.length:', historiasFiltradas.length);

  if (loading) {
    console.log('⏳ Mostrando pantalla de carga...');
    return <div className="text-center py-8">Cargando datos...</div>;
  }

  console.log('🎨 Renderizando interfaz principal');

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Evoluciones Clínicas</h2>
        <button
          onClick={handleOpenModal}
          disabled={!selectedHistoria}
          className={`px-4 py-2 rounded-lg transition-colors inline-flex items-center gap-2 ${
            selectedHistoria
              ? 'bg-primary-600 text-white hover:bg-primary-700'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nueva Evolución
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

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Panel izquierdo - Historias clínicas */}
        <div className="lg:col-span-1 space-y-4">
          <div className="bg-white rounded-lg shadow p-4">
            <h3 className="font-semibold mb-3">Historias Clínicas</h3>

            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar mascota..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 mb-3"
            />

            <div className="space-y-2 max-h-[600px] overflow-y-auto">
              {historiasFiltradas.map((historia) => {
                const mascota = getMascotaInfo(historia.idHistoriaClinica);
                if (!mascota) return null;

                return (
                  <div
                    key={historia.idHistoriaClinica}
                    className={`p-3 rounded-lg border transition-colors ${
                      selectedHistoria?.idHistoriaClinica === historia.idHistoriaClinica
                        ? 'bg-primary-50 border-primary-500'
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    }`}
                  >
                    <div 
                      onClick={() => handleSelectHistoria(historia)}
                      className="cursor-pointer"
                    >
                      <div className="font-medium text-gray-900">{mascota.nombre}</div>
                      <div className="text-sm text-gray-600">
                        {mascota.propietario?.nombres} {mascota.propietario?.apellidos}
                      </div>
                      <div className="text-xs text-gray-500 mt-1">
                        {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                      </div>
                      <div className="text-xs text-gray-500">
                        HC: #{historia.idHistoriaClinica}
                      </div>
                    </div>
                    <div className="mt-2 pt-2 border-t border-gray-200">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleVerHistoria(historia);
                        }}
                        className="w-full flex items-center justify-center gap-2 px-3 py-1.5 text-sm text-primary-600 hover:bg-primary-50 rounded transition-colors"
                      >
                        <Eye className="w-4 h-4" />
                        Ver Historia Clínica
                      </button>
                    </div>
                  </div>
                );
              })}

              {historiasFiltradas.length === 0 && (
                <div className="text-center text-gray-500 py-4 text-sm">
                  No se encontraron historias clínicas
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Panel derecho - Evoluciones */}
        <div className="lg:col-span-2">
          {selectedHistoria ? (
            <div className="bg-white rounded-lg shadow">
              <div className="p-6 border-b">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold text-lg">
                      {getMascotaInfo(selectedHistoria.idHistoriaClinica)?.nombre}
                    </h3>
                    <p className="text-sm text-gray-600">
                      Historia Clínica #{selectedHistoria.idHistoriaClinica}
                    </p>
                    {selectedHistoria.motivoConsulta && (
                      <p className="text-sm text-gray-600 mt-1">
                        <strong>Motivo:</strong> {selectedHistoria.motivoConsulta}
                      </p>
                    )}
                  </div>
                </div>
              </div>

              <div className="p-6">
                <h4 className="font-semibold mb-4">Historial de Evoluciones</h4>

                {evoluciones.length > 0 ? (
                  <div className="space-y-4">
                    {/* Timeline de evoluciones */}
                    {evoluciones.map((evolucion, index) => (
                      <div key={evolucion.idEvolucionClinica || index} className="relative pl-8 pb-6 border-l-2 border-gray-200 last:border-0">
                        {/* Punto en la línea */}
                        <div className="absolute left-0 top-0 -ml-2 w-4 h-4 rounded-full bg-primary-600"></div>

                        <div className="bg-gray-50 p-4 rounded-lg">
                          <div className="flex justify-between items-start mb-2">
                            <div>
                              <div className="font-medium text-gray-900">
                                {evolucion.tipoEvolucion || 'Evolución'}
                              </div>
                              <div className="text-sm text-gray-600">
                                {evolucion.fechaEvolucion 
                                  ? new Date(evolucion.fechaEvolucion).toLocaleString('es-ES')
                                  : evolucion.fecha}
                              </div>
                              {evolucion.veterinario && (
                                <div className="text-sm text-gray-600">
                                  Dr./Dra. {evolucion.veterinario.nombreCompleto || 
                                    `${evolucion.veterinario.nombres || ''} ${evolucion.veterinario.apellidos || ''}`.trim()}
                                </div>
                              )}
                            </div>
                          </div>

                          {evolucion.motivoConsulta && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Motivo de Consulta:</div>
                              <div className="text-sm text-gray-900 mt-1 whitespace-pre-wrap">{evolucion.motivoConsulta}</div>
                            </div>
                          )}

                          {evolucion.hallazgosExamen && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Hallazgos del Examen:</div>
                              <div className="text-sm text-gray-900 mt-1 whitespace-pre-wrap">{evolucion.hallazgosExamen}</div>
                            </div>
                          )}

                          {evolucion.descripcion && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Descripción:</div>
                              <div className="text-sm text-gray-600 mt-1 whitespace-pre-wrap">{evolucion.descripcion}</div>
                            </div>
                          )}

                          {evolucion.signosVitales && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Signos Vitales:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.signosVitales}</div>
                            </div>
                          )}

                          {evolucion.diagnostico && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Diagnóstico:</div>
                              <div className="text-sm text-gray-900 mt-1 whitespace-pre-wrap">{evolucion.diagnostico}</div>
                            </div>
                          )}

                          {evolucion.planTratamiento && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Plan de Tratamiento:</div>
                              <div className="text-sm text-gray-900 mt-1 whitespace-pre-wrap">{evolucion.planTratamiento}</div>
                            </div>
                          )}

                          {(evolucion.peso || evolucion.temperatura || evolucion.frecuenciaCardiaca || evolucion.frecuenciaRespiratoria) && (
                            <div className="mt-3 grid grid-cols-2 gap-2 p-2 bg-white rounded">
                              {evolucion.peso && <p className="text-sm"><span className="font-medium">Peso:</span> {evolucion.peso} kg</p>}
                              {evolucion.temperatura && <p className="text-sm"><span className="font-medium">Temperatura:</span> {evolucion.temperatura} °C</p>}
                              {evolucion.frecuenciaCardiaca && <p className="text-sm"><span className="font-medium">FC:</span> {evolucion.frecuenciaCardiaca} bpm</p>}
                              {evolucion.frecuenciaRespiratoria && <p className="text-sm"><span className="font-medium">FR:</span> {evolucion.frecuenciaRespiratoria} rpm</p>}
                            </div>
                          )}

                          {evolucion.observaciones && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Observaciones:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.observaciones}</div>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center text-gray-500 py-8">
                    No hay evoluciones registradas para esta historia clínica.
                    <br />
                    <span className="text-sm">Haz clic en "Nueva Evolución" para agregar una.</span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
              <svg className="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p className="text-lg font-medium mb-2">Selecciona una Historia Clínica</p>
              <p className="text-sm">Selecciona una historia clínica del panel izquierdo para ver sus evoluciones</p>
            </div>
          )}
        </div>
      </div>

      {/* Modal de nueva evolución */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">Nueva Evolución Clínica</h3>

              <div className="mb-4 p-3 bg-blue-50 rounded-lg">
                <div className="text-sm">
                  <strong>Mascota:</strong> {getMascotaInfo(selectedHistoria?.idHistoriaClinica)?.nombre}
                  <br />
                  <strong>HC:</strong> #{selectedHistoria?.idHistoriaClinica}
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo de Evolución *
                  </label>
                  <select
                    required
                    value={formData.tipoEvolucion}
                    onChange={(e) => setFormData({...formData, tipoEvolucion: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="CONSULTA">Consulta</option>
                    <option value="CONTROL">Control</option>
                    <option value="EMERGENCIA">Emergencia</option>
                    <option value="SEGUIMIENTO">Seguimiento</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Motivo de Consulta *
                  </label>
                  <textarea
                    required
                    value={formData.motivoConsulta}
                    onChange={(e) => setFormData({...formData, motivoConsulta: e.target.value})}
                    rows="3"
                    placeholder="Motivo de la consulta o razón de la evolución..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    {formData.motivoConsulta.length} / 2000 caracteres
                    {formData.motivoConsulta && formData.motivoConsulta.trim().length < 10 && (
                      <span className="text-red-500 ml-2">
                        (faltan {10 - formData.motivoConsulta.trim().length} caracteres)
                      </span>
                    )}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Hallazgos del Examen *
                  </label>
                  <textarea
                    required
                    value={formData.hallazgosExamen}
                    onChange={(e) => setFormData({...formData, hallazgosExamen: e.target.value})}
                    rows="4"
                    placeholder="Hallazgos del examen físico o clínico realizado..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    {formData.hallazgosExamen.length} / 3000 caracteres
                    {formData.hallazgosExamen && formData.hallazgosExamen.trim().length < 10 && (
                      <span className="text-red-500 ml-2">
                        (faltan {10 - formData.hallazgosExamen.trim().length} caracteres)
                      </span>
                    )}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Diagnóstico
                  </label>
                  <textarea
                    value={formData.diagnostico}
                    onChange={(e) => setFormData({...formData, diagnostico: e.target.value})}
                    rows="3"
                    placeholder="Diagnóstico actual o impresión clínica..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Plan de Tratamiento
                  </label>
                  <textarea
                    value={formData.planTratamiento}
                    onChange={(e) => setFormData({...formData, planTratamiento: e.target.value})}
                    rows="3"
                    placeholder="Plan de tratamiento o próximos pasos..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Peso (kg)
                    </label>
                    <input
                      type="number"
                      step="0.1"
                      min="0.1"
                      value={formData.peso}
                      onChange={(e) => setFormData({...formData, peso: e.target.value})}
                      placeholder="Ej: 5.5"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Temperatura (°C)
                    </label>
                    <input
                      type="number"
                      step="0.1"
                      min="35"
                      max="45"
                      value={formData.temperatura}
                      onChange={(e) => setFormData({...formData, temperatura: e.target.value})}
                      placeholder="Ej: 38.5"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Frecuencia Cardíaca (bpm)
                    </label>
                    <input
                      type="number"
                      min="20"
                      max="300"
                      value={formData.frecuenciaCardiaca}
                      onChange={(e) => setFormData({...formData, frecuenciaCardiaca: e.target.value})}
                      placeholder="Ej: 120"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Frecuencia Respiratoria (rpm)
                    </label>
                    <input
                      type="number"
                      min="5"
                      max="100"
                      value={formData.frecuenciaRespiratoria}
                      onChange={(e) => setFormData({...formData, frecuenciaRespiratoria: e.target.value})}
                      placeholder="Ej: 20"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observaciones
                  </label>
                  <textarea
                    value={formData.observaciones}
                    onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                    rows="2"
                    placeholder="Observaciones adicionales..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
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
                    onClick={() => setModalOpen(false)}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={!formData.motivoConsulta || !formData.hallazgosExamen}
                    className={`px-4 py-2 rounded-lg ${
                      formData.motivoConsulta && formData.hallazgosExamen
                        ? 'bg-primary-600 text-white hover:bg-primary-700'
                        : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    }`}
                  >
                    Guardar Evolución
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Historia Clínica */}
      {modalHistoriaOpen && historiaDetalle && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-bold">
                  Historia Clínica - {getMascotaInfo(historiaDetalle.idHistoriaClinica)?.nombre || 'N/A'}
                </h3>
                <button
                  onClick={() => setModalHistoriaOpen(false)}
                  className="text-gray-500 hover:text-gray-700"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Alergias:</label>
                    <p className="text-sm text-gray-900 mt-1">{historiaDetalle.alergias || 'N/A'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Enfermedades Crónicas:</label>
                    <p className="text-sm text-gray-900 mt-1">{historiaDetalle.enfermedadesCronicas || 'N/A'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Cirugías Previas:</label>
                    <p className="text-sm text-gray-900 mt-1">{historiaDetalle.cirugiasPrevias || 'N/A'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Medicamentos Actuales:</label>
                    <p className="text-sm text-gray-900 mt-1">{historiaDetalle.medicamentosActuales || 'N/A'}</p>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Observaciones:</label>
                  <p className="text-sm text-gray-900 mt-1 whitespace-pre-wrap">{historiaDetalle.observaciones || 'N/A'}</p>
                </div>

                <div className="pt-4 border-t">
                  <h4 className="font-semibold mb-3">Evoluciones Clínicas ({evoluciones.length})</h4>
                  {evoluciones.length > 0 ? (
                    <div className="space-y-4 max-h-96 overflow-y-auto">
                      {evoluciones.map((evolucion, index) => (
                        <div key={evolucion.idEvolucionClinica || index} className="border-l-4 border-primary-500 pl-4 py-2 bg-gray-50 rounded-r-lg p-3">
                          <div className="flex justify-between items-start mb-2">
                            <div>
                              <div className="font-medium text-sm text-gray-900">
                                {evolucion.tipoEvolucion || 'Evolución'}
                              </div>
                              <div className="text-xs text-gray-600">
                                {evolucion.fechaEvolucion 
                                  ? new Date(evolucion.fechaEvolucion).toLocaleString('es-ES')
                                  : evolucion.fecha}
                              </div>
                              {evolucion.veterinario && (
                                <div className="text-xs text-gray-600">
                                  Dr./Dra. {evolucion.veterinario.nombreCompleto || 
                                    `${evolucion.veterinario.nombres || ''} ${evolucion.veterinario.apellidos || ''}`.trim()}
                                </div>
                              )}
                            </div>
                          </div>

                          {evolucion.motivoConsulta && (
                            <div className="mt-2">
                              <div className="text-xs font-medium text-gray-700">Motivo de Consulta:</div>
                              <div className="text-xs text-gray-900 whitespace-pre-wrap">{evolucion.motivoConsulta}</div>
                            </div>
                          )}

                          {evolucion.hallazgosExamen && (
                            <div className="mt-2">
                              <div className="text-xs font-medium text-gray-700">Hallazgos del Examen:</div>
                              <div className="text-xs text-gray-900 whitespace-pre-wrap">{evolucion.hallazgosExamen}</div>
                            </div>
                          )}

                          {evolucion.diagnostico && (
                            <div className="mt-2">
                              <div className="text-xs font-medium text-gray-700">Diagnóstico:</div>
                              <div className="text-xs text-gray-900 whitespace-pre-wrap">{evolucion.diagnostico}</div>
                            </div>
                          )}

                          {evolucion.planTratamiento && (
                            <div className="mt-2">
                              <div className="text-xs font-medium text-gray-700">Plan de Tratamiento:</div>
                              <div className="text-xs text-gray-900 whitespace-pre-wrap">{evolucion.planTratamiento}</div>
                            </div>
                          )}

                          {(evolucion.peso || evolucion.temperatura || evolucion.frecuenciaCardiaca || evolucion.frecuenciaRespiratoria) && (
                            <div className="mt-2 grid grid-cols-2 gap-2 p-2 bg-white rounded text-xs">
                              {evolucion.peso && <p><span className="font-medium">Peso:</span> {evolucion.peso} kg</p>}
                              {evolucion.temperatura && <p><span className="font-medium">Temperatura:</span> {evolucion.temperatura} °C</p>}
                              {evolucion.frecuenciaCardiaca && <p><span className="font-medium">FC:</span> {evolucion.frecuenciaCardiaca} bpm</p>}
                              {evolucion.frecuenciaRespiratoria && <p><span className="font-medium">FR:</span> {evolucion.frecuenciaRespiratoria} rpm</p>}
                            </div>
                          )}

                          {evolucion.observaciones && (
                            <div className="mt-2">
                              <div className="text-xs font-medium text-gray-700">Observaciones:</div>
                              <div className="text-xs text-gray-600">{evolucion.observaciones}</div>
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-sm text-gray-500">No hay evoluciones registradas</p>
                  )}
                </div>
              </div>

              <div className="mt-6 flex justify-end">
                <button
                  onClick={() => setModalHistoriaOpen(false)}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                >
                  Cerrar
                </button>
              </div>
            </div>
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
            <h4 className="font-semibold text-blue-900 mb-1">Cómo usar Evoluciones Clínicas</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Selecciona una historia clínica del panel izquierdo</li>
              <li>• Haz clic en "Nueva Evolución" para registrar el seguimiento del paciente</li>
              <li>• Las evoluciones se muestran en orden cronológico en formato timeline</li>
              <li>• Registra signos vitales, diagnóstico y tratamiento en cada evolución</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
