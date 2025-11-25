import { useEffect, useState } from 'react';
import { propietarioService, mascotaService } from '../../services/api';
import { Users, Search, Phone, Mail, MapPin, PawPrint, X } from 'lucide-react';

export default function PropietariosPage() {
  const [propietarios, setPropietarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedPropietario, setSelectedPropietario] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [mascotasPropietario, setMascotasPropietario] = useState([]);
  const [loadingMascotas, setLoadingMascotas] = useState(false);

  useEffect(() => {
    loadPropietarios();
  }, []);

  const loadPropietarios = async () => {
    try {
      setLoading(true);
      setError('');

      const response = await propietarioService.getAll();
      console.log('✅ Propietarios cargados:', response.data);
      setPropietarios(response.data || []);
    } catch (error) {
      console.error('❌ Error al cargar propietarios:', error);
      setError(`Error al cargar propietarios: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalle = async (propietario) => {
    try {
      setSelectedPropietario(propietario);
      setModalOpen(true);
      setLoadingMascotas(true);
      setError('');

      // Cargar mascotas del propietario
      const mascotasRes = await mascotaService.getByPropietario(propietario.idPropietario);
      setMascotasPropietario(mascotasRes.data || []);
    } catch (error) {
      console.error('Error al cargar mascotas:', error);
      setError(`Error al cargar mascotas: ${error.response?.data?.message || error.message}`);
      setMascotasPropietario([]);
    } finally {
      setLoadingMascotas(false);
    }
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setSelectedPropietario(null);
    setMascotasPropietario([]);
    setError('');
  };

  const propietariosFiltrados = propietarios.filter(propietario =>
    propietario.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    propietario.apellidos?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    propietario.correo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    propietario.telefono?.includes(searchTerm) ||
    propietario.dni?.includes(searchTerm)
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Propietarios</h2>
        <button
          onClick={loadPropietarios}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
        >
          Actualizar
        </button>
      </div>

      {error && !modalOpen && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Buscador */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Buscar por nombre, apellido, correo, teléfono o DNI..."
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          />
        </div>
      </div>

      {/* Estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Propietarios</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{propietarios.length}</p>
            </div>
            <div className="bg-blue-500 text-white p-3 rounded-lg">
              <Users className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Resultados de Búsqueda</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{propietariosFiltrados.length}</p>
            </div>
            <div className="bg-green-500 text-white p-3 rounded-lg">
              <Search className="w-6 h-6" />
            </div>
          </div>
        </div>
      </div>

      {/* Tabla de propietarios */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre Completo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">DNI</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Teléfono</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Correo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dirección</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {propietariosFiltrados.map((propietario) => (
                <tr key={propietario.idPropietario} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10 bg-primary-100 rounded-full flex items-center justify-center">
                        <Users className="h-5 w-5 text-primary-600" />
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {propietario.nombres} {propietario.apellidos}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {propietario.dni || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div className="flex items-center">
                      <Phone className="w-4 h-4 text-gray-400 mr-2" />
                      {propietario.telefono || 'N/A'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div className="flex items-center">
                      <Mail className="w-4 h-4 text-gray-400 mr-2" />
                      {propietario.correo || 'N/A'}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {propietario.direccion || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <button
                      onClick={() => handleVerDetalle(propietario)}
                      className="text-primary-600 hover:text-primary-900 font-medium"
                    >
                      Ver Detalle
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {propietariosFiltrados.length === 0 && (
          <div className="text-center py-8 text-gray-500">
            No se encontraron propietarios con los criterios de búsqueda.
          </div>
        )}
      </div>

      {/* Modal de detalle */}
      {modalOpen && selectedPropietario && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h3 className="text-2xl font-bold text-gray-900">
                    {selectedPropietario.nombres} {selectedPropietario.apellidos}
                  </h3>
                  <p className="text-gray-600">Información del Propietario</p>
                </div>
                <button
                  onClick={handleCloseModal}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">
                  {error}
                </div>
              )}

              {/* Información de contacto */}
              <div className="bg-gray-50 p-4 rounded-lg mb-6">
                <h4 className="font-semibold text-gray-900 mb-3">Información de Contacto</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4 text-gray-500" />
                    <div>
                      <span className="text-xs text-gray-500 block">DNI</span>
                      <span className="text-sm font-medium">{selectedPropietario.dni || 'No registrado'}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <Phone className="w-4 h-4 text-gray-500" />
                    <div>
                      <span className="text-xs text-gray-500 block">Teléfono</span>
                      <span className="text-sm font-medium">{selectedPropietario.telefono || 'No registrado'}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <Mail className="w-4 h-4 text-gray-500" />
                    <div>
                      <span className="text-xs text-gray-500 block">Correo Electrónico</span>
                      <span className="text-sm font-medium">{selectedPropietario.correo || 'No registrado'}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <MapPin className="w-4 h-4 text-gray-500" />
                    <div>
                      <span className="text-xs text-gray-500 block">Dirección</span>
                      <span className="text-sm font-medium">{selectedPropietario.direccion || 'No registrada'}</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Mascotas del propietario */}
              <div>
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <PawPrint className="w-5 h-5" />
                  Mascotas Registradas
                </h4>

                {loadingMascotas ? (
                  <div className="text-center py-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
                  </div>
                ) : mascotasPropietario.length > 0 ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {mascotasPropietario.map((mascota) => (
                      <div key={mascota.idMascota} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                        <div className="flex items-start gap-3">
                          <div className="bg-primary-100 p-2 rounded-full flex-shrink-0">
                            <PawPrint className="w-5 h-5 text-primary-600" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <h5 className="font-semibold text-gray-900">{mascota.nombre}</h5>
                            <p className="text-sm text-gray-600 mt-1">
                              {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                            </p>
                            <div className="flex gap-3 mt-2 text-xs text-gray-500">
                              <span>Sexo: {mascota.sexo}</span>
                              {mascota.peso && <span>Peso: {mascota.peso} kg</span>}
                              {mascota.edad && <span>Edad: {mascota.edad}</span>}
                            </div>
                            {mascota.color && (
                              <p className="text-xs text-gray-500 mt-1">Color: {mascota.color}</p>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-8 bg-gray-50 rounded-lg">
                    <PawPrint className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <p className="text-gray-500">Este propietario no tiene mascotas registradas</p>
                  </div>
                )}
              </div>

              <div className="flex justify-end pt-4 border-t mt-6">
                <button
                  onClick={handleCloseModal}
                  className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <Users className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información para Veterinarios</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Consulta la información de contacto de los propietarios</li>
              <li>• Visualiza las mascotas asociadas a cada propietario</li>
              <li>• Usa el buscador para encontrar rápidamente un propietario</li>
              <li>• Esta vista es de solo consulta, no puedes modificar datos</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
