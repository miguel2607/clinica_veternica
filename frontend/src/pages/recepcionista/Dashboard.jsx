import { useEffect, useState } from 'react';
import { citaService } from '../../services/api';
import { Calendar, Clock } from 'lucide-react';

export default function RecepcionistaDashboard() {
  const [citasHoy, setCitasHoy] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCitasHoy();
  }, []);

  const loadCitasHoy = async () => {
    try {
      const response = await citaService.getAll();
      const hoy = new Date().toISOString().split('T')[0];
      const citas = response.data.filter(c => c.fechaCita === hoy);
      setCitasHoy(citas);
    } catch (error) {
      console.error('Error al cargar citas:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Dashboard Recepcionista</h2>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Citas Hoy</p>
              <p className="text-3xl font-bold">{citasHoy.length}</p>
            </div>
            <Calendar className="w-12 h-12 text-primary-600" />
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold mb-4">Citas de Hoy</h3>
        {citasHoy.length > 0 ? (
          <div className="space-y-3">
            {citasHoy.map((cita) => (
              <div key={cita.idCita} className="border rounded-lg p-4">
                <div className="flex justify-between">
                  <div>
                    <p className="font-semibold">{cita.mascota?.nombre}</p>
                    <p className="text-sm text-gray-600">{cita.veterinario?.nombreCompleto}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm">{cita.horaCita}</p>
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
          <p className="text-gray-500">No hay citas programadas para hoy</p>
        )}
      </div>
    </div>
  );
}

