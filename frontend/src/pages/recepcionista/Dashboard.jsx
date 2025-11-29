import { useEffect, useState } from 'react';
import { citaService, propietarioService, mascotaService, veterinarioService } from '../../services/api';
import { Calendar, Clock, Users, User, CheckCircle, AlertCircle, TrendingUp, Phone } from 'lucide-react';

export default function RecepcionistaDashboard() {
  const [citasHoy, setCitasHoy] = useState([]);
  const [proximasCitas, setProximasCitas] = useState([]);
  const [estadisticas, setEstadisticas] = useState({
    citasHoy: 0,
    citasPendientes: 0,
    propietariosTotal: 0,
    mascotasTotal: 0,
    veterinariosActivos: 0
  });
  const [citasSinConfirmar, setCitasSinConfirmar] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);

      // Cargar datos en paralelo
      const [citasRes, propietariosRes, mascotasRes, veterinariosRes] = await Promise.all([
        citaService.getAll(),
        propietarioService.getAll(),
        mascotaService.getAll(),
        veterinarioService.getActivos()
      ]);

      const todasCitas = citasRes.data || [];
      const propietarios = propietariosRes.data || [];
      const mascotas = mascotasRes.data || [];
      const veterinarios = veterinariosRes.data || [];

      // Calcular fechas
      const hoy = new Date();
      const hoyStr = hoy.toISOString().split('T')[0];
      const finSemana = new Date(hoy);
      finSemana.setDate(hoy.getDate() + 7);
      const finSemanaStr = finSemana.toISOString().split('T')[0];

      // Filtrar citas de hoy
      const citasDeHoy = todasCitas.filter(c => c.fechaCita === hoyStr)
        .sort((a, b) => (a.horaCita || '').localeCompare(b.horaCita || ''));

      // Filtrar próximas citas (siguientes 7 días, excluyendo hoy)
      const citasProximas = todasCitas.filter(c =>
        c.fechaCita > hoyStr &&
        c.fechaCita <= finSemanaStr &&
        (c.estado === 'PROGRAMADA' || c.estado === 'CONFIRMADA')
      ).sort((a, b) => {
        if (a.fechaCita !== b.fechaCita) {
          return a.fechaCita.localeCompare(b.fechaCita);
        }
        return (a.horaCita || '').localeCompare(b.horaCita || '');
      }).slice(0, 5);

      // Citas sin confirmar (PROGRAMADA pero no CONFIRMADA)
      const sinConfirmar = todasCitas.filter(c =>
        c.estado === 'PROGRAMADA' &&
        c.fechaCita >= hoyStr
      ).slice(0, 5);

      // Calcular estadísticas
      const citasPendientes = todasCitas.filter(c =>
        c.fechaCita >= hoyStr &&
        (c.estado === 'PROGRAMADA' || c.estado === 'CONFIRMADA')
      ).length;

      setCitasHoy(citasDeHoy);
      setProximasCitas(citasProximas);
      setCitasSinConfirmar(sinConfirmar);
      setEstadisticas({
        citasHoy: citasDeHoy.length,
        citasPendientes: citasPendientes,
        propietariosTotal: propietarios.length,
        mascotasTotal: mascotas.length,
        veterinariosActivos: veterinarios.length
      });
    } catch (error) {
      console.error('Error al cargar dashboard:', error);
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

  const getEstadoBadgeClassName = (estado) => {
    switch (estado) {
      case 'CONFIRMADA':
        return 'bg-green-100 text-green-800';
      case 'PROGRAMADA':
        return 'bg-blue-100 text-blue-800';
      case 'COMPLETADA':
      case 'ATENDIDA':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  const statsCards = [
    {
      title: 'Citas Hoy',
      value: estadisticas.citasHoy,
      icon: <Calendar className="w-6 h-6" />,
      color: 'bg-blue-500'
    },
    {
      title: 'Citas Pendientes',
      value: estadisticas.citasPendientes,
      icon: <Clock className="w-6 h-6" />,
      color: 'bg-orange-500'
    },
    {
      title: 'Total Propietarios',
      value: estadisticas.propietariosTotal,
      icon: <Users className="w-6 h-6" />,
      color: 'bg-green-500'
    },
    {
      title: 'Total Mascotas',
      value: estadisticas.mascotasTotal,
      icon: <User className="w-6 h-6" />,
      color: 'bg-purple-500'
    },
    {
      title: 'Veterinarios Activos',
      value: estadisticas.veterinariosActivos,
      icon: <CheckCircle className="w-6 h-6" />,
      color: 'bg-teal-500'
    }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Dashboard Recepcionista</h2>
        <p className="text-gray-600 mt-1">Gestión de citas y atención al cliente</p>
      </div>

      {/* Tarjetas de estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {statsCards.map((stat) => (
          <div key={stat.title} className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
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
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {citasHoy.map((cita) => (
                <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{cita.mascota?.nombre || 'N/A'}</h4>
                      <p className="text-sm text-gray-600 mt-1">
                        <span className="font-medium">Propietario:</span> {cita.mascota?.propietarioNombre || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Veterinario:</span> {cita.veterinario?.nombreCompleto || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Servicio:</span> {cita.servicio?.nombre || 'N/A'}
                      </p>
                    </div>
                    <div className="text-right ml-4">
                      <p className="text-sm font-bold text-primary-600">{cita.horaCita}</p>
                      <span className={`inline-block mt-1 px-2 py-1 text-xs rounded-full ${getEstadoBadgeClassName(cita.estado)}`}>
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
              <p className="text-gray-500">No hay citas programadas para hoy</p>
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
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {proximasCitas.map((cita) => (
                <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{cita.mascota?.nombre || 'N/A'}</h4>
                      <p className="text-sm text-gray-600 mt-1">
                        <span className="font-medium">Propietario:</span> {cita.mascota?.propietarioNombre || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Veterinario:</span> {cita.veterinario?.nombreCompleto || 'N/A'}
                      </p>
                    </div>
                    <div className="text-right ml-4">
                      <p className="text-xs text-gray-500">{cita.fechaCita}</p>
                      <p className="text-sm font-bold text-primary-600">{cita.horaCita}</p>
                      <span className={`inline-block mt-1 px-2 py-1 text-xs rounded-full ${getEstadoBadgeClassName(cita.estado)}`}>
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
              <p className="text-gray-500">No hay citas próximas</p>
            </div>
          )}
        </div>
      </div>

      {/* Citas sin confirmar */}
      {citasSinConfirmar.length > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
          <div className="flex items-start gap-3 mb-4">
            <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
            <div className="flex-1">
              <h4 className="font-semibold text-yellow-900 mb-2">Citas Pendientes de Confirmar ({citasSinConfirmar.length})</h4>
              <div className="space-y-2">
                {citasSinConfirmar.map((cita) => (
                  <div key={cita.idCita} className="bg-white rounded p-3 text-sm">
                    <div className="flex justify-between items-start">
                      <div>
                        <span className="font-medium">{cita.mascota?.nombre}</span> - {cita.fechaCita} a las {cita.horaCita}
                        <p className="text-gray-600 text-xs mt-1">
                          Propietario: {cita.mascota?.propietarioNombre || 'N/A'} - Tel: {cita.mascota?.propietarioTelefono || 'N/A'}
                        </p>
                      </div>
                      <Phone className="w-4 h-4 text-yellow-600" />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <TrendingUp className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Accesos Rápidos</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Gestiona las citas desde el menú "Citas"</li>
              <li>• Consulta horarios de veterinarios en "Horarios"</li>
              <li>• Revisa el catálogo de servicios en "Servicios"</li>
              <li>• Registra nuevos propietarios en "Propietarios"</li>
              <li>• Consulta especies y razas disponibles en "Especies y Razas"</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
