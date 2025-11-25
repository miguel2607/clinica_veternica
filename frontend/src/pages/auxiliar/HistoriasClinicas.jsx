import { useEffect, useState } from 'react';
import { historiaClinicaService, mascotaService } from '../../services/api';

export default function HistoriasClinicasPage() {
  const [historias, setHistorias] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedHistoria, setSelectedHistoria] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    observaciones: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      const [historiasRes, mascotasRes] = await Promise.all([
        historiaClinicaService.getAll(),
        mascotaService.getAll()
      ]);

      const historiasData = historiasRes.data || [];
      setHistorias(Array.isArray(historiasData) ? historiasData : [historiasData]);
      setMascotas(mascotasRes.data || []);
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const getMascotaInfo = (historia) => {
    if (!historia) return null;

    if (historia.mascota) {
      // Intentar enriquecer con la info completa de la mascota si está disponible
      const mascotaCompleta = mascotas.find(m => m.idMascota === historia.mascota.idMascota);
      if (mascotaCompleta) {
        return mascotaCompleta;
      }

      return {
        idMascota: historia.mascota.idMascota,
        nombre: historia.mascota.nombre,
        raza: { nombre: historia.mascota.especie, especie: { nombre: historia.mascota.especie } },
        propietario: { nombres: historia.mascota.propietarioNombre || 'Propietario', apellidos: '' }
      };
    }

    // Fallback: buscar por idMascota si el DTO lo incluye
    if (historia.idMascota) {
      return mascotas.find(m => m.idMascota === historia.idMascota) || null;
    }

    return null;
  };

  const handleOpenModal = (historia) => {
    const mascota = getMascotaInfo(historia.idHistoriaClinica);
    setSelectedHistoria({...historia, mascota});
    setFormData({
      observaciones: historia.observaciones || ''
    });
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const updateData = {
        ...selectedHistoria,
        observaciones: formData.observaciones,
        idMascota: selectedHistoria.idMascota
      };

      await historiaClinicaService.update(selectedHistoria.idHistoriaClinica, updateData);
      setSuccess('Historia clínica actualizada exitosamente');

      await loadData();
      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al actualizar:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const historiasFiltradas = historias.filter(historia => {
    const mascota = getMascotaInfo(historia);
    if (!mascota) return false;

    return mascota.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.apellidos?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           historia.motivoConsulta?.toLowerCase().includes(searchTerm.toLowerCase());
  });

  if (loading) {
    return <div className="text-center py-8">Cargando historias clínicas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Historias Clínicas</h2>
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

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {success}
        </div>
      )}

      {/* Buscador */}
      <div className="bg-white rounded-lg shadow p-4">
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Buscar por nombre de mascota, propietario o motivo de consulta..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
        />
      </div>

      {/* Vista de tarjetas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {historiasFiltradas.map((historia) => {
          const mascota = getMascotaInfo(historia);
          if (!mascota) return null;

          return (
            <div
              key={historia.idHistoriaClinica}
              className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow"
            >
              <div className="p-6">
                <div className="mb-4">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex-1">
                      <h3 className="font-semibold text-lg text-gray-900">{mascota.nombre}</h3>
                      <p className="text-sm text-gray-600">
                        {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                      </p>
                    </div>
                    <span className="px-2 py-1 text-xs font-semibold bg-blue-100 text-blue-800 rounded-full">
                      HC #{historia.idHistoriaClinica}
                    </span>
                  </div>
                  <p className="text-sm text-gray-600">
                    <strong>Propietario:</strong> {mascota.propietario?.nombres} {mascota.propietario?.apellidos}
                  </p>
                </div>

                <div className="space-y-2 text-sm">
                  {historia.motivoConsulta && (
                    <div>
                      <span className="text-gray-600 font-medium">Motivo de Consulta:</span>
                      <p className="text-gray-900 mt-1">{historia.motivoConsulta}</p>
                    </div>
                  )}

                  {historia.fechaCreacion && (
                    <div className="text-gray-600">
                      <strong>Fecha de Creación:</strong> {historia.fechaCreacion}
                    </div>
                  )}

                  {historia.observaciones && (
                    <div>
                      <span className="text-gray-600 font-medium">Observaciones:</span>
                      <p className="text-gray-900 mt-1 line-clamp-2">{historia.observaciones}</p>
                    </div>
                  )}
                </div>

                <button
                  onClick={() => handleOpenModal(historia)}
                  className="mt-4 w-full bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 transition-colors text-sm"
                >
                  Ver / Actualizar
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {historiasFiltradas.length === 0 && (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          No se encontraron historias clínicas.
        </div>
      )}

      {/* Modal de detalle/actualización */}
      {modalOpen && selectedHistoria && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h3 className="text-2xl font-bold text-gray-900">
                    Historia Clínica #{selectedHistoria.idHistoriaClinica}
                  </h3>
                  <p className="text-gray-600">
                    {selectedHistoria.mascota?.nombre} - {selectedHistoria.mascota?.raza?.nombre}
                  </p>
                </div>
                <button
                  onClick={() => setModalOpen(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="space-y-4">
                {/* Información de la mascota */}
                <div className="bg-gray-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-2">Información del Paciente</h4>
                  <div className="grid grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="text-gray-600">Nombre:</span>
                      <span className="ml-2 font-medium">{selectedHistoria.mascota?.nombre}</span>
                    </div>
                    <div>
                      <span className="text-gray-600">Especie:</span>
                      <span className="ml-2 font-medium">{selectedHistoria.mascota?.raza?.especie?.nombre}</span>
                    </div>
                    <div>
                      <span className="text-gray-600">Raza:</span>
                      <span className="ml-2 font-medium">{selectedHistoria.mascota?.raza?.nombre}</span>
                    </div>
                    <div>
                      <span className="text-gray-600">Sexo:</span>
                      <span className="ml-2 font-medium">{selectedHistoria.mascota?.sexo}</span>
                    </div>
                    <div>
                      <span className="text-gray-600">Propietario:</span>
                      <span className="ml-2 font-medium">
                        {selectedHistoria.mascota?.propietario?.nombres} {selectedHistoria.mascota?.propietario?.apellidos}
                      </span>
                    </div>
                    <div>
                      <span className="text-gray-600">Teléfono:</span>
                      <span className="ml-2 font-medium">{selectedHistoria.mascota?.propietario?.telefono || 'N/A'}</span>
                    </div>
                  </div>
                </div>

                {/* Información de la historia clínica */}
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Información de la Historia</h4>
                  <div className="space-y-3">
                    {selectedHistoria.fechaCreacion && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Fecha de Creación</span>
                        <span className="text-sm font-medium text-gray-900">{selectedHistoria.fechaCreacion}</span>
                      </div>
                    )}

                    {selectedHistoria.motivoConsulta && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Motivo de Consulta</span>
                        <p className="text-sm text-gray-900">{selectedHistoria.motivoConsulta}</p>
                      </div>
                    )}

                    {selectedHistoria.antecedentesMedicos && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Antecedentes Médicos</span>
                        <p className="text-sm text-gray-900">{selectedHistoria.antecedentesMedicos}</p>
                      </div>
                    )}

                    {selectedHistoria.alergias && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Alergias</span>
                        <p className="text-sm text-gray-900">{selectedHistoria.alergias}</p>
                      </div>
                    )}
                  </div>
                </div>

                {/* Formulario de actualización */}
                <form onSubmit={handleUpdate} className="border-t pt-4">
                  <h4 className="font-semibold text-gray-900 mb-3">Actualizar Observaciones</h4>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Observaciones
                    </label>
                    <textarea
                      value={formData.observaciones}
                      onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                      rows="4"
                      placeholder="Agregar observaciones o notas sobre el paciente..."
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                    <p className="text-xs text-gray-500 mt-1">
                      Como auxiliar puedes agregar notas y observaciones para el equipo veterinario
                    </p>
                  </div>

                  {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm mt-4">
                      {error}
                    </div>
                  )}

                  {success && (
                    <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm mt-4">
                      {success}
                    </div>
                  )}

                  <div className="flex justify-end gap-3 pt-4 border-t mt-4">
                    <button
                      type="button"
                      onClick={() => setModalOpen(false)}
                      className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                      Cerrar
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                    >
                      Guardar Cambios
                    </button>
                  </div>
                </form>
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
            <h4 className="font-semibold text-blue-900 mb-1">Información para Auxiliares</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Puedes ver y actualizar las observaciones de las historias clínicas</li>
              <li>• Usa las observaciones para agregar notas importantes para el veterinario</li>
              <li>• No puedes modificar la información médica principal (solo el veterinario)</li>
              <li>• Para crear evoluciones clínicas, consulta con el veterinario</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
