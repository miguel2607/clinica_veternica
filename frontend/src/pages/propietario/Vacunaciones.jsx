import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { vacunacionService, mascotaService, propietarioService, historiaClinicaService, citaService, veterinarioService, servicioService, horarioService } from '../../services/api';
import { Plus, Search, Eye, Calendar, Syringe, Clock, User, PawPrint, Scissors, CheckCircle, AlertCircle } from 'lucide-react';
import Modal from '../../components/Modal';
import { useNavigate } from 'react-router-dom';

export default function VacunacionesPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [vacunaciones, setVacunaciones] = useState([]);
  const [citasVacunacion, setCitasVacunacion] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedVacunacion, setSelectedVacunacion] = useState(null);
  
  // Estados para el flujo de agendar vacunación (paso a paso)
  const [mostrarAgendarVacunacion, setMostrarAgendarVacunacion] = useState(false);
  const [step, setStep] = useState(1); // 1: Mascota, 2: Servicio, 3: Fecha y Hora, 4: Confirmación
  const [propietario, setPropietario] = useState(null);
  const [horarios, setHorarios] = useState([]);
  const [mascotaSeleccionada, setMascotaSeleccionada] = useState(null);
  const [servicioSeleccionado, setServicioSeleccionado] = useState(null);
  const [veterinarioSeleccionado, setVeterinarioSeleccionado] = useState(null);
  const [fechaSeleccionada, setFechaSeleccionada] = useState('');
  const [horaSeleccionada, setHoraSeleccionada] = useState('');
  const [motivo, setMotivo] = useState('Vacunación');
  const [disponibilidad, setDisponibilidad] = useState(null);
  const [loadingDisponibilidad, setLoadingDisponibilidad] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, [user]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener propietario y sus mascotas
      let propietarioData = null;
      if (user?.rol === 'PROPIETARIO') {
        try {
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          propietarioData = propietarioRes.data;
          setPropietario(propietarioData);
        } catch (error) {
          console.error('Error al obtener propietario:', error);
        }
      } else if (user?.email) {
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          propietarioData = propietarioRes.data;
          setPropietario(propietarioData);
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
        }
      }

      if (propietarioData?.idPropietario) {
        // Obtener mascotas del propietario
        const mascotasRes = await mascotaService.getByPropietario(propietarioData.idPropietario);
        const mascotasData = mascotasRes.data || [];
        setMascotas(mascotasData);

        // Obtener todas las vacunaciones de las mascotas
        const todasVacunaciones = [];
        for (const mascota of mascotasData) {
          try {
            // Obtener historia clínica de la mascota
            const historiaRes = await historiaClinicaService.getByMascota(mascota.idMascota);
            const historia = historiaRes.data;
            
            if (historia?.idHistoriaClinica) {
              // Obtener vacunaciones de la historia clínica
              const vacunacionesRes = await vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica);
              const vacunacionesData = vacunacionesRes.data || [];
              // Agregar información de la mascota a cada vacunación
              vacunacionesData.forEach(v => {
                v.mascotaNombre = mascota.nombre;
                v.mascotaId = mascota.idMascota;
              });
              todasVacunaciones.push(...vacunacionesData);
            }
          } catch (error) {
            // La mascota puede no tener historia clínica o vacunaciones aún
            console.debug(`Mascota ${mascota.idMascota} no tiene historia clínica o vacunaciones`);
          }
        }
        setVacunaciones(todasVacunaciones);

        // Obtener citas de vacunación programadas de las mascotas
        const todasCitasVacunacion = [];
        for (const mascota of mascotasData) {
          try {
            // Obtener citas de la mascota
            const citasRes = await citaService.getByMascota(mascota.idMascota);
            const citasData = citasRes.data || [];

            // Filtrar solo las citas de vacunación que estén programadas o confirmadas
            const citasVacunacionMascota = citasData.filter(cita =>
              cita.servicio?.tipoServicio === 'VACUNACION' &&
              ['PROGRAMADA', 'CONFIRMADA'].includes(cita.estado)
            );

            // Agregar información de la mascota a cada cita
            citasVacunacionMascota.forEach(c => {
              c.mascotaNombre = mascota.nombre;
              c.mascotaId = mascota.idMascota;
            });

            todasCitasVacunacion.push(...citasVacunacionMascota);
          } catch (error) {
            console.debug(`Mascota ${mascota.idMascota} no tiene citas de vacunación programadas`);
          }
        }
        setCitasVacunacion(todasCitasVacunacion);
      }

      // Obtener veterinarios y servicios disponibles
      const [veterinariosRes, serviciosRes] = await Promise.all([
        veterinarioService.getActivos(),
        servicioService.getAll(),
      ]);
      setVeterinarios(veterinariosRes.data || []);
      // Filtrar servicios de vacunación
      const serviciosVacunacion = (serviciosRes.data || []).filter(s =>
        s.tipoServicio === 'VACUNACION' || s.nombre?.toLowerCase().includes('vacun')
      );
      setServicios(serviciosVacunacion.length > 0 ? serviciosVacunacion : serviciosRes.data || []);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos. Por favor, recarga la página.');
    } finally {
      setLoading(false);
    }
  };

  const handleViewVacunacion = (vacunacion) => {
    setSelectedVacunacion(vacunacion);
    setViewModalOpen(true);
  };

  const handleAgendarVacunacion = (mascota = null) => {
    // Resetear estados
    setStep(1);
    setMascotaSeleccionada(mascota || null);
    setServicioSeleccionado(null);
    setVeterinarioSeleccionado(null);
    setFechaSeleccionada('');
    setHoraSeleccionada('');
    setMotivo('Vacunación');
    setHorarios([]);
    setDisponibilidad(null);
    setError('');
    setSuccess('');
    setMostrarAgendarVacunacion(true);
  };

  const handleCloseAgendarVacunacion = () => {
    setMostrarAgendarVacunacion(false);
    setStep(1);
    setMascotaSeleccionada(null);
    setServicioSeleccionado(null);
    setVeterinarioSeleccionado(null);
    setFechaSeleccionada('');
    setHoraSeleccionada('');
    setMotivo('Vacunación');
    setHorarios([]);
    setDisponibilidad(null);
    setError('');
    setSuccess('');
  };

  const handleSelectMascota = (mascota) => {
    setMascotaSeleccionada(mascota);
    setStep(2);
  };

  const handleSelectServicio = (servicio) => {
    setServicioSeleccionado(servicio);
    setStep(3);
  };

  const loadHorarios = async (veterinarioId) => {
    try {
      setLoading(true);
      const horariosRes = await horarioService.getByVeterinario(veterinarioId);
      setHorarios(horariosRes.data || []);
      console.log('✅ Horarios cargados:', horariosRes.data);
    } catch (error) {
      console.error('Error al cargar horarios:', error);
      setError('Error al cargar horarios del veterinario');
      setHorarios([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectVeterinario = (veterinario) => {
    setVeterinarioSeleccionado(veterinario);
    loadHorarios(veterinario.idPersonal);

    // Si no hay fecha seleccionada, sugerir la fecha de hoy
    if (!fechaSeleccionada) {
      const hoy = new Date().toISOString().split('T')[0];
      setFechaSeleccionada(hoy);
      loadDisponibilidad(veterinario.idPersonal, hoy);
    } else {
      // Si ya hay una fecha seleccionada, cargar disponibilidad
      loadDisponibilidad(veterinario.idPersonal, fechaSeleccionada);
    }
  };

  const loadDisponibilidad = async (idVeterinario, fecha) => {
    if (!idVeterinario || !fecha) {
      setDisponibilidad(null);
      return;
    }

    try {
      setLoadingDisponibilidad(true);
      const response = await horarioService.getDisponibilidad(idVeterinario, fecha);
      setDisponibilidad(response.data);
      console.log('✅ Disponibilidad cargada:', response.data);
    } catch (error) {
      console.error('Error al cargar disponibilidad:', error);
      setDisponibilidad(null);
    } finally {
      setLoadingDisponibilidad(false);
    }
  };

  const getFechaMinima = () => {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    return hoy.toISOString().split('T')[0];
  };

  const handleFechaChange = (fecha) => {
    const fechaMinima = getFechaMinima();
    if (fecha && fecha < fechaMinima) {
      setError('No se pueden agendar citas en fechas pasadas. Por favor selecciona una fecha de hoy en adelante.');
      setFechaSeleccionada('');
      setHoraSeleccionada('');
      return;
    }

    setError('');
    setFechaSeleccionada(fecha);
    setHoraSeleccionada('');
    if (veterinarioSeleccionado && fecha) {
      loadDisponibilidad(veterinarioSeleccionado.idPersonal, fecha);
    }
  };

  const handleSubmitAgendar = async () => {
    try {
      setSubmitting(true);
      setError('');
      setSuccess('');

      if (!mascotaSeleccionada || !servicioSeleccionado || !veterinarioSeleccionado || !fechaSeleccionada || !horaSeleccionada) {
        setError('Por favor completa todos los campos obligatorios');
        return;
      }

      const fechaMinima = getFechaMinima();
      if (fechaSeleccionada < fechaMinima) {
        setError('No se pueden agendar citas en fechas pasadas. Por favor selecciona una fecha de hoy en adelante.');
        return;
      }

      const motivoFinal = motivo.trim() || `Vacunación: ${servicioSeleccionado.nombre}`;
      if (motivoFinal.length < 5) {
        setError('El motivo debe tener al menos 5 caracteres');
        return;
      }

      // Formatear la hora
      let horaFormateada = horaSeleccionada;
      if (typeof horaSeleccionada === 'object' && horaSeleccionada.hour !== undefined) {
        const hour = String(horaSeleccionada.hour).padStart(2, '0');
        const minute = String(horaSeleccionada.minute || 0).padStart(2, '0');
        const second = String(horaSeleccionada.second || 0).padStart(2, '0');
        horaFormateada = `${hour}:${minute}:${second}`;
      } else if (typeof horaSeleccionada === 'string') {
        if (horaSeleccionada.match(/^\d{1,2}:\d{2}:\d{2}$/)) {
          horaFormateada = horaSeleccionada;
        } else if (horaSeleccionada.match(/^\d{1,2}:\d{2}$/)) {
          horaFormateada = `${horaSeleccionada}:00`;
        } else if (horaSeleccionada.match(/^\d{1,2}$/)) {
          horaFormateada = `${horaSeleccionada.padStart(2, '0')}:00:00`;
        }
      }

      const citaData = {
        fechaCita: fechaSeleccionada,
        horaCita: horaFormateada,
        motivo: motivoFinal,
        idMascota: mascotaSeleccionada.idMascota,
        idServicio: servicioSeleccionado.idServicio,
        idVeterinario: veterinarioSeleccionado.idPersonal
      };

      console.log('📤 Enviando cita de vacunación:', citaData);
      await citaService.create(citaData);
      console.log('✅ Cita de vacunación agendada exitosamente');

      setSuccess('¡Cita para vacunación agendada exitosamente! Hemos enviado un correo de confirmación a tu email con todos los detalles de la cita.');
      setStep(4);

      setTimeout(() => {
        handleCloseAgendarVacunacion();
        loadData(); // Recargar datos para mostrar la nueva cita
      }, 5000);
    } catch (error) {
      console.error('❌ Error al agendar cita de vacunación:', error);
      let mensajeError = 'Error desconocido';
      if (error.response?.data) {
        if (error.response.data.validationErrors) {
          const validationErrors = error.response.data.validationErrors;
          const errorMessages = Object.entries(validationErrors)
            .map(([field, message]) => `${field}: ${message}`)
            .join(', ');
          mensajeError = `Error de validación: ${errorMessages}`;
        } else if (error.response.data.errors && Array.isArray(error.response.data.errors)) {
          mensajeError = `Error de validación: ${error.response.data.errors.join(', ')}`;
        } else if (error.response.data.message) {
          mensajeError = error.response.data.message;
        } else if (error.response.data.error) {
          mensajeError = error.response.data.error;
        }
      } else if (error.message) {
        mensajeError = error.message;
      }
      setError(`Error al agendar cita: ${mensajeError}`);
    } finally {
      setSubmitting(false);
    }
  };

  const filteredVacunaciones = vacunaciones.filter((v) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      v.nombreVacuna?.toLowerCase().includes(searchLower) ||
      v.laboratorio?.toLowerCase().includes(searchLower) ||
      v.lote?.toLowerCase().includes(searchLower) ||
      v.mascotaNombre?.toLowerCase().includes(searchLower)
    );
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Vacunaciones de Mis Mascotas</h2>
        <button
          onClick={() => handleAgendarVacunacion()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Agendar Vacunación
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg animate-slideDown">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg animate-slideDown">
          {success}
        </div>
      )}

      {/* Citas de Vacunación Programadas */}
      {citasVacunacion.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg overflow-hidden animate-scaleIn">
          <div className="p-4 bg-gradient-to-r from-blue-600 to-blue-700">
            <h3 className="text-xl font-bold text-white flex items-center gap-2">
              <Calendar className="w-6 h-6" />
              Citas de Vacunación Programadas ({citasVacunacion.length})
            </h3>
          </div>
          <div className="p-4">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {citasVacunacion.map((cita) => (
                <div key={cita.idCita} className="border-2 border-blue-200 rounded-lg p-4 hover:border-blue-400 hover:shadow-md transition-all bg-blue-50">
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <Syringe className="w-5 h-5 text-blue-600" />
                      <h4 className="font-semibold text-gray-900">{cita.mascotaNombre}</h4>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                      cita.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {cita.estado}
                    </span>
                  </div>
                  <div className="space-y-2 text-sm">
                    <div className="flex items-start gap-2">
                      <span className="text-gray-600 font-medium min-w-[80px]">Servicio:</span>
                      <span className="text-gray-900">{cita.servicio?.nombre || 'N/A'}</span>
                    </div>
                    {cita.servicio?.duracionMinutos && (
                      <div className="flex items-start gap-2">
                        <span className="text-gray-600 font-medium min-w-[80px]">Duración:</span>
                        <span className="text-gray-900 flex items-center gap-1">
                          <Clock className="w-4 h-4" />
                          {cita.servicio.duracionMinutos} minutos
                        </span>
                      </div>
                    )}
                    <div className="flex items-start gap-2">
                      <span className="text-gray-600 font-medium min-w-[80px]">Fecha:</span>
                      <span className="text-gray-900">{new Date(cita.fechaCita).toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</span>
                    </div>
                    <div className="flex items-start gap-2">
                      <span className="text-gray-600 font-medium min-w-[80px]">Hora:</span>
                      <span className="text-gray-900">{cita.horaCita}</span>
                    </div>
                    <div className="flex items-start gap-2">
                      <span className="text-gray-600 font-medium min-w-[80px]">Veterinario:</span>
                      <span className="text-gray-900">Dr(a). {cita.veterinario?.nombreCompleto || 'N/A'}</span>
                    </div>
                    {cita.motivoConsulta && (
                      <div className="flex items-start gap-2">
                        <span className="text-gray-600 font-medium min-w-[80px]">Motivo:</span>
                        <span className="text-gray-900">{cita.motivoConsulta}</span>
                      </div>
                    )}
                  </div>
                  <div className="mt-3 pt-3 border-t border-blue-200">
                    <p className="text-xs text-blue-700 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      Recibirás un recordatorio por correo antes de la cita
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Resumen de vacunaciones por mascota */}
      {mascotas.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mascotas.map((mascota) => {
            const vacunacionesMascota = vacunaciones.filter(v => v.mascotaId === mascota.idMascota);
            const citasMascota = citasVacunacion.filter(c => c.mascotaId === mascota.idMascota);
            const proximasVacunaciones = vacunacionesMascota.filter(v => {
              if (!v.fechaProximaDosis) return false;
              const fechaProxima = new Date(v.fechaProximaDosis);
              return fechaProxima >= new Date();
            }).sort((a, b) => new Date(a.fechaProximaDosis) - new Date(b.fechaProximaDosis));

            return (
              <div key={mascota.idMascota} className="bg-white rounded-lg shadow-md p-4">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">{mascota.nombre}</h3>
                    <p className="text-sm text-gray-500">{mascota.especie?.nombre || 'N/A'}</p>
                  </div>
                  <button
                    onClick={() => handleAgendarVacunacion(mascota)}
                    className="text-primary-600 hover:text-primary-800"
                    title="Agendar vacunación"
                  >
                    <Calendar className="w-5 h-5" />
                  </button>
                </div>
                <div className="space-y-2">
                  <p className="text-sm text-gray-600">
                    <span className="font-medium">Total vacunaciones:</span> {vacunacionesMascota.length}
                  </p>
                  {citasMascota.length > 0 && (
                    <div className="mt-2 p-2 bg-blue-50 rounded border border-blue-200">
                      <p className="text-xs font-medium text-blue-800">Citas programadas:</p>
                      <p className="text-xs text-blue-700">{citasMascota.length} cita(s) de vacunación</p>
                    </div>
                  )}
                  {proximasVacunaciones.length > 0 && (
                    <div className="mt-2 p-2 bg-yellow-50 rounded">
                      <p className="text-xs font-medium text-yellow-800">Próxima vacunación:</p>
                      <p className="text-xs text-yellow-700">
                        {proximasVacunaciones[0].nombreVacuna} - {new Date(proximasVacunaciones[0].fechaProximaDosis).toLocaleDateString('es-ES')}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      <div className="bg-white rounded-lg shadow-lg overflow-hidden animate-scaleIn">
        <div className="p-4 border-b bg-gradient-to-r from-green-600 to-green-700">
          <h3 className="text-xl font-bold text-white flex items-center gap-2 mb-3">
            <Syringe className="w-6 h-6" />
            Historial de Vacunaciones Aplicadas
          </h3>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar vacunaciones..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-smooth"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vacuna</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Laboratorio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha Aplicación</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Próxima Dosis</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredVacunaciones.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    {vacunaciones.length === 0 
                      ? 'No hay vacunaciones registradas para tus mascotas. Agenda una cita para vacunación.'
                      : 'No se encontraron vacunaciones'}
                  </td>
                </tr>
              ) : (
                filteredVacunaciones.map((vacunacion) => (
                  <tr key={vacunacion.idVacunacion} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{vacunacion.mascotaNombre || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{vacunacion.nombreVacuna}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.laboratorio}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaAplicacion ? new Date(vacunacion.fechaAplicacion).toLocaleDateString('es-ES') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaProximaDosis ? (
                        <span className={new Date(vacunacion.fechaProximaDosis) <= new Date() ? 'text-red-600 font-medium' : ''}>
                          {new Date(vacunacion.fechaProximaDosis).toLocaleDateString('es-ES')}
                        </span>
                      ) : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.veterinario?.nombreCompleto || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleViewVacunacion(vacunacion)}
                        className="text-primary-600 hover:text-primary-900 transition-colors"
                        title="Ver detalles"
                      >
                        <Eye className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal para ver detalles de vacunación */}
      <Modal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        title="Detalles de Vacunación"
        size="md"
      >
        {selectedVacunacion && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Mascota</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.mascotaNombre || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Vacuna</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.nombreVacuna}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Laboratorio</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.laboratorio}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Lote</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.lote}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Vía de Administración</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.viaAdministracion || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Fecha de Aplicación</label>
                <p className="mt-1 text-sm text-gray-900">
                  {selectedVacunacion.fechaAplicacion ? new Date(selectedVacunacion.fechaAplicacion).toLocaleDateString('es-ES') : 'N/A'}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Próxima Dosis</label>
                <p className="mt-1 text-sm text-gray-900">
                  {selectedVacunacion.fechaProximaDosis ? new Date(selectedVacunacion.fechaProximaDosis).toLocaleDateString('es-ES') : 'N/A'}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Veterinario</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.veterinario?.nombreCompleto || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Esquema Completo</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.esquemaCompleto ? 'Sí' : 'No'}</p>
              </div>
              {selectedVacunacion.observaciones && (
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                  <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.observaciones}</p>
                </div>
              )}
            </div>
          </div>
        )}
      </Modal>

      {/* Flujo paso a paso para agendar vacunación */}
      {mostrarAgendarVacunacion && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 overflow-y-auto">
          <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="sticky top-0 bg-white border-b p-6 flex justify-between items-center z-10">
              <h2 className="text-2xl font-bold">Agendar Vacunación</h2>
              <button
                onClick={handleCloseAgendarVacunacion}
                className="text-gray-500 hover:text-gray-700 text-2xl"
              >
                ×
              </button>
            </div>

            <div className="p-6 space-y-6">
              {/* Indicador de pasos */}
              <div className="bg-gray-50 rounded-lg shadow p-6">
                <div className="flex items-center justify-between">
                  {[1, 2, 3, 4].map((s) => (
                    <div key={s} className="flex items-center">
                      <div className={`flex items-center justify-center w-10 h-10 rounded-full ${
                        step >= s ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-600'
                      }`}>
                        {step > s ? <CheckCircle className="w-6 h-6" /> : s}
                      </div>
                      {s < 4 && (
                        <div className={`w-16 md:w-24 h-1 mx-2 ${
                          step > s ? 'bg-primary-600' : 'bg-gray-200'
                        }`} />
                      )}
                    </div>
                  ))}
                </div>
                <div className="grid grid-cols-4 gap-2 mt-3 text-center text-xs md:text-sm">
                  <div className={step >= 1 ? 'text-primary-600 font-medium' : 'text-gray-500'}>Mascota</div>
                  <div className={step >= 2 ? 'text-primary-600 font-medium' : 'text-gray-500'}>Servicio</div>
                  <div className={step >= 3 ? 'text-primary-600 font-medium' : 'text-gray-500'}>Fecha y Hora</div>
                  <div className={step >= 4 ? 'text-primary-600 font-medium' : 'text-gray-500'}>Confirmación</div>
                </div>
              </div>

              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-start gap-2">
                  <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
                  <span>{error}</span>
                </div>
              )}

              {success && (
                <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg flex items-start gap-2">
                  <CheckCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
                  <span>{success}</span>
                </div>
              )}

              {/* Paso 1: Seleccionar Mascota */}
              {step === 1 && (
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <PawPrint className="w-6 h-6 text-primary-600" />
                    Paso 1: Selecciona tu Mascota
                  </h3>

                  {mascotas.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                      {mascotas.map((mascota) => (
                        <div
                          key={mascota.idMascota}
                          onClick={() => handleSelectMascota(mascota)}
                          className="border-2 border-gray-200 rounded-lg p-4 hover:border-primary-500 hover:bg-primary-50 cursor-pointer transition-all"
                        >
                          <div className="flex items-start gap-3">
                            <div className="bg-primary-100 p-2 rounded-full">
                              <PawPrint className="w-5 h-5 text-primary-600" />
                            </div>
                            <div className="flex-1">
                              <h4 className="font-semibold text-gray-900">{mascota.nombre}</h4>
                              <p className="text-sm text-gray-600 mt-1">
                                {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                              </p>
                              <div className="flex gap-3 mt-2 text-xs text-gray-500">
                                <span>Sexo: {mascota.sexo}</span>
                                {mascota.peso && <span>{mascota.peso} kg</span>}
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <PawPrint className="w-16 h-16 mx-auto mb-4 text-gray-400" />
                      <p>No tienes mascotas registradas</p>
                      <p className="text-sm mt-2">Registra una mascota primero para poder agendar vacunaciones</p>
                    </div>
                  )}
                </div>
              )}

              {/* Paso 2: Seleccionar Servicio */}
              {step === 2 && (
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <Scissors className="w-6 h-6 text-primary-600" />
                    Paso 2: Selecciona el Servicio de Vacunación
                  </h3>

                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {servicios.map((servicio) => (
                      <div
                        key={servicio.idServicio}
                        onClick={() => handleSelectServicio(servicio)}
                        className="border-2 border-gray-200 rounded-lg p-4 hover:border-primary-500 hover:bg-primary-50 cursor-pointer transition-all"
                      >
                        <h4 className="font-semibold text-gray-900">{servicio.nombre}</h4>
                        {servicio.descripcion && (
                          <p className="text-sm text-gray-600 mt-2 line-clamp-2">{servicio.descripcion}</p>
                        )}
                        <div className="flex justify-between items-center mt-3">
                          <span className="text-sm text-gray-500">⏱️ Duración: {servicio.duracionMinutos || servicio.duracionFormateada || 'N/A'} {servicio.duracionMinutos && 'min'}</span>
                          {servicio.precio && (
                            <span className="font-bold text-primary-600">${servicio.precio}</span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>

                  <div className="mt-6">
                    <button
                      onClick={() => setStep(1)}
                      className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                      Volver
                    </button>
                  </div>
                </div>
              )}

              {/* Paso 3: Seleccionar Fecha y Hora */}
              {step === 3 && (
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <Calendar className="w-6 h-6 text-primary-600" />
                    Paso 3: Selecciona Fecha, Hora y Veterinario
                  </h3>

                  <div className="space-y-6">
                    {/* Selección de veterinario */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Veterinario *</label>
                      <select
                        value={veterinarioSeleccionado?.idPersonal || ''}
                        onChange={(e) => {
                          const vet = veterinarios.find(v => v.idPersonal.toString() === e.target.value);
                          if (vet) handleSelectVeterinario(vet);
                        }}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        required
                      >
                        <option value="">Selecciona un veterinario</option>
                        {veterinarios.map(vet => (
                          <option key={vet.idPersonal} value={vet.idPersonal}>
                            Dr(a). {vet.nombres} {vet.apellidos} - {vet.especialidad || 'General'}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Mostrar horarios generales del veterinario */}
                    {veterinarioSeleccionado && horarios.length > 0 && (
                      <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                        <h4 className="font-semibold text-green-900 mb-3 flex items-center gap-2">
                          <Clock className="w-5 h-5" />
                          Horarios de Atención del Dr(a). {veterinarioSeleccionado.nombres}
                        </h4>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                          {horarios.map((horario) => (
                            <div key={horario.idHorario} className="bg-white rounded-lg p-3 border border-green-300">
                              <div className="flex items-center justify-between">
                                <div>
                                  <p className="font-semibold text-green-900">{horario.diaSemana}</p>
                                  <p className="text-sm text-green-700">
                                    {horario.horaInicio} - {horario.horaFin}
                                  </p>
                                  <p className="text-xs text-green-600 mt-1">
                                    Duración por cita: {horario.duracionCitaMinutos} min
                                  </p>
                                </div>
                                {horario.activo ? (
                                  <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full">
                                    Activo
                                  </span>
                                ) : (
                                  <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded-full">
                                    Inactivo
                                  </span>
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {veterinarioSeleccionado && horarios.length === 0 && (
                      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                        <p className="text-sm text-yellow-800">
                          ⚠️ Este veterinario aún no tiene horarios configurados. Por favor, selecciona otro veterinario o contacta a la clínica.
                        </p>
                      </div>
                    )}

                    {/* Selección de fecha */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Fecha de la Cita *</label>
                      <input
                        type="date"
                        value={fechaSeleccionada}
                        onChange={(e) => handleFechaChange(e.target.value)}
                        min={getFechaMinima()}
                        max="2099-12-31"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        required
                      />
                      <p className="text-xs text-gray-500 mt-1">
                        Solo se pueden agendar citas para fechas futuras o el día de hoy
                      </p>
                    </div>

                    {/* Mostrar disponibilidad */}
                    {veterinarioSeleccionado && fechaSeleccionada && (
                      <div className="bg-gradient-to-br from-blue-50 to-indigo-50 border-2 border-blue-300 rounded-xl p-6 shadow-lg">
                        <h4 className="font-bold text-blue-900 mb-4 flex items-center gap-2 text-lg">
                          <Clock className="w-6 h-6" />
                          Horarios Disponibles para {disponibilidad?.diaSemana || 'este día'}
                        </h4>

                        {loadingDisponibilidad ? (
                          <div className="text-center py-8">
                            <div className="animate-spin rounded-full h-10 w-10 border-b-4 border-blue-600 mx-auto"></div>
                            <p className="text-base text-blue-700 mt-4 font-medium">Cargando disponibilidad...</p>
                          </div>
                        ) : disponibilidad ? (
                          <div className="space-y-4">
                            {disponibilidad.tieneHorarios ? (
                              <>
                                <div className="bg-white/70 rounded-lg p-4 border border-blue-200">
                                  <p className="text-sm font-semibold text-blue-900 mb-1">
                                    📅 Horario de atención: {disponibilidad.diaSemana}
                                  </p>
                                  {disponibilidad.horarios && disponibilidad.horarios.length > 0 && (
                                    <p className="text-base text-blue-700 font-medium">
                                      ⏰ {disponibilidad.horarios.map(h =>
                                        `${h.horaInicio} - ${h.horaFin}`
                                      ).join(', ')}
                                    </p>
                                  )}
                                </div>

                                {/* Slots disponibles */}
                                {disponibilidad.slotsDisponibles && disponibilidad.slotsDisponibles.length > 0 ? (
                                  <div>
                                    <p className="text-base font-bold text-blue-900 mb-3">
                                      ✨ Selecciona un horario disponible:
                                    </p>
                                    <div className="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3 max-h-96 overflow-y-auto p-2">
                                      {disponibilidad.slotsDisponibles.map((slot, index) => {
                                        const slotHoraStr = typeof slot.hora === 'string'
                                          ? slot.hora
                                          : `${String(slot.hora.hour || 0).padStart(2, '0')}:${String(slot.hora.minute || 0).padStart(2, '0')}:${String(slot.hora.second || 0).padStart(2, '0')}`;

                                        const horaSeleccionadaStr = typeof horaSeleccionada === 'string'
                                          ? horaSeleccionada
                                          : horaSeleccionada?.hour !== undefined
                                            ? `${String(horaSeleccionada.hour).padStart(2, '0')}:${String(horaSeleccionada.minute || 0).padStart(2, '0')}:${String(horaSeleccionada.second || 0).padStart(2, '0')}`
                                            : '';

                                        const isSelected = slotHoraStr === horaSeleccionadaStr;
                                        const horaDisplay = typeof slot.hora === 'string'
                                          ? slot.hora.substring(0, 5)
                                          : `${String(slot.hora.hour || 0).padStart(2, '0')}:${String(slot.hora.minute || 0).padStart(2, '0')}`;
                                        const estaDisponible = slot.disponible;

                                        return (
                                          <button
                                            key={index}
                                            type="button"
                                            onClick={() => estaDisponible && setHoraSeleccionada(slot.hora)}
                                            disabled={!estaDisponible}
                                            className={`px-4 py-4 text-base font-bold rounded-xl transition-all transform hover:scale-105 ${
                                              estaDisponible
                                                ? isSelected
                                                  ? 'bg-gradient-to-br from-primary-600 to-primary-700 text-white shadow-xl ring-4 ring-primary-300 scale-105'
                                                  : 'bg-gradient-to-br from-green-100 to-green-200 text-green-900 hover:from-green-200 hover:to-green-300 border-2 border-green-400 shadow-md'
                                                : 'bg-gray-200 text-gray-500 cursor-not-allowed border-2 border-gray-300 opacity-50'
                                            }`}
                                            title={estaDisponible ? '✅ Clic para seleccionar' : `❌ ${slot.motivoNoDisponible || 'No disponible (ocupado por otra cita)'}`}
                                          >
                                            <div className="flex flex-col items-center">
                                              <span className="text-lg">{horaDisplay}</span>
                                              {isSelected && (
                                                <span className="text-xs mt-1">✓ Seleccionado</span>
                                              )}
                                              {!estaDisponible && (
                                                <span className="text-xs mt-1">🔒</span>
                                              )}
                                            </div>
                                          </button>
                                        );
                                      })}
                                    </div>

                                    {/* Leyenda */}
                                    <div className="flex flex-wrap gap-4 mt-4 text-sm">
                                      <div className="flex items-center gap-2">
                                        <div className="w-4 h-4 bg-gradient-to-br from-green-100 to-green-200 border-2 border-green-400 rounded"></div>
                                        <span className="text-gray-700">Disponible</span>
                                      </div>
                                      <div className="flex items-center gap-2">
                                        <div className="w-4 h-4 bg-gradient-to-br from-primary-600 to-primary-700 rounded"></div>
                                        <span className="text-gray-700">Seleccionado</span>
                                      </div>
                                      <div className="flex items-center gap-2">
                                        <div className="w-4 h-4 bg-gray-200 border-2 border-gray-300 rounded"></div>
                                        <span className="text-gray-700">Ocupado</span>
                                      </div>
                                    </div>
                                  </div>
                                ) : (
                                  <div className="bg-yellow-50 border border-yellow-300 rounded-lg p-4">
                                    <p className="text-yellow-800">⚠️ No hay horarios disponibles para esta fecha. Intenta con otro día.</p>
                                  </div>
                                )}

                                {/* Citas ocupadas */}
                                {disponibilidad.citasOcupadas && disponibilidad.citasOcupadas.length > 0 && (
                                  <div className="bg-white/70 rounded-lg p-4 border border-red-200">
                                    <p className="text-sm font-bold text-red-900 mb-3 flex items-center gap-2">
                                      🔴 Horarios ya ocupados:
                                    </p>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                                      {disponibilidad.citasOcupadas.map((cita) => (
                                        <div key={cita.idCita} className="text-sm text-red-700 bg-red-50 px-3 py-2 rounded-lg border border-red-200">
                                          <span className="font-bold">{cita.hora}</span> - {cita.nombreMascota} ({cita.nombreServicio})
                                        </div>
                                      ))}
                                    </div>
                                  </div>
                                )}
                              </>
                            ) : (
                              <div className="bg-orange-50 border-2 border-orange-300 rounded-lg p-6 text-center">
                                <p className="text-orange-800 font-semibold text-lg">⚠️ El veterinario no trabaja este día</p>
                                <p className="text-orange-700 text-sm mt-2">Por favor, selecciona otro día de la semana.</p>
                              </div>
                            )}
                          </div>
                        ) : (
                          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                            <p className="text-red-700">❌ No se pudo cargar la disponibilidad. Intenta de nuevo.</p>
                          </div>
                        )}
                      </div>
                    )}

                    {/* Mostrar hora seleccionada */}
                    {horaSeleccionada && (
                      <div className="bg-gradient-to-r from-primary-50 to-primary-100 border-2 border-primary-400 rounded-xl p-5 shadow-md">
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-3">
                            <div className="bg-primary-600 text-white rounded-full p-3">
                              <Clock className="w-6 h-6" />
                            </div>
                            <div>
                              <p className="text-sm font-medium text-primary-900">Hora seleccionada:</p>
                              <p className="text-2xl font-bold text-primary-700">
                                {typeof horaSeleccionada === 'string'
                                  ? horaSeleccionada
                                  : `${String(horaSeleccionada.hour || 0).padStart(2, '0')}:${String(horaSeleccionada.minute || 0).padStart(2, '0')}`}
                              </p>
                            </div>
                          </div>
                          <button
                            type="button"
                            onClick={() => setHoraSeleccionada('')}
                            className="text-primary-600 hover:text-primary-800 font-medium text-sm underline"
                          >
                            Cambiar hora
                          </button>
                        </div>
                      </div>
                    )}

                    {/* Motivo */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Motivo <span className="text-red-500">*</span>
                        <span className="text-xs text-gray-500 ml-2">(mínimo 5 caracteres)</span>
                      </label>
                      <textarea
                        value={motivo}
                        onChange={(e) => setMotivo(e.target.value)}
                        rows="3"
                        placeholder="Describe el motivo de la vacunación (ej: Vacunación antirrábica, Refuerzo anual, etc.)"
                        className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent ${
                          motivo.trim().length > 0 && motivo.trim().length < 5
                            ? 'border-red-300 bg-red-50'
                            : 'border-gray-300'
                        }`}
                        minLength={5}
                        maxLength={500}
                      />
                      {motivo.trim().length > 0 && motivo.trim().length < 5 && (
                        <p className="text-xs text-red-600 mt-1">⚠️ El motivo debe tener al menos 5 caracteres</p>
                      )}
                    </div>
                  </div>

                  <div className="flex gap-3 mt-6">
                    <button
                      onClick={() => setStep(2)}
                      className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                      Volver
                    </button>
                    <button
                      onClick={handleSubmitAgendar}
                      disabled={!veterinarioSeleccionado || !fechaSeleccionada || !horaSeleccionada || submitting}
                      className="flex-1 bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
                    >
                      {submitting ? 'Agendando...' : 'Agendar Vacunación'}
                    </button>
                  </div>
                </div>
              )}

              {/* Paso 4: Confirmación */}
              {step === 4 && (
                <div className="bg-white rounded-lg shadow p-8 text-center">
                  <div className="bg-green-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                    <CheckCircle className="w-10 h-10 text-green-600" />
                  </div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-2">¡Vacunación Agendada Exitosamente!</h3>
                  <p className="text-gray-600 mb-6">Tu cita para vacunación ha sido registrada. Recibirás una confirmación pronto.</p>

                  <div className="bg-gray-50 rounded-lg p-6 text-left max-w-md mx-auto mb-6">
                    <h4 className="font-semibold text-gray-900 mb-3">Detalles de la Cita:</h4>
                    <div className="space-y-2 text-sm">
                      <p><span className="text-gray-600">Mascota:</span> <span className="font-medium">{mascotaSeleccionada?.nombre}</span></p>
                      <p><span className="text-gray-600">Servicio:</span> <span className="font-medium">{servicioSeleccionado?.nombre}</span></p>
                      <p><span className="text-gray-600">Veterinario:</span> <span className="font-medium">Dr(a). {veterinarioSeleccionado?.nombres}</span></p>
                      <p><span className="text-gray-600">Fecha:</span> <span className="font-medium">{fechaSeleccionada}</span></p>
                      <p><span className="text-gray-600">Hora:</span> <span className="font-medium">{typeof horaSeleccionada === 'string' ? horaSeleccionada : `${String(horaSeleccionada?.hour || 0).padStart(2, '0')}:${String(horaSeleccionada?.minute || 0).padStart(2, '0')}`}</span></p>
                    </div>
                  </div>

                  <button
                    onClick={handleCloseAgendarVacunacion}
                    className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700"
                  >
                    Cerrar
                  </button>
                </div>
              )}

              {/* Información útil */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <Calendar className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                  <div className="flex-1">
                    <h4 className="font-semibold text-blue-900 mb-1">Información sobre el Agendamiento</h4>
                    <ul className="text-sm text-blue-800 space-y-1">
                      <li>• Las citas deben ser confirmadas por recepción</li>
                      <li>• Te contactaremos para confirmar la disponibilidad</li>
                      <li>• Puedes cancelar o reagendar contactando a la clínica</li>
                      <li>• Llega 10 minutos antes de tu cita</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

