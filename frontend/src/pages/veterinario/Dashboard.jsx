import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { citaService, veterinarioService } from '../../services/api';
import { Calendar, Clock } from 'lucide-react';

export default function VeterinarioDashboard() {
  const { user } = useAuth();
  const [citasHoy, setCitasHoy] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user) {
      loadCitasHoy();
    }
  }, [user]);

  const loadCitasHoy = async () => {
    try {
      setLoading(true);
      
      let veterinario = null;
      
      // Intentar obtener el perfil usando el endpoint mi-perfil
      try {
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        veterinario = veterinarioRes.data;
      } catch (error404) {
        // Si el endpoint no existe (404), buscar en todos los veterinarios por email o por usuario
        try {
          const todosVeterinarios = await veterinarioService.getAll();
          
          // Buscar por email del veterinario (campo correo)
          veterinario = todosVeterinarios.data.find(v => 
            v.correo && v.correo.toLowerCase() === user?.email?.toLowerCase()
          );
          
          // Si no se encuentra por email del veterinario, buscar por email del usuario asociado
          if (!veterinario) {
            veterinario = todosVeterinarios.data.find(v => 
              v.usuario?.email && v.usuario.email.toLowerCase() === user?.email?.toLowerCase()
            );
          }
          
          // Si no se encuentra por email, buscar por usuario.idUsuario
          if (!veterinario && user?.idUsuario) {
            veterinario = todosVeterinarios.data.find(v => 
              v.usuario && v.usuario.idUsuario === user.idUsuario
            );
          }
          
          // Si aún no se encuentra, intentar buscar por username del usuario asociado
          if (!veterinario && user?.username) {
            veterinario = todosVeterinarios.data.find(v => 
              v.usuario?.username && v.usuario.username.toLowerCase() === user.username.toLowerCase()
            );
          }
          
          // Si aún no se encuentra, intentar buscar por similitud de nombre (último recurso)
          if (!veterinario && user?.username) {
            const userFirstName = user.username.split(' ')[0]?.toLowerCase();
            veterinario = todosVeterinarios.data.find(v => 
              v.nombres && v.nombres.toLowerCase().includes(userFirstName)
            );
          }
        } catch (error2) {
          console.error('Error al buscar veterinarios:', error2);
          setCitasHoy([]);
          return;
        }
      }
      
      if (!veterinario || !veterinario.idPersonal) {
        setCitasHoy([]);
        return;
      }
      
      // Obtener las citas del veterinario usando su idPersonal
      const response = await citaService.getByVeterinario(veterinario.idPersonal);
      const hoy = new Date().toISOString().split('T')[0];
      
      // Filtrar solo las citas de hoy y que estén activas (PROGRAMADA o CONFIRMADA)
      const citas = (response.data || []).filter(c => 
        c.fechaCita === hoy && 
        (c.estado === 'PROGRAMADA' || c.estado === 'CONFIRMADA')
      );
      
      // Ordenar por hora
      citas.sort((a, b) => {
        if (a.horaCita < b.horaCita) return -1;
        if (a.horaCita > b.horaCita) return 1;
        return 0;
      });
      
      setCitasHoy(citas);
    } catch (error) {
      console.error('Error al cargar citas:', error);
      setCitasHoy([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Bienvenido, {user?.nombre}</h2>

      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold mb-4">Citas de Hoy</h3>
        {citasHoy.length > 0 ? (
          <div className="space-y-4">
            {citasHoy.map((cita) => (
              <div key={cita.idCita} className="border rounded-lg p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h4 className="font-semibold">{cita.mascota?.nombre}</h4>
                    <p className="text-sm text-gray-600">Propietario: {cita.mascota?.propietarioNombre || 'N/A'}</p>
                    <p className="text-sm text-gray-600">Servicio: {cita.servicio?.nombre}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium">{cita.horaCita}</p>
                    <span className={`px-2 py-1 text-xs rounded-full ${
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
          <p className="text-gray-500">No tienes citas programadas para hoy</p>
        )}
      </div>
    </div>
  );
}

