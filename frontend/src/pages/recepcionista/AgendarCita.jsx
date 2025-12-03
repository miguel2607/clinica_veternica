import { useEffect, useState } from 'react';
import { mascotaService, servicioService, veterinarioService, horarioService, citaService, propietarioService } from '../../services/api';
import { Calendar, Clock, PawPrint, Scissors, CheckCircle, AlertCircle, Users } from 'lucide-react';

export default function AgendarCitaPage() {
  const [step, setStep] = useState(1); // 1: Propietario, 2: Mascota, 3: Servicio, 4: Fecha y Hora, 5: Confirmación

  // Datos
  const [propietarios, setPropietarios] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [horarios, setHorarios] = useState([]);

  // Selecciones
  const [propietarioSeleccionado, setPropietarioSeleccionado] = useState(null);
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
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      setError('');

      const [propietariosRes, serviciosRes, veterinariosRes] = await Promise.all([
        propietarioService.getActivos(),
        servicioService.getActivos(),
        veterinarioService.getActivos()
      ]);

      setPropietarios(propietariosRes.data || []);
      setServicios(serviciosRes.data || []);
      setVeterinarios(veterinariosRes.data || []);

      console.log('✅ Datos cargados');
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const loadMascotas = async (propietarioId) => {
    try {
      setLoading(true);
      const mascotasRes = await mascotaService.getByPropietario(propietarioId);
      setMascotas(mascotasRes.data || []);
      console.log('✅ Mascotas cargadas:', mascotasRes.data);
    } catch (error) {
      console.error('Error al cargar mascotas:', error);
      setError('Error al cargar mascotas del propietario');
      setMascotas([]);
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

  const handleSelectPropietario = (propietario) => {
    setPropietarioSeleccionado(propietario);
    loadMascotas(propietario.idPropietario);
    setStep(2);
  };

  const handleSelectMascota = (mascota) => {
    setMascotaSeleccionada(mascota);
    setStep(3);
  };

  const handleSelectServicio = (servicio) => {
    setServicioSeleccionado(servicio);
    setStep(4);
  };

  const handleSelectVeterinario = (veterinario) => {
    setVeterinarioSeleccionado(veterinario);
    loadHorarios(veterinario.idPersonal);

    if (fechaSeleccionada) {
      loadDisponibilidad(veterinario.idPersonal, fechaSeleccionada);
    } else {
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

  const getFechaMinima = () => {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    return hoy.toISOString().split('T')[0];
  };

  const extraerMensajeError = (error) => {
    if (!error.response?.data) {
      return error.message || 'Error desconocido';
    }

    const errorData = error.response.data;

    if (errorData.validationErrors) {
      const validationErrors = errorData.validationErrors;
      const errorMessages = Object.entries(validationErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join(', ');
      return `Error de validación: ${errorMessages}`;
    }

    if (errorData.errors && Array.isArray(errorData.errors)) {
      return `Error de validación: ${errorData.errors.join(', ')}`;
    }

    if (errorData.message) {
      return errorData.message;
    }

    if (errorData.error) {
      return errorData.error;
    }

    if (typeof errorData === 'object') {
      return JSON.stringify(errorData);
    }

    return errorData;
  };

  const handleFechaChange = (fecha) => {
    const fechaMinima = getFechaMinima();
    if (fecha && fecha < fechaMinima) {
      setError('No se pueden agendar citas en fechas pasadas.');
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

  const formatearHora = (hora) => {
    if (!hora) return '';
    
    if (typeof hora === 'object' && hora?.hour !== undefined) {
      const hour = String(hora.hour).padStart(2, '0');
      const minute = String(hora.minute || 0).padStart(2, '0');
      const second = String(hora.second || 0).padStart(2, '0');
      return `${hour}:${minute}:${second}`;
    }

    if (typeof hora === 'string') {
      const regexHHMMSS = /^\d{1,2}:\d{2}:\d{2}$/;
      const regexHHMM = /^\d{1,2}:\d{2}$/;
      
      if (regexHHMMSS.exec(hora)) {
        return hora;
      }
      if (regexHHMM.exec(hora)) {
        return `${hora}:00`;
      }
    }

    return hora;
  };

  const handleSubmit = async () => {
    try {
      setSubmitting(true);
      setError('');
      setSuccess('');

      if (!propietarioSeleccionado || !mascotaSeleccionada || !servicioSeleccionado || !veterinarioSeleccionado || !fechaSeleccionada || !horaSeleccionada) {
        setError('Por favor completa todos los campos obligatorios');
        return;
      }

      const fechaMinima = getFechaMinima();
      if (fechaSeleccionada < fechaMinima) {
        setError('No se pueden agendar citas en fechas pasadas.');
        return;
      }

      const motivoFinal = motivo.trim() || `Cita para ${servicioSeleccionado.nombre}`;

      if (motivoFinal.length < 5) {
        setError('El motivo debe tener al menos 5 caracteres');
        return;
      }

      if (!horaSeleccionada) {
        setError('Por favor selecciona una hora para la cita');
        return;
      }
      const horaFormateada = formatearHora(horaSeleccionada);

      const citaData = {
        fechaCita: fechaSeleccionada,
        horaCita: horaFormateada,
        motivo: motivoFinal,
        idMascota: mascotaSeleccionada.idMascota,
        idServicio: servicioSeleccionado.idServicio,
        idVeterinario: veterinarioSeleccionado.idPersonal
      };

      console.log('📤 Enviando cita:', citaData);

      const response = await citaService.create(citaData);
      console.log('✅ Respuesta del servidor:', response);

      setSuccess('¡Cita agendada exitosamente!');
      setStep(5);

      setTimeout(() => {
        setPropietarioSeleccionado(null);
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
      const mensajeError = extraerMensajeError(error);
      setError(`Error al agendar cita: ${mensajeError}`);
    } finally {
      setSubmitting(false);
    }
  };

  const getStepCircleClassName = (stepNum) => {
    return step >= stepNum ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-600';
  };

  const getStepConnectorClassName = (stepNum) => {
    return step > stepNum ? 'bg-primary-600' : 'bg-gray-200';
  };

  const getStepLabelClassName = (stepNum) => {
    return step >= stepNum ? 'text-primary-600 font-medium' : 'text-gray-500';
  };

  if (loading && propietarios.length === 0) {
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
        <p className="text-gray-600 mt-1">Sigue los pasos para agendar una cita</p>
      </div>

      {/* Indicador de pasos */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex items-center justify-between">
          {[1, 2, 3, 4, 5].map((s) => (
            <div key={`step-${s}`} className="flex items-center">
              <div className={`flex items-center justify-center w-10 h-10 rounded-full ${getStepCircleClassName(s)}`}>
                {step > s ? <CheckCircle className="w-6 h-6" /> : s}
              </div>
              {s < 5 && (
                <div className={`w-12 md:w-16 h-1 mx-2 ${getStepConnectorClassName(s)}`} />
              )}
            </div>
          ))}
        </div>
        <div className="grid grid-cols-5 gap-2 mt-3 text-center text-xs">
          <div className={getStepLabelClassName(1)}>Propietario</div>
          <div className={getStepLabelClassName(2)}>Mascota</div>
          <div className={getStepLabelClassName(3)}>Servicio</div>
          <div className={getStepLabelClassName(4)}>Fecha y Hora</div>
          <div className={getStepLabelClassName(5)}>Confirmación</div>
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

      {/* Paso 1: Seleccionar Propietario */}
      {step === 1 && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <Users className="w-6 h-6 text-primary-600" />
            Paso 1: Selecciona el Propietario
          </h3>

          {propietarios.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {propietarios.map((propietario) => (
                <button
                  key={propietario.idPropietario}
                  type="button"
                  onClick={() => handleSelectPropietario(propietario)}
                  className="border-2 border-gray-200 rounded-lg p-4 hover:border-primary-500 hover:bg-primary-50 cursor-pointer transition-all text-left w-full"
                >
                  <div className="flex items-start gap-3">
                    <div className="bg-primary-100 p-2 rounded-full">
                      <Users className="w-5 h-5 text-primary-600" />
                    </div>
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{propietario.nombres} {propietario.apellidos}</h4>
                      <p className="text-sm text-gray-600 mt-1">{propietario.email}</p>
                      <p className="text-xs text-gray-500 mt-1">{propietario.telefono}</p>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <Users className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p>No hay propietarios registrados</p>
            </div>
          )}
        </div>
      )}

      {/* Paso 2: Seleccionar Mascota */}
      {step === 2 && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <PawPrint className="w-6 h-6 text-primary-600" />
            Paso 2: Selecciona la Mascota
          </h3>

          {mascotas.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {mascotas.map((mascota) => (
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
                    </div>
                  </div>
                </button>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <PawPrint className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p>Este propietario no tiene mascotas registradas</p>
            </div>
          )}

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

      {/* Paso 3: Seleccionar Servicio */}
      {step === 3 && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <Scissors className="w-6 h-6 text-primary-600" />
            Paso 3: Selecciona el Servicio
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
                  <span className="text-sm text-gray-500">⏱️ {servicio.duracionMinutos || 'N/A'} min</span>
                  {servicio.precio && (
                    <span className="font-bold text-primary-600">${servicio.precio}</span>
                  )}
                </div>
              </button>
            ))}
          </div>

          <div className="mt-6">
            <button
              onClick={() => setStep(2)}
              className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Volver
            </button>
          </div>
        </div>
      )}

      {/* Paso 4: Seleccionar Fecha y Hora */}
      {step === 4 && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <Calendar className="w-6 h-6 text-primary-600" />
            Paso 4: Selecciona Fecha, Hora y Veterinario
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
            </div>

            {/* Mostrar disponibilidad */}
            {veterinarioSeleccionado && fechaSeleccionada && (
              <div className="bg-gradient-to-br from-blue-50 to-indigo-50 border-2 border-blue-300 rounded-xl p-6 shadow-lg">
                <h4 className="font-bold text-blue-900 mb-4 flex items-center gap-2 text-lg">
                  <Clock className="w-6 h-6" />
                  Horarios Disponibles
                </h4>

                {loadingDisponibilidad ? (
                  <div className="text-center py-8">
                    <div className="animate-spin rounded-full h-10 w-10 border-b-4 border-blue-600 mx-auto"></div>
                    <p className="text-base text-blue-700 mt-4 font-medium">Cargando disponibilidad...</p>
                  </div>
                ) : disponibilidad && disponibilidad.slotsDisponibles && disponibilidad.slotsDisponibles.length > 0 ? (
                  <div>
                    <p className="text-base font-bold text-blue-900 mb-3">Selecciona un horario:</p>
                    <div className="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3 max-h-96 overflow-y-auto p-2">
                      {disponibilidad.slotsDisponibles.map((slot, index) => {
                        const horaStr = typeof slot.hora === 'string' ? slot.hora : `${String(slot.hora.hour || 0).padStart(2, '0')}:${String(slot.hora.minute || 0).padStart(2, '0')}`;
                        const horaDisplay = horaStr.substring(0, 5);
                        const isSelected = horaSeleccionada === slot.hora || (typeof horaSeleccionada === 'string' && horaSeleccionada.substring(0, 5) === horaDisplay);
                        
                        return (
                          <button
                            key={index}
                            type="button"
                            onClick={() => slot.disponible && setHoraSeleccionada(slot.hora)}
                            disabled={!slot.disponible}
                            className={`px-4 py-4 text-base font-bold rounded-xl transition-all ${
                              !slot.disponible
                                ? 'bg-gray-200 text-gray-500 cursor-not-allowed opacity-50'
                                : isSelected
                                ? 'bg-primary-600 text-white shadow-xl ring-4 ring-primary-300'
                                : 'bg-green-100 text-green-900 hover:bg-green-200 border-2 border-green-400'
                            }`}
                          >
                            {horaDisplay}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                ) : (
                  <div className="bg-yellow-50 border border-yellow-300 rounded-lg p-4">
                    <p className="text-yellow-800">⚠️ No hay horarios disponibles para esta fecha. Intenta con otro día.</p>
                  </div>
                )}
              </div>
            )}

            {/* Motivo */}
            <div>
              <label htmlFor="motivo-textarea" className="block text-sm font-medium text-gray-700 mb-2">
                Motivo <span className="text-red-500">*</span>
              </label>
              <textarea
                id="motivo-textarea"
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
                rows="3"
                placeholder="Describe el motivo de la consulta"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                minLength={5}
                maxLength={500}
              />
            </div>
          </div>

          <div className="flex gap-3 mt-6">
            <button
              onClick={() => setStep(3)}
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

      {/* Paso 5: Confirmación */}
      {step === 5 && (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <div className="bg-green-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="w-10 h-10 text-green-600" />
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-2">¡Cita Agendada Exitosamente!</h3>
          <p className="text-gray-600 mb-6">La cita ha sido registrada correctamente.</p>

          <div className="bg-gray-50 rounded-lg p-6 text-left max-w-md mx-auto mb-6">
            <h4 className="font-semibold text-gray-900 mb-3">Detalles de la Cita:</h4>
            <div className="space-y-2 text-sm">
              <p><span className="text-gray-600">Propietario:</span> <span className="font-medium">{propietarioSeleccionado?.nombres} {propietarioSeleccionado?.apellidos}</span></p>
              <p><span className="text-gray-600">Mascota:</span> <span className="font-medium">{mascotaSeleccionada?.nombre}</span></p>
              <p><span className="text-gray-600">Servicio:</span> <span className="font-medium">{servicioSeleccionado?.nombre}</span></p>
              <p><span className="text-gray-600">Veterinario:</span> <span className="font-medium">Dr(a). {veterinarioSeleccionado?.nombres}</span></p>
              <p><span className="text-gray-600">Fecha:</span> <span className="font-medium">{fechaSeleccionada}</span></p>
              <p><span className="text-gray-600">Hora:</span> <span className="font-medium">{typeof horaSeleccionada === 'string' ? horaSeleccionada.substring(0, 5) : horaSeleccionada}</span></p>
            </div>
          </div>

          <button
            onClick={() => {
              setPropietarioSeleccionado(null);
              setMascotaSeleccionada(null);
              setServicioSeleccionado(null);
              setVeterinarioSeleccionado(null);
              setFechaSeleccionada('');
              setHoraSeleccionada('');
              setMotivo('');
              setStep(1);
              setSuccess('');
            }}
            className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700"
          >
            Agendar Otra Cita
          </button>
        </div>
      )}
    </div>
  );
}

