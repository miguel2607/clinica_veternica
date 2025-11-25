import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { citaService, veterinarioService, mascotaService, evolucionClinicaService } from '../../services/api';
import { Calendar, Clock, Users, Activity, AlertCircle, CheckCircle, TrendingUp } from 'lucide-react';

export default function VeterinarioDashboard() {
  const { user } = useAuth();
  const [citasHoy, setCitasHoy] = useState([]);
  const [proximasCitas, setProximasCitas] = useState([]);
  const [estadisticas, setEstadisticas] = useState({
    totalCitas: 0,
    citasHoy: 0,
    citasSemana: 0,
    citasCompletadas: 0,
    citasCanceladas: 0,
    citasNoAsistio: 0,
    totalPacientes: 0
  });
  const [loading, setLoading] = useState(true);
  const [veterinario, setVeterinario] = useState(null);

  useEffect(() => {
    if (user) {
      loadDashboardData();
    }
  }, [user]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);

      console.log('🔍 Iniciando carga del dashboard del veterinario...');
      console.log('👤 Usuario actual:', user);

      // Obtener el perfil del veterinario
      let vet = null;
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
          console.log('📋 Veterinarios completos:', todosVeterinarios.data);
          
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
              console.log(`🔍 Comparando usuario: v.usuario=${v.usuario}, v.usuario.idUsuario=${v.usuario?.idUsuario} === ${user.idUsuario} = ${match}`);
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
            const nombreUsuario = (user.nombre || user.username || '').toLowerCase().split(' ')[0]; // Primer nombre
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
          return;
        }
      }

      if (!vet || !vet.idPersonal) {
        console.error('❌ No se pudo encontrar el veterinario o no tiene idPersonal');
        console.error('Veterinario encontrado:', vet);
        return;
      }

      console.log('✅ Veterinario válido encontrado. ID:', vet.idPersonal);
      setVeterinario(vet);

      // Obtener todas las citas del veterinario
      console.log('📞 Obteniendo citas del veterinario ID:', vet.idPersonal);
      const citasRes = await citaService.getByVeterinario(vet.idPersonal);
      const todasCitas = citasRes.data || [];
      console.log('✅ Total de citas obtenidas:', todasCitas.length);
      console.log('📋 Citas:', todasCitas);
      console.log('📊 Estados de las citas:', todasCitas.map(c => ({ id: c.idCita, estado: c.estado, fecha: c.fechaCita })));

      // Calcular fechas
      const hoy = new Date();
      hoy.setHours(0, 0, 0, 0); // Normalizar a medianoche
      const hoyStr = hoy.toISOString().split('T')[0];
      const finSemana = new Date(hoy);
      finSemana.setDate(hoy.getDate() + 7);
      const finSemanaStr = finSemana.toISOString().split('T')[0];

      console.log('📅 Fecha de hoy:', hoyStr);
      console.log('📅 Fin de semana (7 días):', finSemanaStr);

      const normalizarEstado = (estado) => (estado || '').toUpperCase().trim();

      // Filtrar citas de hoy (incluir PROGRAMADA, CONFIRMADA, EN_ATENCION, EN_ATENCIÓN)
      const citasDeHoy = todasCitas.filter(c => {
        const estado = normalizarEstado(c.estado);
        const esHoy = c.fechaCita === hoyStr;
        const esActiva = estado === 'PROGRAMADA' || 
                        estado === 'CONFIRMADA' || 
                        estado === 'EN_ATENCION' || 
                        estado === 'EN_ATENCIÓN';
        return esHoy && esActiva;
      }).sort((a, b) => (a.horaCita || '').localeCompare(b.horaCita || ''));
      
      console.log('📅 Citas de hoy:', citasDeHoy.length, citasDeHoy);

      // Filtrar próximas citas (siguientes 7 días, excluyendo hoy)
      const citasProximas = todasCitas.filter(c => {
        const estado = normalizarEstado(c.estado);
        return c.fechaCita > hoyStr &&
          c.fechaCita <= finSemanaStr &&
          (estado === 'PROGRAMADA' || estado === 'CONFIRMADA');
      }).sort((a, b) => {
        if (a.fechaCita !== b.fechaCita) {
          return a.fechaCita.localeCompare(b.fechaCita);
        }
        return (a.horaCita || '').localeCompare(b.horaCita || '');
      }).slice(0, 5);
      
      console.log('📅 Próximas citas (7 días):', citasProximas.length, citasProximas);

      // Calcular estadísticas
      const citasSemana = todasCitas.filter(c => {
        const estado = normalizarEstado(c.estado);
        return c.fechaCita >= hoyStr &&
          c.fechaCita <= finSemanaStr &&
          (estado === 'PROGRAMADA' || estado === 'CONFIRMADA');
      }).length;

      const citasCompletadas = todasCitas.filter(c => {
        const estado = normalizarEstado(c.estado);
        return estado === 'ATENDIDA';
      }).length;

      const citasCanceladas = todasCitas.filter(c => normalizarEstado(c.estado) === 'CANCELADA').length;
      const citasNoAsistio = todasCitas.filter(c => normalizarEstado(c.estado) === 'NO_ASISTIO' || normalizarEstado(c.estado) === 'NO ASISTIO').length;

      console.log('📊 Estadísticas calculadas:', {
        totalCitas: todasCitas.length,
        citasHoy: citasDeHoy.length,
        citasSemana,
        citasCompletadas,
        citasCanceladas,
        citasNoAsistio
      });

      // Obtener total de pacientes únicos
      const mascotasUnicas = new Set(todasCitas.map(c => c.mascota?.idMascota).filter(Boolean));

      setCitasHoy(citasDeHoy);
      setProximasCitas(citasProximas);
      setEstadisticas({
        totalCitas: todasCitas.length,
        citasHoy: citasDeHoy.length,
        citasSemana,
        citasCompletadas,
        citasCanceladas,
        citasNoAsistio,
        totalPacientes: mascotasUnicas.size
      });
    } catch (error) {
      console.error('❌ Error al cargar dashboard:', error);
      console.error('❌ Detalles del error:', error.response?.data || error.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const statsCards = [
    {
      title: 'Total de Citas',
      value: estadisticas.totalCitas,
      icon: <TrendingUp className="w-6 h-6" />,
      color: 'bg-indigo-500',
      textColor: 'text-indigo-600'
    },
    {
      title: 'Citas Hoy',
      value: estadisticas.citasHoy,
      icon: <Calendar className="w-6 h-6" />,
      color: 'bg-blue-500',
      textColor: 'text-blue-600'
    },
    {
      title: 'Citas esta Semana',
      value: estadisticas.citasSemana,
      icon: <Clock className="w-6 h-6" />,
      color: 'bg-green-500',
      textColor: 'text-green-600'
    },
    {
      title: 'Total Pacientes',
      value: estadisticas.totalPacientes,
      icon: <Users className="w-6 h-6" />,
      color: 'bg-purple-500',
      textColor: 'text-purple-600'
    },
    {
      title: 'Citas Completadas',
      value: estadisticas.citasCompletadas,
      icon: <CheckCircle className="w-6 h-6" />,
      color: 'bg-orange-500',
      textColor: 'text-orange-600'
    },
    {
      title: 'Citas Canceladas',
      value: estadisticas.citasCanceladas,
      icon: <AlertCircle className="w-6 h-6" />,
      color: 'bg-red-500',
      textColor: 'text-red-600'
    },
    {
      title: 'No asistieron',
      value: estadisticas.citasNoAsistio,
      icon: <Activity className="w-6 h-6" />,
      color: 'bg-yellow-500',
      textColor: 'text-yellow-600'
    }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Bienvenido, Dr(a). {veterinario?.nombres || user?.nombre}</h2>
        <p className="text-gray-600 mt-1">Aquí está el resumen de tu día</p>
      </div>

      {/* Tarjetas de estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {statsCards.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
              </div>
              <div className={`${stat.color} text-white p-3 rounded-lg`}>
                {stat.icon}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Citas de Hoy */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Citas de Hoy</h3>
            <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded">
              {citasHoy.length}
            </span>
          </div>

          {citasHoy.length > 0 ? (
            <div className="space-y-3">
              {citasHoy.map((cita) => (
                <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{cita.mascota?.nombre}</h4>
                      <p className="text-sm text-gray-600 mt-1">
                        <span className="font-medium">Propietario:</span> {cita.mascota?.propietarioNombre || cita.propietario?.nombres || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Servicio:</span> {cita.servicio?.nombre || 'N/A'}
                      </p>
                    </div>
                    <div className="text-right ml-4">
                      <p className="text-sm font-bold text-primary-600">{cita.horaCita}</p>
                      <span className={`inline-block mt-1 px-2 py-1 text-xs rounded-full ${
                        cita.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                      }`}>
                        {cita.estado}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-3" />
              <p className="text-gray-500">No tienes citas programadas para hoy</p>
            </div>
          )}
        </div>

        {/* Próximas Citas */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Próximas Citas (7 días)</h3>
            <span className="bg-green-100 text-green-800 text-xs font-medium px-2.5 py-0.5 rounded">
              {proximasCitas.length}
            </span>
          </div>

          {proximasCitas.length > 0 ? (
            <div className="space-y-3">
              {proximasCitas.map((cita) => (
                <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{cita.mascota?.nombre}</h4>
                      <p className="text-sm text-gray-600 mt-1">
                        <span className="font-medium">Propietario:</span> {cita.mascota?.propietarioNombre || cita.propietario?.nombres || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Servicio:</span> {cita.servicio?.nombre || 'N/A'}
                      </p>
                    </div>
                    <div className="text-right ml-4">
                      <p className="text-xs text-gray-500">{cita.fechaCita}</p>
                      <p className="text-sm font-bold text-primary-600">{cita.horaCita}</p>
                      <span className={`inline-block mt-1 px-2 py-1 text-xs rounded-full ${
                        cita.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                      }`}>
                        {cita.estado}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <Clock className="w-12 h-12 text-gray-400 mx-auto mb-3" />
              <p className="text-gray-500">No hay citas programadas próximamente</p>
            </div>
          )}
        </div>
      </div>

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <Activity className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Accesos Rápidos</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Puedes gestionar tus horarios desde el menú "Mis Horarios"</li>
              <li>• Accede a "Mis Pacientes" para ver el historial completo de tus mascotas</li>
              <li>• Registra evoluciones clínicas en "Evoluciones Clínicas"</li>
              <li>• Consulta el inventario disponible en "Insumos/Inventario"</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

