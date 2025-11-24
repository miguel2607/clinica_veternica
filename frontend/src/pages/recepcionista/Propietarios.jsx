import { useEffect, useState } from 'react';
import { propietarioService } from '../../services/api';
import { Plus, Search } from 'lucide-react';

export default function PropietariosPage() {
  const [propietarios, setPropietarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadPropietarios();
  }, []);

  const loadPropietarios = async () => {
    try {
      const response = await propietarioService.getAll();
      setPropietarios(response.data);
    } catch (error) {
      console.error('Error al cargar propietarios:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredPropietarios = propietarios.filter(
    (p) =>
      p.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.apellidos?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.documento?.includes(searchTerm)
  );

  if (loading) {
    return <div className="text-center py-8">Cargando propietarios...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Gestión de Propietarios</h2>
        <button className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center">
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Propietario
        </button>
      </div>

      <div className="bg-white rounded-lg shadow">
        <div className="p-4 border-b">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar propietarios..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Documento</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Teléfono</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredPropietarios.map((propietario) => (
                <tr key={propietario.idPropietario} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {propietario.nombres} {propietario.apellidos}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{propietario.documento}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{propietario.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{propietario.telefono}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button className="text-primary-600 hover:text-primary-900">Ver</button>
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

