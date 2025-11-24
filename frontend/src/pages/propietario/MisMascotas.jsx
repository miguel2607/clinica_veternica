import { useEffect, useState } from 'react';
import { mascotaService, propietarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function MisMascotasPage() {
  const { user } = useAuth();
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadMascotas();
  }, [user]);

  const loadMascotas = async () => {
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
          } else {
            setMascotas([]);
          }
        } catch (error) {
          console.error('Error al obtener/crear propietario:', error);
          setMascotas([]);
        }
      } else if (user?.email) {
        // Fallback: intentar buscar por email
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          if (propietarioRes.data?.idPropietario) {
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            setMascotas(mascotasRes.data || []);
          } else {
            setMascotas([]);
          }
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
          setMascotas([]);
        }
      } else {
        setMascotas([]);
      }
    } catch (error) {
      console.error('Error al cargar mascotas:', error);
      setMascotas([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando mascotas...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Mis Mascotas</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {mascotas.map((mascota) => (
          <div key={mascota.idMascota} className="bg-white rounded-lg shadow p-6">
            <h3 className="text-xl font-semibold mb-2">{mascota.nombre}</h3>
            <div className="space-y-2 text-sm text-gray-600">
              <p><span className="font-medium">Especie:</span> {mascota.especie?.nombre}</p>
              <p><span className="font-medium">Raza:</span> {mascota.raza?.nombre}</p>
              <p><span className="font-medium">Edad:</span> {mascota.fechaNacimiento ? 
                Math.floor((new Date() - new Date(mascota.fechaNacimiento)) / (365.25 * 24 * 60 * 60 * 1000)) + ' años' 
                : 'N/A'}</p>
            </div>
            <button className="mt-4 w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700">
              Ver Detalles
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

