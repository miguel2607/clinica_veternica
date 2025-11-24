import { useEffect, useState } from 'react';
import { citaService, servicioService, veterinarioService } from '../../services/api';
import { Plus } from 'lucide-react';

export default function CitasPage() {
  const [citas, setCitas] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [citasRes, serviciosRes, veterinariosRes] = await Promise.all([
        citaService.getAll(),
        servicioService.getAll(),
        veterinarioService.getAll(),
      ]);
      setCitas(citasRes.data);
      setServicios(serviciosRes.data);
      setVeterinarios(veterinariosRes.data);
    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Gestión de Citas</h2>
        <button
          onClick={() => setShowModal(true)}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Cita
        </button>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {citas.map((cita) => (
                <tr key={cita.idCita} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {cita.mascota?.nombre}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {cita.veterinario?.nombreCompleto}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.fechaCita}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.horaCita}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                      cita.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                    }`}>
                      {cita.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button className="text-primary-600 hover:text-primary-900 mr-4">Ver</button>
                    {cita.estado === 'PROGRAMADA' && (
                      <button className="text-green-600 hover:text-green-900">Confirmar</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

