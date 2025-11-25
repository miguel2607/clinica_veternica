import { useEffect, useState } from 'react';
import { especieService, razaService } from '../../services/api';

export default function EspeciesRazasPage() {
  const [especies, setEspecies] = useState([]);
  const [razas, setRazas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [especieSeleccionada, setEspecieSeleccionada] = useState('TODAS');
  const [vistaActual, setVistaActual] = useState('especies'); // 'especies' o 'razas'

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      const [especiesRes, razasRes] = await Promise.all([
        especieService.getActivas(),
        razaService.getActivas()
      ]);

      console.log('✅ Especies cargadas:', especiesRes.data);
      console.log('✅ Razas cargadas:', razasRes.data);

      setEspecies(especiesRes.data || []);
      setRazas(razasRes.data || []);
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const especiesFiltradas = especies.filter(especie =>
    especie.nombre?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const razasFiltradas = razas.filter(raza => {
    const matchSearch = raza.nombre?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchEspecie = especieSeleccionada === 'TODAS' || raza.especie?.idEspecie?.toString() === especieSeleccionada;
    return matchSearch && matchEspecie;
  });

  if (loading) {
    return <div className="text-center py-8">Cargando información...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Especies y Razas</h2>
        <button
          onClick={loadData}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
        >
          Actualizar
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Pestañas */}
      <div className="bg-white rounded-lg shadow">
        <div className="border-b">
          <div className="flex">
            <button
              onClick={() => setVistaActual('especies')}
              className={`px-6 py-3 font-medium border-b-2 transition-colors ${
                vistaActual === 'especies'
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              Especies ({especies.length})
            </button>
            <button
              onClick={() => setVistaActual('razas')}
              className={`px-6 py-3 font-medium border-b-2 transition-colors ${
                vistaActual === 'razas'
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              Razas ({razas.length})
            </button>
          </div>
        </div>

        <div className="p-6">
          {/* Filtros */}
          <div className="mb-6 space-y-4">
            <div>
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder={`Buscar ${vistaActual}...`}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            {vistaActual === 'razas' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Filtrar por Especie
                </label>
                <select
                  value={especieSeleccionada}
                  onChange={(e) => setEspecieSeleccionada(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="TODAS">Todas las especies</option>
                  {especies.map(especie => (
                    <option key={especie.idEspecie} value={especie.idEspecie.toString()}>
                      {especie.nombre}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {/* Vista de Especies */}
          {vistaActual === 'especies' && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
                {especiesFiltradas.map((especie) => (
                  <div
                    key={especie.idEspecie}
                    className="bg-gray-50 rounded-lg p-4 border border-gray-200 hover:shadow-md transition-shadow"
                  >
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold text-lg text-gray-900">{especie.nombre}</h3>
                      <span className="text-xs text-gray-500">ID: {especie.idEspecie}</span>
                    </div>

                    {especie.descripcion && (
                      <p className="text-sm text-gray-600 mb-3">{especie.descripcion}</p>
                    )}

                    <div className="text-sm text-gray-600">
                      <strong>Razas disponibles:</strong> {razas.filter(r => r.especie?.idEspecie === especie.idEspecie).length}
                    </div>

                    <button
                      onClick={() => {
                        setEspecieSeleccionada(especie.idEspecie.toString());
                        setVistaActual('razas');
                      }}
                      className="mt-3 w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 text-sm"
                    >
                      Ver Razas
                    </button>
                  </div>
                ))}
              </div>

              {especiesFiltradas.length === 0 && (
                <div className="text-center text-gray-500 py-8">
                  No se encontraron especies.
                </div>
              )}

              {/* Tabla de especies */}
              <div className="bg-white rounded-lg border overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Razas</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {especiesFiltradas.map((especie) => (
                        <tr key={especie.idEspecie} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            #{especie.idEspecie}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {especie.nombre}
                          </td>
                          <td className="px-6 py-4 text-sm text-gray-600">
                            {especie.descripcion || 'Sin descripción'}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {razas.filter(r => r.especie?.idEspecie === especie.idEspecie).length}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}

          {/* Vista de Razas */}
          {vistaActual === 'razas' && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
                {razasFiltradas.map((raza) => (
                  <div
                    key={raza.idRaza}
                    className="bg-gray-50 rounded-lg p-4 border border-gray-200 hover:shadow-md transition-shadow"
                  >
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold text-lg text-gray-900">{raza.nombre}</h3>
                      <span className="text-xs text-gray-500">ID: {raza.idRaza}</span>
                    </div>

                    <div className="mb-2">
                      <span className="inline-block px-2 py-1 text-xs font-medium bg-primary-100 text-primary-800 rounded">
                        {raza.especie?.nombre}
                      </span>
                    </div>

                    {raza.descripcion && (
                      <p className="text-sm text-gray-600">{raza.descripcion}</p>
                    )}
                  </div>
                ))}
              </div>

              {razasFiltradas.length === 0 && (
                <div className="text-center text-gray-500 py-8">
                  No se encontraron razas con los filtros seleccionados.
                </div>
              )}

              {/* Tabla de razas */}
              <div className="bg-white rounded-lg border overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Raza</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Especie</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {razasFiltradas.map((raza) => (
                        <tr key={raza.idRaza} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            #{raza.idRaza}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {raza.nombre}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {raza.especie?.nombre}
                          </td>
                          <td className="px-6 py-4 text-sm text-gray-600">
                            {raza.descripcion || 'Sin descripción'}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Resumen rápido */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div className="flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-full">
              <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <div className="flex-1">
              <div className="text-2xl font-bold text-blue-900">{especies.length}</div>
              <div className="text-sm text-blue-800">Especies Activas</div>
            </div>
          </div>
        </div>

        <div className="bg-green-50 border border-green-200 rounded-lg p-4">
          <div className="flex items-center gap-3">
            <div className="bg-green-100 p-3 rounded-full">
              <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div className="flex-1">
              <div className="text-2xl font-bold text-green-900">{razas.length}</div>
              <div className="text-sm text-green-800">Razas Activas</div>
            </div>
          </div>
        </div>
      </div>

      {/* Información útil */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información para Recepcionistas</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Consulta las especies y razas disponibles antes de registrar una mascota</li>
              <li>• Usa el buscador para encontrar rápidamente una raza específica</li>
              <li>• Si una raza no está en la lista, solicita al administrador que la agregue</li>
              <li>• Las razas están organizadas por especie para facilitar su búsqueda</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
