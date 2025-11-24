import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { mascotaService, citaService, propietarioService } from '../../services/api';
import { User, Calendar } from 'lucide-react';

export default function PropietarioDashboard() {
  const { user } = useAuth();
  const [mascotas, setMascotas] = useState([]);
  const [citasProgramadas, setCitasProgramadas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, [user]);

  const loadData = async () => {
    try {
      setLoading(true);
      
      // Obtener o crear propietario automáticamente
      if (user?.rol === 'PROPIETARIO') {
        try {
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          
          // Obtener mascotas del propietario
          if (propietarioRes.data?.idPropietario) {
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            setMascotas(mascotasRes.data || []);
            
            // Obtener citas y filtrar por propietario y estado PROGRAMADA
            const citasRes = await citaService.getAll();
            const mascotasIds = (mascotasRes.data || []).map(m => m.idMascota);
            const citasFiltradas = (citasRes.data || []).filter(cita => 
              cita.mascota?.idMascota && 
              mascotasIds.includes(cita.mascota.idMascota) &&
              cita.estado === 'PROGRAMADA'
            );
            
            // Ordenar por fecha más próxima
            citasFiltradas.sort((a, b) => {
              const fechaA = new Date(a.fechaCita + ' ' + a.horaCita);
              const fechaB = new Date(b.fechaCita + ' ' + b.horaCita);
              return fechaA - fechaB;
            });
            
            setCitasProgramadas(citasFiltradas);
          } else {
            setMascotas([]);
            setCitasProgramadas([]);
          }
        } catch (error) {
          console.error('Error al obtener/crear propietario:', error);
          setMascotas([]);
          setCitasProgramadas([]);
        }
      } else if (user?.email) {
        // Fallback: intentar buscar por email
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          if (propietarioRes.data?.idPropietario) {
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            setMascotas(mascotasRes.data || []);
            
            const citasRes = await citaService.getAll();
            const mascotasIds = (mascotasRes.data || []).map(m => m.idMascota);
            const citasFiltradas = (citasRes.data || []).filter(cita => 
              cita.mascota?.idMascota && 
              mascotasIds.includes(cita.mascota.idMascota) &&
              cita.estado === 'PROGRAMADA'
            );
            
            citasFiltradas.sort((a, b) => {
              const fechaA = new Date(a.fechaCita + ' ' + a.horaCita);
              const fechaB = new Date(b.fechaCita + ' ' + b.horaCita);
              return fechaA - fechaB;
            });
            
            setCitasProgramadas(citasFiltradas);
          } else {
            setMascotas([]);
            setCitasProgramadas([]);
          }
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
          setMascotas([]);
          setCitasProgramadas([]);
        }
      } else {
        setMascotas([]);
        setCitasProgramadas([]);
      }
    } catch (error) {
      console.error('Error al cargar datos:', error);
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

  return (
    <div className="space-y-6 animate-fadeIn">
      <h2 className="text-2xl font-bold text-gray-900">Bienvenido, {user?.nombre || user?.username}</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Mis Mascotas</h3>
            <User className="w-8 h-8 text-primary-600" />
          </div>
          <p className="text-3xl font-bold text-primary-600">{mascotas.length}</p>
          <p className="text-sm text-gray-500 mt-2">mascotas registradas</p>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-900">Citas Programadas</h3>
            <Calendar className="w-8 h-8 text-primary-600" />
          </div>
          <p className="text-3xl font-bold text-primary-600">{citasProgramadas.length}</p>
          <p className="text-sm text-gray-500 mt-2">próximas citas</p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-lg p-6 animate-scaleIn">
        <h3 className="text-xl font-semibold text-gray-900 mb-4">Próximas Citas Programadas</h3>
        {citasProgramadas.length > 0 ? (
          <div className="space-y-3">
            {citasProgramadas.slice(0, 5).map((cita) => (
              <div key={cita.idCita} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <p className="font-semibold text-gray-900">{cita.mascota?.nombre}</p>
                    <p className="text-sm text-gray-600 mt-1">{cita.servicio?.nombre}</p>
                    <p className="text-sm text-gray-500 mt-1">Veterinario: {cita.veterinario?.nombreCompleto}</p>
                    {cita.motivo && (
                      <p className="text-sm text-gray-500 mt-1">Motivo: {cita.motivo}</p>
                    )}
                  </div>
                  <div className="text-right ml-4">
                    <p className="text-sm font-medium text-gray-900">{cita.fechaCita}</p>
                    <p className="text-sm text-gray-600">{cita.horaCita}</p>
                    <span className="mt-2 inline-block px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {cita.estado}
                    </span>
                  </div>
                </div>
              </div>
            ))}
            {citasProgramadas.length > 5 && (
              <p className="text-sm text-gray-500 text-center mt-4">
                Y {citasProgramadas.length - 5} cita(s) más...
              </p>
            )}
          </div>
        ) : (
          <div className="text-center py-8">
            <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-500">No tienes citas programadas</p>
          </div>
        )}
      </div>
    </div>
  );
}
