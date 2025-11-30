import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { mascotaService, servicioService, veterinarioService, horarioService, citaService, propietarioService } from '../../services/api';
import { Calendar, Clock, PawPrint, Scissors, CheckCircle, AlertCircle } from 'lucide-react';

export default function AgendarCitaPage() {
  const { user } = useAuth();
  const [step, setStep] = useState(1); // 1: Mascota, 2: Servicio, 3: Fecha y Hora, 4: Confirmación

  // Datos
  const [misMascotas, setMisMascotas] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [horarios, setHorarios] = useState([]);

  // Selecciones
  const [mascotaSeleccionada, setMascotaSeleccionada] = useState(null);
  const [servicioSeleccionado, setServicioSeleccionado] = useState(null);
  const [veterinarioSeleccionado, setVeterinarioSeleccionado] = useState(null);
  const [fechaSeleccionada, setFechaSeleccionada] = useState('');
  const [horaSeleccionada, setHoraSeleccionada] = useState('');
  const [motivo, setMotivo] = useState('');

  // Estados
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [disponibilidad, setDisponibilidad] = useState(null);
  const [loadingDisponibilidad, setLoadingDisponibilidad] = useState(false);

  useEffect(() => {
    loadInitialData();
  }, [user]);

  // Función auxiliar para obtener propietario
  const obtenerPropietario = async () => {
    const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
    return propietarioRes.data;
  };

  // Función auxiliar para cargar datos en paralelo
  const cargarDatosParalelos = async (propietarioId) => {
    const [mascotasRes, serviciosRes, veterinariosRes] = await Promise.all([
      mascotaService.getByPropietario(propietarioId),
      servicioService.getActivos(),
      veterinarioService.getActivos()
    ]);
    return {
      mascotas: mascotasRes.data || [],
      servicios: serviciosRes.data || [],
      veterinarios: veterinariosRes.data || []
    };
  };

  // Función auxiliar para actualizar estados
  const actualizarEstadosDatos = (datos) => {
    setMisMascotas(datos.mascotas);
    setServicios(datos.servicios);
    setVeterinarios(datos.veterinarios);
  };

  const loadInitialData = async () => {
    try {
      setLoading(true);
      setError('');

      const prop = await obtenerPropietario();
      const datos = await cargarDatosParalelos(prop.idPropietario);
      actualizarEstadosDatos(datos);

      console.log('✅ Datos cargados:', datos);
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
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

  const handleSelectMascota = (mascota) => {
    setMascotaSeleccionada(mascota);
    setStep(2);
  };

  const handleSelectServicio = (servicio) => {
    setServicioSeleccionado(servicio);
    setStep(3);
  };

  const handleSelectVeterinario = (veterinario) => {
    setVeterinarioSeleccionado(veterinario);
    loadHorarios(veterinario.idPersonal);

    // Si hay fecha seleccionada, cargar disponibilidad con esa fecha
    if (fechaSeleccionada) {
      loadDisponibilidad(veterinario.idPersonal, fechaSeleccionada);
    } else {
      // Si no hay fecha seleccionada, sugerir la fecha de hoy
      const hoy = new Date().toISOString().split('T')[0];
      setFechaSeleccionada(hoy);
      loadDisponibilidad(veterinario.idPersonal, hoy);
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

  // Función para obtener la fecha mínima (hoy)
  const getFechaMinima = () => {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0); // Normalizar a medianoche
    return hoy.toISOString().split('T')[0];
  };

  // Función para extraer mensaje de error
  const extraerMensajeError = (error) => {
    if (!error.response?.data) {
      return error.message || 'Error desconocido';
    }

    const errorData = error.response.data;

    // Si hay errores de validación específicos, mostrarlos primero
    if (errorData.validationErrors) {
      const validationErrors = errorData.validationErrors;
      const errorMessages = Object.entries(validationErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join(', ');
      return `Error de validación: ${errorMessages}`;
    }

    // Si hay errores en formato array, mostrarlos
    if (errorData.errors && Array.isArray(errorData.errors)) {
      return `Error de validación: ${errorData.errors.join(', ')}`;
    }

    // Intentar obtener el mensaje del campo 'message'
    if (errorData.message) {
      return errorData.message;
    }

    // Si hay un campo 'error', usarlo
    if (errorData.error) {
      return errorData.error;
    }

    // Si es un objeto, convertirlo a string
    if (typeof errorData === 'object') {
      return JSON.stringify(errorData);
    }

    return errorData;
  };

  const handleFechaChange = (fecha) => {
    // Validar que la fecha no sea del pasado
    const fechaMinima = getFechaMinima();
    if (fecha && fecha < fechaMinima) {
      setError('No se pueden agendar citas en fechas pasadas. Por favor selecciona una fecha de hoy en adelante.');
      setFechaSeleccionada('');
      setHoraSeleccionada('');
      return;
    }

    setError(''); // Limpiar errores anteriores
    setFechaSeleccionada(fecha);
    setHoraSeleccionada(''); // Limpiar hora seleccionada
    // Si hay un veterinario seleccionado, cargar disponibilidad
    if (veterinarioSeleccionado && fecha) {
      loadDisponibilidad(veterinarioSeleccionado.idPersonal, fecha);
    }
  };

  // Función para renderizar la disponibilidad
  const renderDisponibilidad = () => {
    if (loadingDisponibilidad) {
      return (
        <div className="text-center py-8">
          <div className="animate-spin rounded-full h-10 w-10 border-b-4 border-blue-600 mx-auto"></div>
          <p className="text-base text-blue-700 mt-4 font-medium">Cargando disponibilidad...</p>
        </div>
      );
    }

    if (!disponibilidad) {
      return (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700">❌ No se pudo cargar la disponibilidad. Intenta de nuevo.</p>
        </div>
      );
    }

    return renderContenidoDisponibilidad();
  };

  // Función para renderizar el contenido de disponibilidad
  const renderContenidoDisponibilidad = () => {
    if (!disponibilidad.tieneHorarios) {
      return (
        <div className="bg-orange-50 border-2 border-orange-300 rounded-lg p-6 text-center">
          <p className="text-orange-800 font-semibold text-lg">⚠️ El veterinario no trabaja este día</p>
          <p className="text-orange-700 text-sm mt-2">Por favor, selecciona otro día de la semana.</p>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        {renderHorariosAtencion()}
        {renderSlotsDisponibles()}
        {renderCitasOcupadas()}
      </div>
    );
  };

  // Función para renderizar horarios de atención
  const renderHorariosAtencion = () => {
    if (!disponibilidad.horarios || disponibilidad.horarios.length === 0) {
      return null;
    }

    return (
      <div className="bg-white/70 rounded-lg p-4 border border-blue-200">
        <p className="text-sm font-semibold text-blue-900 mb-1">
          📅 Horario de atención: {disponibilidad.diaSemana}
        </p>
        <p className="text-base text-blue-700 font-medium">
          ⏰ {disponibilidad.horarios.map(h =>
            `${h.horaInicio} - ${h.horaFin}`
          ).join(', ')}
        </p>
      </div>
    );
  };

  // Función para renderizar slots disponibles
  const renderSlotsDisponibles = () => {
    if (!disponibilidad.slotsDisponibles || disponibilidad.slotsDisponibles.length === 0) {
      return (
        <div className="bg-yellow-50 border border-yellow-300 rounded-lg p-4">
          <p className="text-yellow-800">⚠️ No hay horarios disponibles para esta fecha. Intenta con otro día.</p>
        </div>
      );
    }

    return (
      <div>
        <p className="text-base font-bold text-blue-900 mb-3">
          ✨ Selecciona un horario disponible:
        </p>
        <div className="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3 max-h-96 overflow-y-auto p-2">
          {disponibilidad.slotsDisponibles.map((slot) => renderSlotDisponible(slot))}
        </div>
        {renderLeyendaSlots()}
      </div>
    );
  };

  // Función para renderizar un slot disponible
  const renderSlotDisponible = (slot) => {
    const getSlotId = () => {
      if (typeof slot.hora === 'string') {
        return `slot-${slot.hora}`;
      }
      const horaPart = `${slot.hora.hour}-${slot.hora.minute}`;
      return `slot-${horaPart}`;
    };
    const slotId = getSlotId();

    const slotHoraStr = getSlotHoraStr(slot.hora);
    const horaSeleccionadaStr = getHoraSeleccionadaStr();
    const isSelected = slotHoraStr === horaSeleccionadaStr;
    const horaDisplay = getHoraDisplay(slot.hora);
    const estaDisponible = slot.disponible;

    return (
      <button
        key={slotId}
        type="button"
        onClick={() => estaDisponible && setHoraSeleccionada(slot.hora)}
        disabled={!estaDisponible}
        className={`px-4 py-4 text-base font-bold rounded-xl transition-all transform hover:scale-105 ${getButtonClassName(estaDisponible, isSelected)}`}
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
  };

  // Funciones auxiliares para formatear horas
  const getSlotHoraStr = (hora) => {
    if (typeof hora === 'string') {
      return hora;
    }
    const hour = String(hora.hour || 0).padStart(2, '0');
    const minute = String(hora.minute || 0).padStart(2, '0');
    const second = String(hora.second || 0).padStart(2, '0');
    return `${hour}:${minute}:${second}`;
  };

  const getHoraSeleccionadaStr = () => {
    if (typeof horaSeleccionada === 'string') {
      return horaSeleccionada;
    }
    if (horaSeleccionada?.hour !== undefined) {
      const hour = String(horaSeleccionada.hour).padStart(2, '0');
      const minute = String(horaSeleccionada.minute || 0).padStart(2, '0');
      const second = String(horaSeleccionada.second || 0).padStart(2, '0');
      return `${hour}:${minute}:${second}`;
    }
    return '';
  };

  const getHoraDisplay = (hora) => {
    if (typeof hora === 'string') {
      return hora.substring(0, 5); // "HH:mm:ss" -> "HH:mm"
    }
    const hour = String(hora.hour || 0).padStart(2, '0');
    const minute = String(hora.minute || 0).padStart(2, '0');
    return `${hour}:${minute}`;
  };

  const getButtonClassName = (estaDisponible, isSelected) => {
    if (!estaDisponible) {
      return 'bg-gray-200 text-gray-500 cursor-not-allowed border-2 border-gray-300 opacity-50';
    }
    if (isSelected) {
      return 'bg-gradient-to-br from-primary-600 to-primary-700 text-white shadow-xl ring-4 ring-primary-300 scale-105';
    }
    return 'bg-gradient-to-br from-green-100 to-green-200 text-green-900 hover:from-green-200 hover:to-green-300 border-2 border-green-400 shadow-md';
  };

  // Función para renderizar leyenda de slots
  const renderLeyendaSlots = () => {
    return (
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
    );
  };

  // Función para renderizar citas ocupadas
  const renderCitasOcupadas = () => {
    if (!disponibilidad.citasOcupadas || disponibilidad.citasOcupadas.length === 0) {
      return null;
    }

    return (
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
    );
  };

  // Función para formatear la hora seleccionada
  const formatearHora = (hora) => {
    // Validar que hora no sea null o undefined
    if (!hora) {
      return '';
    }
    
    // Si horaSeleccionada es un objeto (como {hour: 9, minute: 0, second: 0})
    if (typeof hora === 'object' && hora?.hour !== undefined) {
      const hour = String(hora.hour).padStart(2, '0');
      const minute = String(hora.minute || 0).padStart(2, '0');
      const second = String(hora.second || 0).padStart(2, '0');
      return `${hour}:${minute}:${second}`;
    }

    // Si es un string, normalizar el formato
    if (typeof hora === 'string') {
      const regexHHMMSS = /^\d{1,2}:\d{2}:\d{2}$/;
      const regexHHMM = /^\d{1,2}:\d{2}$/;
      const regexHH = /^\d{1,2}$/;
      
      // Si ya tiene formato HH:mm:ss, usarlo directamente
      if (regexHHMMSS.exec(hora)) {
        return hora;
      }
      // Si es formato HH:mm, agregar :00
      if (regexHHMM.exec(hora)) {
        return `${hora}:00`;
      }
      // Si es solo un número (H), convertir a HH:00:00
      if (regexHH.exec(hora)) {
        return `${hora.padStart(2, '0')}:00:00`;
      }
    }

    return hora;
  };

  const handleSubmit = async () => {
    try {
      setSubmitting(true);
      setError('');
      setSuccess('');

      // Validar que todos los campos estén completos
      if (!mascotaSeleccionada || !servicioSeleccionado || !veterinarioSeleccionado || !fechaSeleccionada || !horaSeleccionada) {
        setError('Por favor completa todos los campos obligatorios');
        return;
      }

      // Validar que la fecha no sea del pasado
      const fechaMinima = getFechaMinima();
      if (fechaSeleccionada < fechaMinima) {
        setError('No se pueden agendar citas en fechas pasadas. Por favor selecciona una fecha de hoy en adelante.');
        return;
      }

      // Preparar motivo (obligatorio, mínimo 5 caracteres)
      const motivoFinal = motivo.trim() || `Cita para ${servicioSeleccionado.nombre}`;

      // Validar motivo
      if (motivoFinal.length < 5) {
        setError('El motivo debe tener al menos 5 caracteres');
        return;
      }

      // Formatear la hora correctamente - asegurarse de enviar como string en formato HH:mm:ss
      if (!horaSeleccionada) {
        setError('Por favor selecciona una hora para la cita');
        return;
      }
      const horaFormateada = formatearHora(horaSeleccionada);

      // Crear la cita
      const citaData = {
        fechaCita: fechaSeleccionada,
        horaCita: horaFormateada,
        motivo: motivoFinal,
        idMascota: mascotaSeleccionada.idMascota,
        idServicio: servicioSeleccionado.idServicio,
        idVeterinario: veterinarioSeleccionado.idPersonal
      };

      console.log('📤 Enviando cita:', citaData);
      console.log('📤 Hora original:', horaSeleccionada);
      console.log('📤 Hora formateada:', horaFormateada);
      console.log('📤 Slots disponibles:', disponibilidad?.slotsDisponibles);

      const response = await citaService.create(citaData);
      console.log('✅ Respuesta del servidor:', response);

      setSuccess('¡Cita agendada exitosamente! Te enviaremos una confirmación pronto.');
      setStep(4);

      // Limpiar formulario
      setTimeout(() => {
        setMascotaSeleccionada(null);
        setServicioSeleccionado(null);
        setVeterinarioSeleccionado(null);
        setFechaSeleccionada('');
        setHoraSeleccionada('');
        setMotivo('');
        setStep(1);
        setSuccess('');
      }, 5000);
    } catch (error) {
      console.error('❌ Error al agendar cita:', error);
      console.error('❌ Error response:', error.response);
      console.error('❌ Error response data:', error.response?.data);

      // Extraer el mensaje de error más descriptivo
      const mensajeError = extraerMensajeError(error);

      setError(`Error al agendar cita: ${mensajeError}`);
    } finally {
      setSubmitting(false);
    }
  };

  // Funciones auxiliares para el indicador de pasos
  const getStepCircleClassName = (stepNum) => {
    return step >= stepNum ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-600';
  };

  const getStepConnectorClassName = (stepNum) => {
    return step > stepNum ? 'bg-primary-600' : 'bg-gray-200';
  };

  const getStepLabelClassName = (stepNum) => {
    return step >= stepNum ? 'text-primary-600 font-medium' : 'text-gray-500';
  };

  if (loading && misMascotas.length === 0) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold">Agendar Nueva Cita</h2>
        <p className="text-gray-600 mt-1">Sigue los pasos para agendar una cita para tu mascota</p>
      </div>

      {/* Indicador de pasos */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex items-center justify-between">
          {[1, 2, 3, 4].map((s) => (
            <div key={`step-${s}`} className="flex items-center">
              <div className={`flex items-center justify-center w-10 h-10 rounded-full ${getStepCircleClassName(s)}`}>
                {step > s ? <CheckCircle className="w-6 h-6" /> : s}
              </div>
              {s < 4 && (
                <div className={`w-16 md:w-24 h-1 mx-2 ${getStepConnectorClassName(s)}`} />
              )}
            </div>
          ))}
        </div>
        <div className="grid grid-cols-4 gap-2 mt-3 text-center text-xs md:text-sm">
          <div className={getStepLabelClassName(1)}>Mascota</div>
          <div className={getStepLabelClassName(2)}>Servicio</div>
          <div className={getStepLabelClassName(3)}>Fecha y Hora</div>
          <div className={getStepLabelClassName(4)}>Confirmación</div>
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

          {misMascotas.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {misMascotas.map((mascota) => (
                <button
                  key={mascota.idMascota}
                  type="button"
                  onClick={() => handleSelectMascota(mascota)}
                  className="border-2 border-gray-200 rounded-lg p-4 hover:border-primary-500 hover:bg-primary-50 cursor-pointer transition-all text-left w-full"
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
                </button>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <PawPrint className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p>No tienes mascotas registradas</p>
              <p className="text-sm mt-2">Registra una mascota primero para poder agendar citas</p>
            </div>
          )}
        </div>
      )}

      {/* Paso 2: Seleccionar Servicio */}
      {step === 2 && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <Scissors className="w-6 h-6 text-primary-600" />
            Paso 2: Selecciona el Servicio
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {servicios.map((servicio) => (
              <button
                key={servicio.idServicio}
                type="button"
                onClick={() => handleSelectServicio(servicio)}
                className="border-2 border-gray-200 rounded-lg p-4 hover:border-primary-500 hover:bg-primary-50 cursor-pointer transition-all text-left w-full"
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
              </button>
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
              <label htmlFor="veterinario-select" className="block text-sm font-medium text-gray-700 mb-2">Veterinario *</label>
              <select
                id="veterinario-select"
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
              <label htmlFor="fecha-cita-input" className="block text-sm font-medium text-gray-700 mb-2">Fecha de la Cita *</label>
              <input
                id="fecha-cita-input"
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

                {renderDisponibilidad()}
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
                        {horaSeleccionada ? (
                          typeof horaSeleccionada === 'string'
                            ? horaSeleccionada.substring(0, 5)
                            : `${String(horaSeleccionada.hour || 0).padStart(2, '0')}:${String(horaSeleccionada.minute || 0).padStart(2, '0')}`
                        ) : ''}
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

            {/* Motivo (opcional) */}
            <div>
              <label htmlFor="motivo-textarea" className="block text-sm font-medium text-gray-700 mb-2">
                Motivo <span className="text-red-500">*</span>
                <span className="text-xs text-gray-500 ml-2">(mínimo 5 caracteres)</span>
              </label>
              <textarea
                id="motivo-textarea"
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
                rows="3"
                placeholder="Describe el motivo de la consulta (ej: Vacunación antirrábica, Control de rutina, Revisión de salud general...)"
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
              {motivo.trim().length === 0 && servicioSeleccionado && (
                <p className="text-xs text-blue-600 mt-1">ℹ️ Si lo dejas vacío, se usará: "Cita para {servicioSeleccionado.nombre}"</p>
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
              onClick={handleSubmit}
              disabled={!veterinarioSeleccionado || !fechaSeleccionada || !horaSeleccionada || submitting}
              className="flex-1 bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
            >
              {submitting ? 'Agendando...' : 'Agendar Cita'}
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
          <h3 className="text-2xl font-bold text-gray-900 mb-2">¡Cita Agendada Exitosamente!</h3>
          <p className="text-gray-600 mb-6">Tu cita ha sido registrada. Recibirás una confirmación pronto.</p>

          <div className="bg-gray-50 rounded-lg p-6 text-left max-w-md mx-auto mb-6">
            <h4 className="font-semibold text-gray-900 mb-3">Detalles de la Cita:</h4>
            <div className="space-y-2 text-sm">
              <p><span className="text-gray-600">Mascota:</span> <span className="font-medium">{mascotaSeleccionada?.nombre}</span></p>
              <p><span className="text-gray-600">Servicio:</span> <span className="font-medium">{servicioSeleccionado?.nombre}</span></p>
              <p><span className="text-gray-600">Veterinario:</span> <span className="font-medium">Dr(a). {veterinarioSeleccionado?.nombres}</span></p>
              <p><span className="text-gray-600">Fecha:</span> <span className="font-medium">{fechaSeleccionada}</span></p>
              <p><span className="text-gray-600">Hora:</span> <span className="font-medium">{horaSeleccionada}</span></p>
            </div>
          </div>

          <button
            onClick={() => globalThis.location.reload()}
            className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700"
          >
            Agendar Otra Cita
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
  );
}
