import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { citaService, veterinarioService } from '../../services/api';
import { Calendar, Clock, ChevronLeft, ChevronRight, AlertCircle } from 'lucide-react';

export default function CalendarioCitasPage() {
  const { user } = useAuth();
  const [citas, setCitas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [citasDelDia, setCitasDelDia] = useState([]);

  const meses = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
  ];

  const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];

  useEffect(() => {
    if (user) {
      loadCitas();
    }
  }, [user, currentDate]);

  useEffect(() => {
    if (selectedDate) {
      const fechaStr = selectedDate.toISOString().split('T')[0];
      const citasFiltradas = citas.filter(cita => cita.fechaCita === fechaStr);
      setCitasDelDia(citasFiltradas.sort((a, b) => (a.horaCita || '').localeCompare(b.horaCita || '')));
    }
  }, [selectedDate, citas]);

  const loadCitas = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener el veterinario asociado al usuario
      let veterinario = null;
      try {
        const veterinariosRes = await veterinarioService.getAll();
        veterinario = veterinariosRes.data?.find(v => v.usuario?.username === user?.username);
      } catch (error2) {
        console.error('Error al buscar veterinarios:', error2);
        setError('Error al buscar el perfil del veterinario');
        return;
      }

      if (!veterinario || !veterinario.idPersonal) {
        setError('No se encontró un perfil de veterinario asociado a tu usuario.');
        return;
      }

      // Obtener todas las citas del veterinario
      const response = await citaService.getByVeterinario(veterinario.idPersonal);
      const todasCitas = response.data || [];

      // Filtrar citas del mes actual
      const año = currentDate.getFullYear();
      const mes = currentDate.getMonth();
      const primerDia = new Date(año, mes, 1);
      const ultimoDia = new Date(año, mes + 1, 0);

      const citasDelMes = todasCitas.filter(cita => {
        const fechaCita = new Date(cita.fechaCita);
        return fechaCita >= primerDia && fechaCita <= ultimoDia;
      });

      setCitas(citasDelMes);
    } catch (error) {
      console.error('Error al cargar citas:', error);
      setError(`Error al cargar las citas: ${error.response?.data?.message || error.message}`);
      setCitas([]);
    } finally {
      setLoading(false);
    }
  };

  const getDaysInMonth = () => {
    const año = currentDate.getFullYear();
    const mes = currentDate.getMonth();
    const primerDia = new Date(año, mes, 1);
    const ultimoDia = new Date(año, mes + 1, 0);
    const diasEnMes = ultimoDia.getDate();
    const primerDiaSemana = primerDia.getDay();

    const dias = [];
    
    // Días del mes anterior (para completar la primera semana)
    const mesAnterior = new Date(año, mes, 0);
    const diasMesAnterior = mesAnterior.getDate();
    for (let i = primerDiaSemana - 1; i >= 0; i--) {
      dias.push({
        fecha: new Date(año, mes - 1, diasMesAnterior - i),
        esDelMes: false
      });
    }

    // Días del mes actual
    for (let dia = 1; dia <= diasEnMes; dia++) {
      dias.push({
        fecha: new Date(año, mes, dia),
        esDelMes: true
      });
    }

    // Días del mes siguiente (para completar la última semana)
    const diasRestantes = 42 - dias.length; // 6 semanas * 7 días
    for (let dia = 1; dia <= diasRestantes; dia++) {
      dias.push({
        fecha: new Date(año, mes + 1, dia),
        esDelMes: false
      });
    }

    return dias;
  };

  const getCitasDelDia = (fecha) => {
    const fechaStr = fecha.toISOString().split('T')[0];
    return citas.filter(cita => cita.fechaCita === fechaStr);
  };

  const getEstadoColor = (estado) => {
    const estadoUpper = (estado || '').toUpperCase();
    switch (estadoUpper) {
      case 'PROGRAMADA':
        return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'CONFIRMADA':
        return 'bg-green-100 text-green-800 border-green-300';
      case 'EN_ATENCION':
      case 'EN_ATENCIÓN':
        return 'bg-yellow-100 text-yellow-800 border-yellow-300';
      case 'ATENDIDA':
        return 'bg-gray-100 text-gray-800 border-gray-300';
      case 'CANCELADA':
        return 'bg-red-100 text-red-800 border-red-300';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  const formatearHora = (hora) => {
    if (!hora) return '';
    if (typeof hora === 'string') {
      return hora.substring(0, 5); // HH:MM
    }
    return hora;
  };

  const handlePrevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
    setSelectedDate(null);
  };

  const handleNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
    setSelectedDate(null);
  };

  const handleToday = () => {
    setCurrentDate(new Date());
    setSelectedDate(new Date());
  };

  const isToday = (fecha) => {
    const hoy = new Date();
    return fecha.toDateString() === hoy.toDateString();
  };

  const isSelected = (fecha) => {
    if (!selectedDate) return false;
    return fecha.toDateString() === selectedDate.toDateString();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const dias = getDaysInMonth();

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold">Calendario de Citas</h2>
        <p className="text-gray-600 mt-1">Visualiza tus citas en formato de calendario</p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-start gap-2">
          <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      <div className="bg-white rounded-lg shadow p-6">
        {/* Controles del calendario */}
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={handlePrevMonth}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          
          <div className="flex items-center gap-4">
            <h3 className="text-xl font-semibold">
              {meses[currentDate.getMonth()]} {currentDate.getFullYear()}
            </h3>
            <button
              onClick={handleToday}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors text-sm"
            >
              Hoy
            </button>
          </div>

          <button
            onClick={handleNextMonth}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>

        {/* Calendario */}
        <div className="grid grid-cols-7 gap-2">
          {/* Encabezados de días */}
          {diasSemana.map(dia => (
            <div key={dia} className="text-center font-semibold text-gray-700 py-2">
              {dia}
            </div>
          ))}

          {/* Días del calendario */}
          {dias.map((dia, index) => {
            const citasDelDia = getCitasDelDia(dia.fecha);
            const esHoy = isToday(dia.fecha);
            const estaSeleccionado = isSelected(dia.fecha);

            return (
              <button
                key={index}
                onClick={() => dia.esDelMes && setSelectedDate(dia.fecha)}
                disabled={!dia.esDelMes}
                className={`
                  min-h-[80px] p-2 border rounded-lg text-left transition-all
                  ${!dia.esDelMes ? 'text-gray-300 bg-gray-50 cursor-not-allowed' : 'hover:bg-gray-50 cursor-pointer'}
                  ${esHoy ? 'border-primary-500 bg-primary-50' : 'border-gray-200'}
                  ${estaSeleccionado ? 'ring-2 ring-primary-500 bg-primary-100' : ''}
                `}
              >
                <div className={`text-sm font-medium mb-1 ${esHoy ? 'text-primary-600' : 'text-gray-700'}`}>
                  {dia.fecha.getDate()}
                </div>
                <div className="space-y-1">
                  {citasDelDia.slice(0, 2).map((cita, idx) => (
                    <div
                      key={idx}
                      className={`text-xs px-1 py-0.5 rounded border ${getEstadoColor(cita.estado)}`}
                      title={`${formatearHora(cita.horaCita)} - ${cita.mascota?.nombre || 'Sin mascota'}`}
                    >
                      <div className="truncate">{formatearHora(cita.horaCita)}</div>
                    </div>
                  ))}
                  {citasDelDia.length > 2 && (
                    <div className="text-xs text-gray-500">
                      +{citasDelDia.length - 2} más
                    </div>
                  )}
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Detalles del día seleccionado */}
      {selectedDate && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-semibold mb-4">
            Citas del {selectedDate.toLocaleDateString('es-ES', { 
              weekday: 'long', 
              year: 'numeric', 
              month: 'long', 
              day: 'numeric' 
            })}
          </h3>

          {citasDelDia.length > 0 ? (
            <div className="space-y-3">
              {citasDelDia.map((cita) => (
                <div
                  key={cita.idCita}
                  className={`border rounded-lg p-4 ${getEstadoColor(cita.estado)}`}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <Clock className="w-4 h-4" />
                        <span className="font-semibold">{formatearHora(cita.horaCita)}</span>
                        <span className="text-xs px-2 py-1 rounded bg-white/50">
                          {cita.estado}
                        </span>
                      </div>
                      <div className="space-y-1 text-sm">
                        <p><span className="font-medium">Mascota:</span> {cita.mascota?.nombre || 'N/A'}</p>
                        <p><span className="font-medium">Propietario:</span> {cita.mascota?.propietario?.nombres} {cita.mascota?.propietario?.apellidos}</p>
                        <p><span className="font-medium">Servicio:</span> {cita.servicio?.nombre || 'N/A'}</p>
                        {cita.motivo && (
                          <p><span className="font-medium">Motivo:</span> {cita.motivo}</p>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <Calendar className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p>No hay citas programadas para este día</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

