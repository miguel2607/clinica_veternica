import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { mascotaService, servicioService, veterinarioService, horarioService, citaService, propietarioService } from '../../services/api';
import { Calendar, Clock, User, PawPrint, Scissors, CheckCircle, AlertCircle } from 'lucide-react';

export default function AgendarCitaPage() {
  const { user } = useAuth();
  const [step, setStep] = useState(1); // 1: Mascota, 2: Servicio, 3: Fecha y Hora, 4: Confirmación

  // Datos
  const [propietario, setPropietario] = useState(null);
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

  useEffect(() => {
    loadInitialData();
  }, [user]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener propietario
      const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
      const prop = propietarioRes.data;
      setPropietario(prop);

      // Obtener mascotas, servicios y veterinarios en paralelo
      const [mascotasRes, serviciosRes, veterinariosRes] = await Promise.all([
        mascotaService.getByPropietario(prop.idPropietario),
        servicioService.getActivos(),
        veterinarioService.getActivos()
      ]);

      setMisMascotas(mascotasRes.data || []);
      setServicios(serviciosRes.data || []);
      setVeterinarios(veterinariosRes.data || []);

      console.log('✅ Datos cargados:', {
        mascotas: mascotasRes.data,
        servicios: serviciosRes.data,
        veterinarios: veterinariosRes.data
      });
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

      // Crear la cita
      const citaData = {
        fechaCita: fechaSeleccionada,
        horaCita: horaSeleccionada,
        motivo: motivo || `Cita para ${servicioSeleccionado.nombre}`,
        estado: 'PROGRAMADA',
        idMascota: mascotaSeleccionada.idMascota,
        idServicio: servicioSeleccionado.idServicio,
        idVeterinario: veterinarioSeleccionado.idPersonal,
        idPropietario: propietario.idPropietario
      };

      console.log('📤 Enviando cita:', citaData);

      await citaService.create(citaData);

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
      setError(`Error al agendar cita: ${error.response?.data?.message || error.message}`);
    } finally {
      setSubmitting(false);
    }
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

          {misMascotas.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {misMascotas.map((mascota) => (
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
                  <span className="text-sm text-gray-500">Duración: {servicio.duracionEstimada || 'N/A'} min</span>
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

            {/* Selección de fecha */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Fecha de la Cita *</label>
              <input
                type="date"
                value={fechaSeleccionada}
                onChange={(e) => setFechaSeleccionada(e.target.value)}
                min={new Date().toISOString().split('T')[0]}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                required
              />
            </div>

            {/* Selección de hora */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Hora de la Cita *</label>
              <input
                type="time"
                value={horaSeleccionada}
                onChange={(e) => setHoraSeleccionada(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                required
              />
            </div>

            {/* Motivo (opcional) */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Motivo (opcional)</label>
              <textarea
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
                rows="3"
                placeholder="Describe brevemente el motivo de la consulta..."
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
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
            onClick={() => window.location.reload()}
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
