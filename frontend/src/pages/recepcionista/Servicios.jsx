import { useEffect, useState } from 'react';
import { servicioService } from '../../services/api';

export default function ServiciosPage() {
  const [servicios, setServicios] = useState([]);
  const [serviciosFiltrados, setServiciosFiltrados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('TODAS');
  const [busqueda, setBusqueda] = useState('');
  const [servicioDetalle, setServicioDetalle] = useState(null);

  const categoriasServicio = {
    CONSULTA: 'Consulta',
    CIRUGIA: 'Cirugía',
    VACUNACION: 'Vacunación',
    DESPARASITACION: 'Desparasitación',
    ESTETICA: 'Estética',
    EMERGENCIA: 'Emergencia',
    DIAGNOSTICO: 'Diagnóstico',
    HOSPITALIZACION: 'Hospitalización',
    OTRO: 'Otro'
  };

  useEffect(() => {
    loadServicios();
  }, []);

  useEffect(() => {
    filtrarServicios();
  }, [categoriaSeleccionada, busqueda, servicios]);

  const loadServicios = async () => {
    try {
      setLoading(true);
      setError('');

      const response = await servicioService.getActivos();
      console.log('✅ Servicios cargados:', response.data);

      // Ordenar servicios por nombre
      const serviciosOrdenados = (response.data || []).sort((a, b) =>
        (a.nombre || '').localeCompare(b.nombre || '')
      );

      setServicios(serviciosOrdenados);
      setServiciosFiltrados(serviciosOrdenados);
    } catch (error) {
      console.error('❌ Error al cargar servicios:', error);
      setError(`Error al cargar los servicios: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const filtrarServicios = () => {
    let resultados = [...servicios];

    // Filtrar por categoría
    if (categoriaSeleccionada !== 'TODAS') {
      resultados = resultados.filter(servicio =>
        servicio.categoria === categoriaSeleccionada
      );
    }

    // Filtrar por búsqueda
    if (busqueda.trim() !== '') {
      const terminoBusqueda = busqueda.toLowerCase().trim();
      resultados = resultados.filter(servicio =>
        servicio.nombre?.toLowerCase().includes(terminoBusqueda) ||
        servicio.descripcion?.toLowerCase().includes(terminoBusqueda)
      );
    }

    setServiciosFiltrados(resultados);
  };

  const formatearPrecio = (precio) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(precio);
  };

  const formatearDuracion = (minutos) => {
    if (!minutos) return 'N/A';
    if (minutos < 60) return `${minutos} min`;
    const horas = Math.floor(minutos / 60);
    const mins = minutos % 60;
    return mins > 0 ? `${horas}h ${mins}min` : `${horas}h`;
  };

  if (loading) {
    return <div className="text-center py-8">Cargando servicios...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Servicios Veterinarios</h2>
        <button
          onClick={loadServicios}
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

      {/* Filtros y búsqueda */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Buscar servicio
            </label>
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              placeholder="Buscar por nombre o descripción..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Categoría
            </label>
            <select
              value={categoriaSeleccionada}
              onChange={(e) => setCategoriaSeleccionada(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="TODAS">Todas las categorías</option>
              {Object.entries(categoriasServicio).map(([key, value]) => (
                <option key={key} value={key}>{value}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Resumen de resultados */}
        <div className="mt-3 text-sm text-gray-600">
          Mostrando {serviciosFiltrados.length} de {servicios.length} servicios
        </div>
      </div>

      {serviciosFiltrados.length > 0 ? (
        <>
          {/* Vista de tarjetas */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {serviciosFiltrados.map((servicio) => (
              <div
                key={servicio.idServicio}
                className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => setServicioDetalle(servicio)}
              >
                <div className="p-6">
                  {/* Header del servicio */}
                  <div className="mb-4">
                    <div className="flex items-start justify-between mb-2">
                      <h3 className="font-semibold text-lg text-gray-900 flex-1">
                        {servicio.nombre}
                      </h3>
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        servicio.activo
                          ? 'bg-green-100 text-green-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}>
                        {servicio.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </div>

                    <span className="inline-block px-2 py-1 text-xs font-medium bg-primary-100 text-primary-800 rounded">
                      {categoriasServicio[servicio.categoria] || servicio.categoria}
                    </span>
                  </div>

                  {/* Descripción */}
                  <p className="text-sm text-gray-600 mb-4 line-clamp-3">
                    {servicio.descripcion || 'Sin descripción'}
                  </p>

                  {/* Información del servicio */}
                  <div className="space-y-2 border-t pt-4">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Precio:</span>
                      <span className="text-lg font-bold text-primary-600">
                        {formatearPrecio(servicio.precio)}
                      </span>
                    </div>

                    {servicio.duracionEstimada && (
                      <div className="flex justify-between items-center">
                        <span className="text-sm text-gray-600">Duración:</span>
                        <span className="text-sm font-medium text-gray-900">
                          {formatearDuracion(servicio.duracionEstimada)}
                        </span>
                      </div>
                    )}

                    {servicio.tipoServicio && (
                      <div className="flex justify-between items-center">
                        <span className="text-sm text-gray-600">Tipo:</span>
                        <span className="text-sm font-medium text-gray-900">
                          {servicio.tipoServicio}
                        </span>
                      </div>
                    )}
                  </div>

                  {/* Botón de acción */}
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      setServicioDetalle(servicio);
                    }}
                    className="mt-4 w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 transition-colors text-sm font-medium"
                  >
                    Ver detalles
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Vista de tabla (alternativa) */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-3 bg-gray-50 border-b">
              <h3 className="font-semibold text-gray-900">Vista de tabla</h3>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Servicio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoría</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Precio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duración</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {serviciosFiltrados.map((servicio) => (
                    <tr
                      key={servicio.idServicio}
                      className="hover:bg-gray-50 cursor-pointer"
                      onClick={() => setServicioDetalle(servicio)}
                    >
                      <td className="px-6 py-4">
                        <div>
                          <div className="text-sm font-medium text-gray-900">{servicio.nombre}</div>
                          <div className="text-sm text-gray-500 line-clamp-1">{servicio.descripcion}</div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {categoriasServicio[servicio.categoria] || servicio.categoria}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-primary-600">
                        {formatearPrecio(servicio.precio)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {formatearDuracion(servicio.duracionEstimada)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                          servicio.activo
                            ? 'bg-green-100 text-green-800'
                            : 'bg-gray-100 text-gray-800'
                        }`}>
                          {servicio.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      ) : (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          No se encontraron servicios {busqueda || categoriaSeleccionada !== 'TODAS' ? 'con los filtros seleccionados' : 'activos'}.
        </div>
      )}

      {/* Modal de detalle de servicio */}
      {servicioDetalle && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              {/* Header */}
              <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                  <h3 className="text-2xl font-bold text-gray-900 mb-2">
                    {servicioDetalle.nombre}
                  </h3>
                  <div className="flex gap-2">
                    <span className="inline-block px-3 py-1 text-sm font-medium bg-primary-100 text-primary-800 rounded-full">
                      {categoriasServicio[servicioDetalle.categoria] || servicioDetalle.categoria}
                    </span>
                    <span className={`px-3 py-1 text-sm font-semibold rounded-full ${
                      servicioDetalle.activo
                        ? 'bg-green-100 text-green-800'
                        : 'bg-gray-100 text-gray-800'
                    }`}>
                      {servicioDetalle.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => setServicioDetalle(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              {/* Contenido */}
              <div className="space-y-4">
                {/* Descripción */}
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Descripción</h4>
                  <p className="text-gray-600">
                    {servicioDetalle.descripcion || 'Sin descripción disponible'}
                  </p>
                </div>

                {/* Información detallada */}
                <div className="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-lg">
                  <div>
                    <span className="text-sm text-gray-600 block mb-1">Precio</span>
                    <span className="text-xl font-bold text-primary-600">
                      {formatearPrecio(servicioDetalle.precio)}
                    </span>
                  </div>

                  {servicioDetalle.duracionEstimada && (
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Duración estimada</span>
                      <span className="text-lg font-semibold text-gray-900">
                        {formatearDuracion(servicioDetalle.duracionEstimada)}
                      </span>
                    </div>
                  )}

                  {servicioDetalle.tipoServicio && (
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Tipo de servicio</span>
                      <span className="text-sm font-medium text-gray-900">
                        {servicioDetalle.tipoServicio}
                      </span>
                    </div>
                  )}

                  <div>
                    <span className="text-sm text-gray-600 block mb-1">ID del servicio</span>
                    <span className="text-sm font-medium text-gray-900">
                      #{servicioDetalle.idServicio}
                    </span>
                  </div>
                </div>

                {/* Botón de cerrar */}
                <button
                  onClick={() => setServicioDetalle(null)}
                  className="w-full bg-gray-600 text-white py-2 rounded-lg hover:bg-gray-700 transition-colors"
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
          <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div className="flex-1">
            <h4 className="font-semibold text-blue-900 mb-1">Información sobre servicios</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Los precios mostrados son valores base y pueden variar según el caso.</li>
              <li>• La duración es estimada y puede extenderse según las necesidades del paciente.</li>
              <li>• Para agendar una cita con un servicio específico, ve a la sección de Citas.</li>
              <li>• Haz clic en cualquier servicio para ver más detalles.</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
