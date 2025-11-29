import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { historiaClinicaService, evolucionClinicaService, citaService, veterinarioService } from '../../services/api';
import { Eye, Edit, Plus, X } from 'lucide-react';

export default function HistoriasClinicasPage() {
  const { user } = useAuth();
  const [historias, setHistorias] = useState([]);
  const [historiaSeleccionada, setHistoriaSeleccionada] = useState(null);
  const [evoluciones, setEvoluciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingEvoluciones, setLoadingEvoluciones] = useState(false);
  const [error, setError] = useState('');
  const [mostrarModalHistoria, setMostrarModalHistoria] = useState(false);
  const [mostrarModalEvolucion, setMostrarModalEvolucion] = useState(false);
  const [editandoHistoria, setEditandoHistoria] = useState(false);
  const [veterinario, setVeterinario] = useState(null);
  const [formHistoria, setFormHistoria] = useState({
    numeroHistoria: '',
    grupoSanguineo: '',
    alergias: '',
    enfermedadesCronicas: '',
    cirugiasPrevias: '',
    medicamentosActuales: '',
    observaciones: '',
  });
  const [formEvolucion, setFormEvolucion] = useState({
    tipoEvolucion: 'CONSULTA',
    motivoConsulta: '',
    hallazgosExamen: '',
    diagnostico: '',
    planTratamiento: '',
    observaciones: '',
    peso: '',
    temperatura: '',
    frecuenciaCardiaca: '',
    frecuenciaRespiratoria: '',
  });

  useEffect(() => {
    loadHistorias();
    loadVeterinario();
  }, []);

  const loadVeterinario = async () => {
    try {
      console.log('🔍 Obteniendo perfil del veterinario...');
      console.log('👤 Usuario actual:', user);
      
      let vet = null;
      
      // Intentar obtener el perfil usando el endpoint mi-perfil
      try {
        console.log('📞 Intentando obtener perfil con obtenerMiPerfil()...');
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        vet = veterinarioRes.data;
        console.log('✅ Veterinario obtenido con obtenerMiPerfil:', vet);
      } catch (error404) {
        console.log('⚠️ obtenerMiPerfil falló, buscando manualmente...', error404);
        // Si no existe el endpoint, buscar manualmente
        try {
          const todosVeterinarios = await veterinarioService.getAll();
          console.log('📋 Total de veterinarios encontrados:', todosVeterinarios.data?.length || 0);
          
          // Buscar por email (correo)
          vet = todosVeterinarios.data.find(v => {
            const match = v.correo && v.correo.toLowerCase() === user?.email?.toLowerCase();
            console.log(`🔍 Comparando email (correo): "${v.correo}" === "${user?.email}" = ${match}`);
            return match;
          });

          // Buscar por email del usuario asociado
          if (!vet) {
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario?.email && v.usuario.email.toLowerCase() === user?.email?.toLowerCase();
              console.log(`🔍 Comparando email (usuario.email): "${v.usuario?.email}" === "${user?.email}" = ${match}`);
              return match;
            });
          }

          // Buscar por idUsuario
          if (!vet && user?.idUsuario) {
            console.log(`🔍 Buscando por idUsuario: ${user.idUsuario}`);
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario && v.usuario.idUsuario === user.idUsuario;
              console.log(`🔍 Comparando usuario: v.usuario.idUsuario=${v.usuario?.idUsuario} === ${user.idUsuario} = ${match}`);
              return match;
            });
          }

          // Buscar por username
          if (!vet && user?.username) {
            console.log(`🔍 Buscando por username: ${user.username}`);
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario?.username && v.usuario.username === user.username;
              console.log(`🔍 Comparando username: v.usuario.username="${v.usuario?.username}" === "${user.username}" = ${match}`);
              return match;
            });
          }

          // Último recurso: buscar por similitud de nombre
          if (!vet && (user?.nombre || user?.username)) {
            console.log('🔍 Buscando por similitud de nombre (último recurso)...');
            const nombreUsuario = (user.nombre || user.username || '').toLowerCase().split(' ')[0];
            vet = todosVeterinarios.data.find(v => {
              const match = v.nombres && v.nombres.toLowerCase().includes(nombreUsuario);
              console.log(`🔍 Comparando nombre: v.nombres="${v.nombres}" incluye "${nombreUsuario}" = ${match}`);
              return match;
            });
          }
          
          if (vet) {
            console.log('✅ Veterinario encontrado manualmente:', vet);
          } else {
            console.error('❌ No se encontró veterinario con ningún método');
          }
        } catch (error2) {
          console.error('❌ Error al buscar veterinarios:', error2);
        }
      }
      
      if (vet && vet.idPersonal) {
        setVeterinario(vet);
      } else {
        console.error('❌ No se pudo encontrar el veterinario o no tiene idPersonal');
        setError('No se encontró el perfil del veterinario');
      }
    } catch (error) {
      console.error('Error al cargar veterinario:', error);
      setError('Error al cargar el perfil del veterinario');
    }
  };

  const loadHistorias = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Cargar historias clínicas desde las citas del veterinario
      let veterinarioData = null;
      try {
        console.log('📞 Intentando obtener perfil con obtenerMiPerfil()...');
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        veterinarioData = veterinarioRes.data;
        console.log('✅ Veterinario obtenido con obtenerMiPerfil:', veterinarioData);
      } catch (error404) {
        console.log('⚠️ obtenerMiPerfil falló, buscando manualmente...', error404);
        try {
          const todosVeterinarios = await veterinarioService.getAll();
          console.log('📋 Total de veterinarios encontrados:', todosVeterinarios.data?.length || 0);
          
          // Buscar por email (correo)
          veterinarioData = todosVeterinarios.data.find(v => {
            const match = v.correo && v.correo.toLowerCase() === user?.email?.toLowerCase();
            console.log(`🔍 Comparando email (correo): "${v.correo}" === "${user?.email}" = ${match}`);
            return match;
          });

          // Buscar por email del usuario asociado
          if (!veterinarioData) {
            veterinarioData = todosVeterinarios.data.find(v => {
              const match = v.usuario?.email && v.usuario.email.toLowerCase() === user?.email?.toLowerCase();
              console.log(`🔍 Comparando email (usuario.email): "${v.usuario?.email}" === "${user?.email}" = ${match}`);
              return match;
            });
          }

          // Buscar por idUsuario
          if (!veterinarioData && user?.idUsuario) {
            console.log(`🔍 Buscando por idUsuario: ${user.idUsuario}`);
            veterinarioData = todosVeterinarios.data.find(v => {
              const match = v.usuario && v.usuario.idUsuario === user.idUsuario;
              console.log(`🔍 Comparando usuario: v.usuario.idUsuario=${v.usuario?.idUsuario} === ${user.idUsuario} = ${match}`);
              return match;
            });
          }

          // Buscar por username
          if (!veterinarioData && user?.username) {
            console.log(`🔍 Buscando por username: ${user.username}`);
            veterinarioData = todosVeterinarios.data.find(v => {
              const match = v.usuario?.username && v.usuario.username === user.username;
              console.log(`🔍 Comparando username: v.usuario.username="${v.usuario?.username}" === "${user.username}" = ${match}`);
              return match;
            });
          }

          // Último recurso: buscar por similitud de nombre
          if (!veterinarioData && (user?.nombre || user?.username)) {
            console.log('🔍 Buscando por similitud de nombre (último recurso)...');
            const nombreUsuario = (user.nombre || user.username || '').toLowerCase().split(' ')[0];
            veterinarioData = todosVeterinarios.data.find(v => {
              const match = v.nombres && v.nombres.toLowerCase().includes(nombreUsuario);
              console.log(`🔍 Comparando nombre: v.nombres="${v.nombres}" incluye "${nombreUsuario}" = ${match}`);
              return match;
            });
          }
          
          if (veterinarioData) {
            console.log('✅ Veterinario encontrado manualmente:', veterinarioData);
          } else {
            console.error('❌ No se encontró veterinario con ningún método');
          }
        } catch (error2) {
          console.error('❌ Error al buscar veterinarios:', error2);
        }
      }

      if (!veterinarioData?.idPersonal) {
        setError('No se encontró el perfil del veterinario');
        setLoading(false);
        return;
      }

      // Obtener citas del veterinario
      const citasRes = await citaService.getByVeterinario(veterinarioData.idPersonal);
      const citas = citasRes.data || [];
      
      // Obtener historias clínicas únicas de las mascotas de las citas
      const historiasUnicas = new Map();
      for (const cita of citas) {
        if (cita.mascota?.idMascota) {
          try {
            const historiaRes = await historiaClinicaService.getByMascota(cita.mascota.idMascota);
            const historia = historiaRes.data;
            if (historia && !historiasUnicas.has(historia.idHistoriaClinica)) {
              historiasUnicas.set(historia.idHistoriaClinica, historia);
            }
          } catch (error) {
            // La mascota puede no tener historia clínica aún
            console.log(`Mascota ${cita.mascota.idMascota} no tiene historia clínica`);
          }
        }
      }
      
      setHistorias(Array.from(historiasUnicas.values()));
    } catch (error) {
      console.error('Error al cargar historias clínicas:', error);
      setError('Error al cargar las historias clínicas');
    } finally {
      setLoading(false);
    }
  };

  const loadEvoluciones = async (idHistoriaClinica) => {
    try {
      setLoadingEvoluciones(true);
      const response = await evolucionClinicaService.getByHistoriaClinica(idHistoriaClinica);
      setEvoluciones(response.data || []);
    } catch (error) {
      console.error('Error al cargar evoluciones:', error);
      setError('Error al cargar las evoluciones');
    } finally {
      setLoadingEvoluciones(false);
    }
  };

  const handleVerHistoria = async (historia) => {
    setHistoriaSeleccionada(historia);
    setFormHistoria({
      numeroHistoria: historia.numeroHistoria || '',
      grupoSanguineo: historia.grupoSanguineo || '',
      alergias: historia.alergias || '',
      enfermedadesCronicas: historia.enfermedadesCronicas || '',
      cirugiasPrevias: historia.cirugiasPrevias || '',
      medicamentosActuales: historia.medicamentosActuales || '',
      observaciones: historia.observaciones || '',
    });
    setEditandoHistoria(false);
    setMostrarModalHistoria(true);
    await loadEvoluciones(historia.idHistoriaClinica);
  };

  const handleEditarHistoria = () => {
    setEditandoHistoria(true);
  };

  const handleGuardarHistoria = async () => {
    try {
      const data = {
        idMascota: historiaSeleccionada.mascota.idMascota,
        ...formHistoria,
      };
      await historiaClinicaService.update(historiaSeleccionada.idHistoriaClinica, data);
      await loadHistorias();
      // Actualizar la historia seleccionada
      const historiaActualizada = await historiaClinicaService.getById(historiaSeleccionada.idHistoriaClinica);
      setHistoriaSeleccionada(historiaActualizada.data);
      setEditandoHistoria(false);
      setError('');
      alert('Historia clínica actualizada exitosamente');
    } catch (error) {
      console.error('Error al guardar historia:', error);
      setError(`Error al guardar: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleNuevaEvolucion = () => {
    setFormEvolucion({
      tipoEvolucion: 'CONSULTA',
      motivoConsulta: '',
      hallazgosExamen: '',
      diagnostico: '',
      planTratamiento: '',
      observaciones: '',
      peso: '',
      temperatura: '',
      frecuenciaCardiaca: '',
      frecuenciaRespiratoria: '',
    });
    setMostrarModalEvolucion(true);
  };

  const handleGuardarEvolucion = async () => {
    try {
      if (!veterinario?.idPersonal) {
        setError('No se encontró el perfil del veterinario');
        return;
      }

      // Validaciones de campos requeridos
      if (!formEvolucion.motivoConsulta || formEvolucion.motivoConsulta.trim().length < 10) {
        setError('El motivo de consulta debe tener al menos 10 caracteres');
        return;
      }

      if (!formEvolucion.hallazgosExamen || formEvolucion.hallazgosExamen.trim().length < 10) {
        setError('Los hallazgos del examen deben tener al menos 10 caracteres');
        return;
      }

      const data = {
        idHistoriaClinica: historiaSeleccionada.idHistoriaClinica,
        idVeterinario: veterinario.idPersonal,
        tipoEvolucion: formEvolucion.tipoEvolucion,
        motivoConsulta: formEvolucion.motivoConsulta.trim(),
        hallazgosExamen: formEvolucion.hallazgosExamen.trim(),
        diagnostico: formEvolucion.diagnostico?.trim() || null,
        planTratamiento: formEvolucion.planTratamiento?.trim() || null,
        observaciones: formEvolucion.observaciones?.trim() || null,
        peso: formEvolucion.peso ? parseFloat(formEvolucion.peso) : null,
        temperatura: formEvolucion.temperatura ? parseFloat(formEvolucion.temperatura) : null,
        frecuenciaCardiaca: formEvolucion.frecuenciaCardiaca ? parseInt(formEvolucion.frecuenciaCardiaca) : null,
        frecuenciaRespiratoria: formEvolucion.frecuenciaRespiratoria ? parseInt(formEvolucion.frecuenciaRespiratoria) : null,
      };

      console.log('📤 Enviando evolución con data:', data);

      await evolucionClinicaService.create(historiaSeleccionada.idHistoriaClinica, data);
      await loadEvoluciones(historiaSeleccionada.idHistoriaClinica);
      setMostrarModalEvolucion(false);
      setError('');
      alert('Evolución clínica creada exitosamente');
    } catch (error) {
      console.error('Error al guardar evolución:', error);
      console.error('Error response:', error.response?.data);

      let errorMessage = 'Error al guardar evolución';

      if (error.response?.data) {
        const errorData = error.response.data;

        // Si hay errores de validación como lista
        if (Array.isArray(errorData.errors) && errorData.errors.length > 0) {
          errorMessage = `Errores de validación: ${errorData.errors.join(', ')}`;
        }
        // Si hay errores de validación como mapa
        else if (errorData.validationErrors && typeof errorData.validationErrors === 'object') {
          const validationErrors = Object.entries(errorData.validationErrors)
            .map(([field, message]) => `${field}: ${message}`)
            .join(', ');
          errorMessage = `Errores de validación: ${validationErrors}`;
        }
        // Si hay un mensaje directo
        else if (errorData.message) {
          errorMessage = errorData.message;
        }
      } else if (error.message) {
        errorMessage = error.message;
      }

      setError(errorMessage);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando historias clínicas...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Historias Clínicas de Mis Pacientes</h2>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      <div className="bg-white rounded-lg shadow overflow-hidden">
        {historias.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Mascota</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Propietario</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Número</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Evoluciones</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {historias.map((historia) => (
                  <tr key={historia.idHistoriaClinica} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {historia.mascota?.nombre}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {historia.mascota?.propietarioNombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{historia.numeroHistoria}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {historia.cantidadEvoluciones || 0}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleVerHistoria(historia)}
                        className="text-primary-600 hover:text-primary-900 mr-3"
                        title="Ver Historia Clínica"
                      >
                        <Eye className="w-5 h-5 inline" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="p-8 text-center text-gray-500">
            No hay historias clínicas disponibles
          </div>
        )}
      </div>

      {/* Modal de Historia Clínica */}
      {mostrarModalHistoria && historiaSeleccionada && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b sticky top-0 bg-white z-10">
              <div className="flex justify-between items-center">
                <h3 className="text-xl font-bold">
                  Historia Clínica - {historiaSeleccionada.mascota?.nombre}
                </h3>
                <div className="flex gap-2">
                  {!editandoHistoria && (
                    <button
                      onClick={handleEditarHistoria}
                      className="text-blue-600 hover:text-blue-900"
                      title="Editar"
                    >
                      <Edit className="w-5 h-5" />
                    </button>
                  )}
                  <button
                    onClick={() => {
                      setMostrarModalHistoria(false);
                      setEditandoHistoria(false);
                    }}
                    className="text-gray-600 hover:text-gray-900"
                    title="Cerrar"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>

            <div className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Número de Historia</label>
                  {editandoHistoria ? (
                    <input
                      type="text"
                      value={formHistoria.numeroHistoria}
                      onChange={(e) => setFormHistoria({ ...formHistoria, numeroHistoria: e.target.value })}
                      className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                      required
                    />
                  ) : (
                    <p className="mt-1 text-sm text-gray-900">{historiaSeleccionada.numeroHistoria}</p>
                  )}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Grupo Sanguíneo</label>
                  {editandoHistoria ? (
                    <input
                      type="text"
                      value={formHistoria.grupoSanguineo}
                      onChange={(e) => setFormHistoria({ ...formHistoria, grupoSanguineo: e.target.value })}
                      className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                    />
                  ) : (
                    <p className="mt-1 text-sm text-gray-900">{historiaSeleccionada.grupoSanguineo || 'N/A'}</p>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Alergias</label>
                {editandoHistoria ? (
                  <textarea
                    value={formHistoria.alergias}
                    onChange={(e) => setFormHistoria({ ...formHistoria, alergias: e.target.value })}
                    rows={3}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  />
                ) : (
                  <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{historiaSeleccionada.alergias || 'N/A'}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Enfermedades Crónicas</label>
                {editandoHistoria ? (
                  <textarea
                    value={formHistoria.enfermedadesCronicas}
                    onChange={(e) => setFormHistoria({ ...formHistoria, enfermedadesCronicas: e.target.value })}
                    rows={3}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  />
                ) : (
                  <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{historiaSeleccionada.enfermedadesCronicas || 'N/A'}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Cirugías Previas</label>
                {editandoHistoria ? (
                  <textarea
                    value={formHistoria.cirugiasPrevias}
                    onChange={(e) => setFormHistoria({ ...formHistoria, cirugiasPrevias: e.target.value })}
                    rows={3}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  />
                ) : (
                  <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{historiaSeleccionada.cirugiasPrevias || 'N/A'}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Medicamentos Actuales</label>
                {editandoHistoria ? (
                  <textarea
                    value={formHistoria.medicamentosActuales}
                    onChange={(e) => setFormHistoria({ ...formHistoria, medicamentosActuales: e.target.value })}
                    rows={3}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  />
                ) : (
                  <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{historiaSeleccionada.medicamentosActuales || 'N/A'}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                {editandoHistoria ? (
                  <textarea
                    value={formHistoria.observaciones}
                    onChange={(e) => setFormHistoria({ ...formHistoria, observaciones: e.target.value })}
                    rows={4}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  />
                ) : (
                  <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{historiaSeleccionada.observaciones || 'N/A'}</p>
                )}
              </div>

              {editandoHistoria && (
                <div className="flex gap-2 pt-4 border-t">
                  <button
                    onClick={handleGuardarHistoria}
                    className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
                  >
                    Guardar Cambios
                  </button>
                  <button
                    onClick={() => {
                      setEditandoHistoria(false);
                      // Restaurar valores originales
                      setFormHistoria({
                        numeroHistoria: historiaSeleccionada.numeroHistoria || '',
                        grupoSanguineo: historiaSeleccionada.grupoSanguineo || '',
                        alergias: historiaSeleccionada.alergias || '',
                        enfermedadesCronicas: historiaSeleccionada.enfermedadesCronicas || '',
                        cirugiasPrevias: historiaSeleccionada.cirugiasPrevias || '',
                        medicamentosActuales: historiaSeleccionada.medicamentosActuales || '',
                        observaciones: historiaSeleccionada.observaciones || '',
                      });
                    }}
                    className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors"
                  >
                    Cancelar
                  </button>
                </div>
              )}

              {/* Sección de Evoluciones */}
              <div className="mt-6 border-t pt-6">
                <div className="flex justify-between items-center mb-4">
                  <h4 className="text-lg font-semibold">Evoluciones Clínicas</h4>
                  <button
                    onClick={handleNuevaEvolucion}
                    className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
                  >
                    <Plus className="w-4 h-4" />
                    Nueva Evolución
                  </button>
                </div>

                {loadingEvoluciones ? (
                  <p className="text-center text-gray-500">Cargando evoluciones...</p>
                ) : evoluciones.length > 0 ? (
                  <div className="space-y-4">
                    {evoluciones.map((evolucion) => (
                      <div key={evolucion.idEvolucion} className="border rounded-lg p-4 bg-gray-50">
                        <div className="flex justify-between items-start mb-2">
                          <div>
                            <p className="font-semibold text-primary-600">{evolucion.tipoEvolucion}</p>
                            <p className="text-sm text-gray-600">
                              {new Date(evolucion.fechaEvolucion).toLocaleString('es-ES')}
                            </p>
                            {evolucion.veterinario && (
                              <p className="text-sm text-gray-600">
                                Dr./Dra. {evolucion.veterinario.nombreCompleto}
                              </p>
                            )}
                          </div>
                        </div>
                        <div className="mt-2 space-y-2">
                          <div>
                            <p className="text-sm font-medium text-gray-700">Motivo de Consulta:</p>
                            <p className="text-sm text-gray-900 whitespace-pre-wrap">
                              {evolucion.motivoConsulta || 'No especificado'}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm font-medium text-gray-700">Hallazgos del Examen:</p>
                            <p className="text-sm text-gray-900 whitespace-pre-wrap">
                              {evolucion.hallazgosExamen || 'No especificado'}
                            </p>
                          </div>
                          {evolucion.diagnostico && (
                            <div>
                              <p className="text-sm font-medium text-gray-700">Diagnóstico:</p>
                              <p className="text-sm text-gray-900 whitespace-pre-wrap">{evolucion.diagnostico}</p>
                            </div>
                          )}
                          {evolucion.planTratamiento && (
                            <div>
                              <p className="text-sm font-medium text-gray-700">Plan de Tratamiento:</p>
                              <p className="text-sm text-gray-900 whitespace-pre-wrap">{evolucion.planTratamiento}</p>
                            </div>
                          )}
                          {(evolucion.peso || evolucion.temperatura || evolucion.frecuenciaCardiaca || evolucion.frecuenciaRespiratoria) && (
                            <div className="grid grid-cols-2 gap-2 mt-2 p-2 bg-white rounded">
                              {evolucion.peso && <p className="text-sm"><span className="font-medium">Peso:</span> {evolucion.peso} kg</p>}
                              {evolucion.temperatura && <p className="text-sm"><span className="font-medium">Temperatura:</span> {evolucion.temperatura} °C</p>}
                              {evolucion.frecuenciaCardiaca && <p className="text-sm"><span className="font-medium">FC:</span> {evolucion.frecuenciaCardiaca} bpm</p>}
                              {evolucion.frecuenciaRespiratoria && <p className="text-sm"><span className="font-medium">FR:</span> {evolucion.frecuenciaRespiratoria} rpm</p>}
                            </div>
                          )}
                          {evolucion.observaciones && (
                            <div>
                              <p className="text-sm font-medium text-gray-700">Observaciones:</p>
                              <p className="text-sm text-gray-900 whitespace-pre-wrap">{evolucion.observaciones}</p>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-center text-gray-500">No hay evoluciones registradas</p>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Nueva Evolución */}
      {mostrarModalEvolucion && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b sticky top-0 bg-white z-10">
              <div className="flex justify-between items-center">
                <h3 className="text-xl font-bold">Nueva Evolución Clínica</h3>
                <button
                  onClick={() => setMostrarModalEvolucion(false)}
                  className="text-gray-600 hover:text-gray-900"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Tipo de Evolución *</label>
                <select
                  value={formEvolucion.tipoEvolucion}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, tipoEvolucion: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                >
                  <option value="CONSULTA">Consulta</option>
                  <option value="CONTROL">Control</option>
                  <option value="URGENCIA">Urgencia</option>
                  <option value="SEGUIMIENTO">Seguimiento</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Motivo de Consulta *
                  <span className="text-xs text-gray-500 ml-2">(mínimo 10 caracteres)</span>
                </label>
                <textarea
                  value={formEvolucion.motivoConsulta}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, motivoConsulta: e.target.value })}
                  rows={3}
                  required
                  className={`mt-1 block w-full rounded-md shadow-sm focus:border-primary-500 focus:ring-primary-500 ${
                    formEvolucion.motivoConsulta && formEvolucion.motivoConsulta.trim().length < 10
                      ? 'border-red-300'
                      : 'border-gray-300'
                  }`}
                  placeholder="Describa el motivo de la consulta (mínimo 10 caracteres)..."
                />
                <p className="mt-1 text-xs text-gray-500">
                  {formEvolucion.motivoConsulta.length} / 2000 caracteres
                  {formEvolucion.motivoConsulta && formEvolucion.motivoConsulta.trim().length < 10 && (
                    <span className="text-red-600 ml-2">
                      (faltan {10 - formEvolucion.motivoConsulta.trim().length} caracteres)
                    </span>
                  )}
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Hallazgos del Examen *
                  <span className="text-xs text-gray-500 ml-2">(mínimo 10 caracteres)</span>
                </label>
                <textarea
                  value={formEvolucion.hallazgosExamen}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, hallazgosExamen: e.target.value })}
                  rows={4}
                  required
                  className={`mt-1 block w-full rounded-md shadow-sm focus:border-primary-500 focus:ring-primary-500 ${
                    formEvolucion.hallazgosExamen && formEvolucion.hallazgosExamen.trim().length < 10
                      ? 'border-red-300'
                      : 'border-gray-300'
                  }`}
                  placeholder="Describa los hallazgos del examen físico (mínimo 10 caracteres)..."
                />
                <p className="mt-1 text-xs text-gray-500">
                  {formEvolucion.hallazgosExamen.length} / 3000 caracteres
                  {formEvolucion.hallazgosExamen && formEvolucion.hallazgosExamen.trim().length < 10 && (
                    <span className="text-red-600 ml-2">
                      (faltan {10 - formEvolucion.hallazgosExamen.trim().length} caracteres)
                    </span>
                  )}
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Diagnóstico</label>
                <textarea
                  value={formEvolucion.diagnostico}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, diagnostico: e.target.value })}
                  rows={3}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  placeholder="Diagnóstico (opcional)..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Plan de Tratamiento</label>
                <textarea
                  value={formEvolucion.planTratamiento}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, planTratamiento: e.target.value })}
                  rows={4}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  placeholder="Plan de tratamiento (opcional)..."
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Peso (kg)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formEvolucion.peso}
                    onChange={(e) => setFormEvolucion({ ...formEvolucion, peso: e.target.value })}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                    placeholder="0.0"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Temperatura (°C)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formEvolucion.temperatura}
                    onChange={(e) => setFormEvolucion({ ...formEvolucion, temperatura: e.target.value })}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                    placeholder="0.0"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Frecuencia Cardíaca (bpm)</label>
                  <input
                    type="number"
                    value={formEvolucion.frecuenciaCardiaca}
                    onChange={(e) => setFormEvolucion({ ...formEvolucion, frecuenciaCardiaca: e.target.value })}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                    placeholder="0"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Frecuencia Respiratoria (rpm)</label>
                  <input
                    type="number"
                    value={formEvolucion.frecuenciaRespiratoria}
                    onChange={(e) => setFormEvolucion({ ...formEvolucion, frecuenciaRespiratoria: e.target.value })}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                    placeholder="0"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                <textarea
                  value={formEvolucion.observaciones}
                  onChange={(e) => setFormEvolucion({ ...formEvolucion, observaciones: e.target.value })}
                  rows={3}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
                  placeholder="Observaciones adicionales (opcional)..."
                />
              </div>

              <div className="flex gap-2 pt-4 border-t">
                <button
                  onClick={handleGuardarEvolucion}
                  className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
                  disabled={!formEvolucion.motivoConsulta || !formEvolucion.hallazgosExamen}
                >
                  Guardar Evolución
                </button>
                <button
                  onClick={() => setMostrarModalEvolucion(false)}
                  className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
