import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { horarioService, veterinarioService } from '../../services/api';

export default function HorariosPage() {
  const { user } = useAuth();
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filtro, setFiltro] = useState('TODOS'); // TODOS, ACTIVOS, INACTIVOS

  const diasSemana = {
    LUNES: 'Lunes',
    MARTES: 'Martes',
    MIERCOLES: 'Miércoles',
    JUEVES: 'Jueves',
    VIERNES: 'Viernes',
    SABADO: 'Sábado',
    DOMINGO: 'Domingo'
  };

  useEffect(() => {
    if (user) {
      loadHorarios();
    }
  }, [user]);

  const loadHorarios = async () => {
    try {
      setLoading(true);
      setError('');

      console.log('🔍 Obteniendo perfil del veterinario...');
      console.log('👤 Usuario actual:', user);

      let veterinario = null;

      // Intentar obtener el perfil usando el endpoint mi-perfil
      try {
        console.log('📞 Intentando obtener perfil con obtenerMiPerfil()...');
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        veterinario = veterinarioRes.data;
        console.log('✅ Veterinario obtenido con obtenerMiPerfil:', veterinario);
      } catch (error404) {
        console.log('⚠️ obtenerMiPerfil falló, buscando manualmente...', error404);
        // Si no existe el endpoint, buscar manualmente
        try {
          const todosVeterinarios = await veterinarioService.getAll();
          console.log('📋 Total de veterinarios encontrados:', todosVeterinarios.data?.length || 0);
          
          // Buscar por email (correo)
          veterinario = todosVeterinarios.data.find(v => {
            const match = v.correo && v.correo.toLowerCase() === user?.email?.toLowerCase();
            console.log(`🔍 Comparando email (correo): "${v.correo}" === "${user?.email}" = ${match}`);
            return match;
          });

          // Buscar por email del usuario asociado
          if (!veterinario) {
            veterinario = todosVeterinarios.data.find(v => {
              const match = v.usuario?.email && v.usuario.email.toLowerCase() === user?.email?.toLowerCase();
              console.log(`🔍 Comparando email (usuario.email): "${v.usuario?.email}" === "${user?.email}" = ${match}`);
              return match;
            });
          }

          // Buscar por idUsuario
          if (!veterinario && user?.idUsuario) {
            console.log(`🔍 Buscando por idUsuario: ${user.idUsuario}`);
            veterinario = todosVeterinarios.data.find(v => {
              const match = v.usuario && v.usuario.idUsuario === user.idUsuario;
              console.log(`🔍 Comparando usuario: v.usuario.idUsuario=${v.usuario?.idUsuario} === ${user.idUsuario} = ${match}`);
              return match;
            });
          }

          // Buscar por username
          if (!veterinario && user?.username) {
            console.log(`🔍 Buscando por username: ${user.username}`);
            veterinario = todosVeterinarios.data.find(v => {
              const match = v.usuario?.username && v.usuario.username === user.username;
              console.log(`🔍 Comparando username: v.usuario.username="${v.usuario?.username}" === "${user.username}" = ${match}`);
              return match;
            });
          }

          // Último recurso: buscar por similitud de nombre
          if (!veterinario && (user?.nombre || user?.username)) {
            console.log('🔍 Buscando por similitud de nombre (último recurso)...');
            const nombreUsuario = (user.nombre || user.username || '').toLowerCase().split(' ')[0];
            veterinario = todosVeterinarios.data.find(v => {
              const match = v.nombres && v.nombres.toLowerCase().includes(nombreUsuario);
              console.log(`🔍 Comparando nombre: v.nombres="${v.nombres}" incluye "${nombreUsuario}" = ${match}`);
              return match;
            });
          }
          
          if (veterinario) {
            console.log('✅ Veterinario encontrado manualmente:', veterinario);
          } else {
            console.error('❌ No se encontró veterinario con ningún método');
          }
        } catch (error2) {
          console.error('❌ Error al buscar veterinarios:', error2);
          setError('Error al buscar el perfil del veterinario');
          setHorarios([]);
          return;
        }
      }

      if (!veterinario || !veterinario.idPersonal) {
        console.error('❌ No se pudo encontrar el veterinario o no tiene idPersonal');
        setError('No se encontró un perfil de veterinario asociado a tu usuario. Por favor, contacta al administrador.');
        setHorarios([]);
        return;
      }

      console.log('🔍 Obteniendo horarios del veterinario ID:', veterinario.idPersonal);
      const response = await horarioService.getByVeterinario(veterinario.idPersonal);
      console.log('✅ Horarios obtenidos:', response.data);

      // Ordenar horarios por día de la semana
      const horariosPorDia = response.data || [];
      const ordenDias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
      horariosPorDia.sort((a, b) => {
        return ordenDias.indexOf(a.diaSemana) - ordenDias.indexOf(b.diaSemana);
      });

      setHorarios(horariosPorDia);
    } catch (error) {
      console.error('❌ Error al cargar horarios:', error);
      setError(`Error al cargar los horarios: ${error.response?.data?.message || error.message}`);
      setHorarios([]);
    } finally {
      setLoading(false);
    }
  };

  const horariosFiltrados = horarios.filter(horario => {
    if (filtro === 'ACTIVOS') return horario.activo === true;
    if (filtro === 'INACTIVOS') return horario.activo === false;
    return true; // TODOS
  });

  // Agrupar horarios por día de la semana
  const horariosAgrupados = horariosFiltrados.reduce((acc, horario) => {
    const dia = horario.diaSemana;
    if (!acc[dia]) {
      acc[dia] = [];
    }
    acc[dia].push(horario);
    return acc;
  }, {});

  if (loading) {
    return <div className="text-center py-8">Cargando horarios...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Mis Horarios</h2>
        <div className="flex gap-3">
          <select
            value={filtro}
            onChange={(e) => setFiltro(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          >
            <option value="TODOS">Todos</option>
            <option value="ACTIVOS">Solo activos</option>
            <option value="INACTIVOS">Solo inactivos</option>
          </select>
          <button
            onClick={loadHorarios}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
          >
            Actualizar
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {horariosFiltrados.length > 0 ? (
        <>
          {/* Vista de Tabla */}
          <div className="bg-white rounded-lg shadow overflow-hidden mb-6">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Día</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Inicio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Fin</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {horariosFiltrados.map((horario) => (
                    <tr key={horario.idHorario} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {diasSemana[horario.diaSemana] || horario.diaSemana}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {horario.horaInicio}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {horario.horaFin}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                          horario.activo
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {horario.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Vista de Calendario Semanal */}
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Vista Semanal</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {Object.entries(horariosAgrupados).map(([dia, horariosDelDia]) => (
                <div key={dia} className="border border-gray-200 rounded-lg p-4">
                  <h4 className="font-semibold text-primary-600 mb-2">
                    {diasSemana[dia] || dia}
                  </h4>
                  <div className="space-y-2">
                    {horariosDelDia.map((horario) => (
                      <div
                        key={horario.idHorario}
                        className={`p-2 rounded ${
                          horario.activo
                            ? 'bg-green-50 border border-green-200'
                            : 'bg-gray-50 border border-gray-200'
                        }`}
                      >
                        <div className="text-sm font-medium">
                          {horario.horaInicio} - {horario.horaFin}
                        </div>
                        {!horario.activo && (
                          <div className="text-xs text-gray-500 mt-1">Inactivo</div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Resumen */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div className="flex items-start gap-3">
              <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div className="flex-1">
                <h4 className="font-semibold text-blue-900 mb-1">Información sobre tus horarios</h4>
                <p className="text-sm text-blue-800">
                  Tienes un total de <span className="font-semibold">{horarios.length}</span> horarios configurados.
                  {filtro === 'TODOS' && horarios.some(h => h.activo === false) && (
                    <span> Los horarios inactivos no estarán disponibles para agendar citas.</span>
                  )}
                </p>
                <p className="text-sm text-blue-800 mt-2">
                  Si necesitas modificar tus horarios, por favor contacta al administrador del sistema.
                </p>
              </div>
            </div>
          </div>
        </>
      ) : (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          {error ? '' : 'No tienes horarios configurados. Por favor, contacta al administrador para configurar tus horarios de atención.'}
        </div>
      )}
    </div>
  );
}
