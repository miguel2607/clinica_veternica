import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { mascotaService, citaService, propietarioService, vacunacionService, historiaClinicaService } from '../../services/api';
import { User, Calendar, Syringe, FileText, AlertCircle, PawPrint, Activity } from 'lucide-react';

export default function PropietarioDashboard() {
  const { user } = useAuth();
  const [mascotas, setMascotas] = useState([]);
  const [citasProgramadas, setCitasProgramadas] = useState([]);
  const [citasProximas, setCitasProximas] = useState([]);
  const [vacunacionesPendientes, setVacunacionesPendientes] = useState([]);
  const [estadisticas, setEstadisticas] = useState({
    totalMascotas: 0,
    citasProgramadas: 0,
    citasEsteMes: 0,
    historiasClinicas: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [user]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);

      // Obtener o crear propietario
      let propietario = null;
      if (user?.rol === 'PROPIETARIO') {
        try {
          console.log('🔍 Obteniendo perfil de propietario...');
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          console.log('✅ Perfil obtenido:', propietarioRes.data);
          propietario = propietarioRes.data;
        } catch (error) {
          console.error('❌ Error al obtener propietario:', error);
          console.error('❌ Detalles del error:', {
            status: error.response?.status,
            message: error.response?.data?.message,
            url: error.config?.url
          });
          
          // Si es un 401, podría ser un problema de token o permisos
          if (error.response?.status === 401) {
            console.warn('⚠️ Error 401 al obtener perfil. Verificando token...');
            // No hacer nada aquí, el interceptor manejará la redirección si es necesario
          }
        }
      } else if (user?.email) {
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          propietario = propietarioRes.data;
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
        }
      }

      if (!propietario || !propietario.idPropietario) {
        setMascotas([]);
        setCitasProgramadas([]);
        setCitasProximas([]);
        setVacunacionesPendientes([]);
        return;
      }

      // Obtener mascotas del propietario
      const mascotasRes = await mascotaService.getByPropietario(propietario.idPropietario);
      const misMascotas = mascotasRes.data || [];
      setMascotas(misMascotas);

      if (misMascotas.length === 0) {
        setCitasProgramadas([]);
        setCitasProximas([]);
        setVacunacionesPendientes([]);
        return;
      }

      const mascotasIds = misMascotas.map(m => m.idMascota);

      // Obtener citas
      const citasRes = await citaService.getAll();
      const todasCitas = citasRes.data || [];

      // Filtrar citas del propietario
      const citasPropietario = todasCitas.filter(cita =>
        cita.mascota?.idMascota && mascotasIds.includes(cita.mascota.idMascota)
      );

      // Calcular fechas
      const hoy = new Date();
      const hoyStr = hoy.toISOString().split('T')[0];
      const inicioMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1).toISOString().split('T')[0];
      const finMes = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0).toISOString().split('T')[0];

      // Citas programadas (futuras)
      const programadas = citasPropietario.filter(cita =>
        cita.estado === 'PROGRAMADA' &&
        cita.fechaCita >= hoyStr
      ).sort((a, b) => {
        if (a.fechaCita !== b.fechaCita) {
          return a.fechaCita.localeCompare(b.fechaCita);
        }
        return (a.horaCita || '').localeCompare(b.horaCita || '');
      });

      // Próximas 5 citas
      const proximas = programadas.slice(0, 5);

      // Citas de este mes
      const citasDelMes = citasPropietario.filter(cita =>
        cita.fechaCita >= inicioMes && cita.fechaCita <= finMes
      ).length;

      // Obtener historias clínicas
      let historiasCount = 0;
      try {
        for (const mascota of misMascotas) {
          const historiasRes = await historiaClinicaService.getByMascota(mascota.idMascota);
          const historiasData = historiasRes.data;
          
          if (Array.isArray(historiasData)) {
            historiasCount += historiasData.length;
          } else if (historiasData) {
            // El backend puede devolver un único objeto en vez de lista
            historiasCount += 1;
          }
        }
      } catch (error) {
        console.error('Error al contar historias:', error);
      }

      // Obtener vacunaciones y buscar próximas dosis
      const vacunasPendientes = [];
      try {
        for (const mascota of misMascotas) {
          const historiasRes = await historiaClinicaService.getByMascota(mascota.idMascota);
          const historias = historiasRes.data || [];

          for (const historia of historias) {
            try {
              const vacunasRes = await vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica);
              const vacunas = vacunasRes.data || [];

              // Buscar vacunas con próxima dosis
              vacunas.forEach(vacuna => {
                if (vacuna.proximaDosis && vacuna.proximaDosis >= hoyStr) {
                  vacunasPendientes.push({
                    ...vacuna,
                    mascota: mascota
                  });
                }
              });
            } catch (error) {
              console.error('Error al obtener vacunaciones:', error);
            }
          }
        }
      } catch (error) {
        console.error('Error al buscar vacunaciones:', error);
      }

      // Ordenar vacunas pendientes por fecha más próxima
      vacunasPendientes.sort((a, b) =>
        (a.proximaDosis || '').localeCompare(b.proximaDosis || '')
      );

      setCitasProgramadas(programadas);
      setCitasProximas(proximas);
      setVacunacionesPendientes(vacunasPendientes.slice(0, 5));
      setEstadisticas({
        totalMascotas: misMascotas.length,
        citasProgramadas: programadas.length,
        citasEsteMes: citasDelMes,
        historiasClinicas: historiasCount || 0
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

  const statsCards = [
    {
      title: 'Mis Mascotas',
      value: estadisticas.totalMascotas,
      icon: <PawPrint className="w-6 h-6" />,
      color: 'bg-blue-500'
    },
    {
      title: 'Citas Programadas',
      value: estadisticas.citasProgramadas,
      icon: <Calendar className="w-6 h-6" />,
      color: 'bg-green-500'
    },
    {
      title: 'Citas Este Mes',
      value: estadisticas.citasEsteMes,
      icon: <Activity className="w-6 h-6" />,
      color: 'bg-purple-500'
    },
    {
      title: 'Historias Clínicas',
      value: estadisticas.historiasClinicas,
      icon: <FileText className="w-6 h-6" />,
      color: 'bg-orange-500'
    }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Bienvenido, {user?.nombre || user?.username}</h2>
        <p className="text-gray-600 mt-1">Portal de propietarios - Gestiona la salud de tus mascotas</p>
      </div>

      {/* Tarjetas de estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
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
        {/* Próximas Citas */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Próximas Citas</h3>
            <span className="bg-green-100 text-green-800 text-xs font-medium px-2.5 py-0.5 rounded">
              {citasProximas.length}
            </span>
          </div>

          {citasProximas.length > 0 ? (
            <div className="space-y-3">
              {citasProximas.map((cita) => (
                <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{cita.mascota?.nombre}</h4>
                      <p className="text-sm text-gray-600 mt-1">
                        <span className="font-medium">Servicio:</span> {cita.servicio?.nombre || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Veterinario:</span> {cita.veterinario?.nombres || 'N/A'}
                      </p>
                      {cita.motivo && (
                        <p className="text-xs text-gray-500 mt-1">{cita.motivo}</p>
                      )}
                    </div>
                    <div className="text-right ml-4">
                      <p className="text-xs text-gray-500">{cita.fechaCita}</p>
                      <p className="text-sm font-bold text-primary-600">{cita.horaCita}</p>
                      <span className="inline-block mt-1 px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
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
              <p className="text-gray-500">No tienes citas programadas</p>
            </div>
          )}
        </div>

        {/* Mis Mascotas */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Mis Mascotas</h3>
            <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded">
              {mascotas.length}
            </span>
          </div>

          {mascotas.length > 0 ? (
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {mascotas.map((mascota) => (
                <div key={mascota.idMascota} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex items-start gap-3">
                    <div className="bg-primary-100 p-2 rounded-full">
                      <PawPrint className="w-5 h-5 text-primary-600" />
                    </div>
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{mascota.nombre}</h4>
                      <p className="text-sm text-gray-600">
                        {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                      </p>
                      <div className="flex gap-4 mt-2 text-xs text-gray-500">
                        <span>Sexo: {mascota.sexo}</span>
                        {mascota.peso && <span>Peso: {mascota.peso} kg</span>}
                        {mascota.edad && <span>Edad: {mascota.edad}</span>}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <User className="w-12 h-12 text-gray-400 mx-auto mb-3" />
              <p className="text-gray-500">No tienes mascotas registradas</p>
            </div>
          )}
        </div>
      </div>

      {/* Vacunaciones Pendientes */}
      {vacunacionesPendientes.length > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
          <div className="flex items-start gap-3 mb-4">
            <Syringe className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
            <div className="flex-1">
              <h4 className="font-semibold text-yellow-900 mb-2">Vacunaciones Próximas ({vacunacionesPendientes.length})</h4>
              <div className="space-y-2">
                {vacunacionesPendientes.map((vacuna, index) => (
                  <div key={index} className="bg-white rounded p-3 text-sm">
                    <div className="flex justify-between items-start">
                      <div>
                        <span className="font-medium">{vacuna.mascota?.nombre}</span> - {vacuna.nombreVacuna}
                        <p className="text-gray-600 text-xs mt-1">
                          Próxima dosis: {vacuna.proximaDosis}
                        </p>
                      </div>
                      <AlertCircle className="w-4 h-4 text-yellow-600" />
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
          <Activity className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Accesos Rápidos</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Consulta tus mascotas en "Mis Mascotas"</li>
              <li>• Revisa tus citas programadas en "Mis Citas"</li>
              <li>• Accede a las historias clínicas en "Historias Clínicas"</li>
              <li>• Revisa el calendario de vacunaciones en "Vacunaciones"</li>
              <li>• Actualiza tu información en "Mi Perfil"</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
