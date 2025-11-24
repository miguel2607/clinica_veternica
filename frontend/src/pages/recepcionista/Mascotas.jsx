import { useEffect, useState } from 'react';
import { mascotaService } from '../../services/api';
import { Plus, Search } from 'lucide-react';

export default function MascotasPage() {
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadMascotas();
  }, []);

  const loadMascotas = async () => {
    try {
      const response = await mascotaService.getAll();
      setMascotas(response.data);
    } catch (error) {
      console.error('Error al cargar mascotas:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredMascotas = mascotas.filter(
    (m) =>
      m.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      m.propietario?.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return <div className="text-center py-8">Cargando mascotas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Gestión de Mascotas</h2>
        <button className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center">
          <Plus className="w-5 h-5 mr-2" />
          Nueva Mascota
        </button>
      </div>

      <div className="bg-white rounded-lg shadow">
        <div className="p-4 border-b">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar mascotas..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          {filteredMascotas.map((mascota) => (
            <div key={mascota.idMascota} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
              <h3 className="font-semibold text-lg">{mascota.nombre}</h3>
              <p className="text-sm text-gray-600">Propietario: {mascota.propietario?.nombreCompleto}</p>
              <p className="text-sm text-gray-600">Especie: {mascota.especie?.nombre}</p>
              <p className="text-sm text-gray-600">Raza: {mascota.raza?.nombre}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

