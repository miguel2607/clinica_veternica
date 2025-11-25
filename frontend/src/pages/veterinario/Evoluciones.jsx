import { useEffect, useState } from 'react';
import { evolucionClinicaService, historiaClinicaService, mascotaService } from '../../services/api';

export default function EvolucionesPage() {
  const [evoluciones, setEvoluciones] = useState([]);
  const [historiasClinicas, setHistoriasClinicas] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedHistoria, setSelectedHistoria] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const [formData, setFormData] = useState({
    idHistoriaClinica: '',
    fecha: new Date().toISOString().split('T')[0],
    descripcion: '',
    signosVitales: '',
    diagnostico: '',
    tratamiento: '',
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
        historiaClinicaService.getActivas(),
        mascotaService.getAll()
      ]);

      console.log('✅ Historias clínicas:', historiasRes.data);
      console.log('✅ Mascotas:', mascotasRes.data);

      setHistoriasClinicas(historiasRes.data || []);
      setMascotas(mascotasRes.data || []);
    } catch (error) {
      console.error('❌ Error al cargar datos:', error);
      setError(`Error al cargar datos: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const loadEvolucionesByHistoria = async (idHistoriaClinica) => {
    try {
      setError('');
      const response = await evolucionClinicaService.getByHistoriaClinica(idHistoriaClinica);
      console.log('✅ Evoluciones cargadas:', response.data);
      setEvoluciones(response.data || []);
    } catch (error) {
      console.error('❌ Error al cargar evoluciones:', error);
      setError(`Error al cargar evoluciones: ${error.response?.data?.message || error.message}`);
      setEvoluciones([]);
    }
  };

  const handleSelectHistoria = async (historia) => {
    setSelectedHistoria(historia);
    await loadEvolucionesByHistoria(historia.idHistoriaClinica);
  };

  const handleOpenModal = () => {
    if (!selectedHistoria) {
      setError('Primero seleccione una historia clínica');
      return;
    }

    setFormData({
      idHistoriaClinica: selectedHistoria.idHistoriaClinica,
      fecha: new Date().toISOString().split('T')[0],
      descripcion: '',
      signosVitales: '',
      diagnostico: '',
      tratamiento: '',
      observaciones: ''
    });
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await evolucionClinicaService.create(formData.idHistoriaClinica, formData);
      setSuccess('Evolución clínica registrada exitosamente');

      // Recargar evoluciones
      await loadEvolucionesByHistoria(formData.idHistoriaClinica);

      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar evolución:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const getMascotaInfo = (idHistoriaClinica) => {
    const historia = historiasClinicas.find(h => h.idHistoriaClinica === idHistoriaClinica);
    if (!historia) return null;

    const mascota = mascotas.find(m => m.idMascota === historia.idMascota);
    return mascota;
  };

  const historiasFiltradas = historiasClinicas.filter(historia => {
    const mascota = getMascotaInfo(historia.idHistoriaClinica);
    if (!mascota) return false;

    return mascota.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
           mascota.propietario?.apellidos?.toLowerCase().includes(searchTerm.toLowerCase());
  });

  if (loading) {
    return <div className="text-center py-8">Cargando datos...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Evoluciones Clínicas</h2>
        <button
          onClick={handleOpenModal}
          disabled={!selectedHistoria}
          className={`px-4 py-2 rounded-lg transition-colors inline-flex items-center gap-2 ${
            selectedHistoria
              ? 'bg-primary-600 text-white hover:bg-primary-700'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nueva Evolución
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

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Panel izquierdo - Historias clínicas */}
        <div className="lg:col-span-1 space-y-4">
          <div className="bg-white rounded-lg shadow p-4">
            <h3 className="font-semibold mb-3">Historias Clínicas</h3>

            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar mascota..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 mb-3"
            />

            <div className="space-y-2 max-h-[600px] overflow-y-auto">
              {historiasFiltradas.map((historia) => {
                const mascota = getMascotaInfo(historia.idHistoriaClinica);
                if (!mascota) return null;

                return (
                  <div
                    key={historia.idHistoriaClinica}
                    onClick={() => handleSelectHistoria(historia)}
                    className={`p-3 rounded-lg border cursor-pointer transition-colors ${
                      selectedHistoria?.idHistoriaClinica === historia.idHistoriaClinica
                        ? 'bg-primary-50 border-primary-500'
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    }`}
                  >
                    <div className="font-medium text-gray-900">{mascota.nombre}</div>
                    <div className="text-sm text-gray-600">
                      {mascota.propietario?.nombres} {mascota.propietario?.apellidos}
                    </div>
                    <div className="text-xs text-gray-500 mt-1">
                      {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                    </div>
                    <div className="text-xs text-gray-500">
                      HC: #{historia.idHistoriaClinica}
                    </div>
                  </div>
                );
              })}

              {historiasFiltradas.length === 0 && (
                <div className="text-center text-gray-500 py-4 text-sm">
                  No se encontraron historias clínicas
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Panel derecho - Evoluciones */}
        <div className="lg:col-span-2">
          {selectedHistoria ? (
            <div className="bg-white rounded-lg shadow">
              <div className="p-6 border-b">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold text-lg">
                      {getMascotaInfo(selectedHistoria.idHistoriaClinica)?.nombre}
                    </h3>
                    <p className="text-sm text-gray-600">
                      Historia Clínica #{selectedHistoria.idHistoriaClinica}
                    </p>
                    {selectedHistoria.motivoConsulta && (
                      <p className="text-sm text-gray-600 mt-1">
                        <strong>Motivo:</strong> {selectedHistoria.motivoConsulta}
                      </p>
                    )}
                  </div>
                </div>
              </div>

              <div className="p-6">
                <h4 className="font-semibold mb-4">Historial de Evoluciones</h4>

                {evoluciones.length > 0 ? (
                  <div className="space-y-4">
                    {/* Timeline de evoluciones */}
                    {evoluciones.map((evolucion, index) => (
                      <div key={evolucion.idEvolucionClinica || index} className="relative pl-8 pb-6 border-l-2 border-gray-200 last:border-0">
                        {/* Punto en la línea */}
                        <div className="absolute left-0 top-0 -ml-2 w-4 h-4 rounded-full bg-primary-600"></div>

                        <div className="bg-gray-50 p-4 rounded-lg">
                          <div className="flex justify-between items-start mb-2">
                            <div>
                              <div className="font-medium text-gray-900">
                                {evolucion.fecha}
                              </div>
                              {evolucion.veterinario && (
                                <div className="text-sm text-gray-600">
                                  Dr(a). {evolucion.veterinario.nombres} {evolucion.veterinario.apellidos}
                                </div>
                              )}
                            </div>
                          </div>

                          {evolucion.descripcion && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Descripción:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.descripcion}</div>
                            </div>
                          )}

                          {evolucion.signosVitales && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Signos Vitales:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.signosVitales}</div>
                            </div>
                          )}

                          {evolucion.diagnostico && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Diagnóstico:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.diagnostico}</div>
                            </div>
                          )}

                          {evolucion.tratamiento && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Tratamiento:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.tratamiento}</div>
                            </div>
                          )}

                          {evolucion.observaciones && (
                            <div className="mt-3">
                              <div className="text-sm font-medium text-gray-700">Observaciones:</div>
                              <div className="text-sm text-gray-600 mt-1">{evolucion.observaciones}</div>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center text-gray-500 py-8">
                    No hay evoluciones registradas para esta historia clínica.
                    <br />
                    <span className="text-sm">Haz clic en "Nueva Evolución" para agregar una.</span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
              <svg className="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p className="text-lg font-medium mb-2">Selecciona una Historia Clínica</p>
              <p className="text-sm">Selecciona una historia clínica del panel izquierdo para ver sus evoluciones</p>
            </div>
          )}
        </div>
      </div>

      {/* Modal de nueva evolución */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">Nueva Evolución Clínica</h3>

              <div className="mb-4 p-3 bg-blue-50 rounded-lg">
                <div className="text-sm">
                  <strong>Mascota:</strong> {getMascotaInfo(selectedHistoria?.idHistoriaClinica)?.nombre}
                  <br />
                  <strong>HC:</strong> #{selectedHistoria?.idHistoriaClinica}
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Fecha *
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.fecha}
                    onChange={(e) => setFormData({...formData, fecha: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Descripción *
                  </label>
                  <textarea
                    required
                    value={formData.descripcion}
                    onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                    rows="3"
                    placeholder="Descripción del estado del paciente..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Signos Vitales
                  </label>
                  <textarea
                    value={formData.signosVitales}
                    onChange={(e) => setFormData({...formData, signosVitales: e.target.value})}
                    rows="2"
                    placeholder="Temperatura, frecuencia cardíaca, respiración, etc..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Diagnóstico
                  </label>
                  <textarea
                    value={formData.diagnostico}
                    onChange={(e) => setFormData({...formData, diagnostico: e.target.value})}
                    rows="3"
                    placeholder="Diagnóstico actual..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tratamiento
                  </label>
                  <textarea
                    value={formData.tratamiento}
                    onChange={(e) => setFormData({...formData, tratamiento: e.target.value})}
                    rows="3"
                    placeholder="Tratamiento indicado..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observaciones
                  </label>
                  <textarea
                    value={formData.observaciones}
                    onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                    rows="2"
                    placeholder="Observaciones adicionales..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  />
                </div>

                {error && (
                  <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                    {error}
                  </div>
                )}

                {success && (
                  <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm">
                    {success}
                  </div>
                )}

                <div className="flex justify-end gap-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => setModalOpen(false)}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                  >
                    Guardar Evolución
                  </button>
                </div>
              </form>
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
            <h4 className="font-semibold text-blue-900 mb-1">Cómo usar Evoluciones Clínicas</h4>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Selecciona una historia clínica del panel izquierdo</li>
              <li>• Haz clic en "Nueva Evolución" para registrar el seguimiento del paciente</li>
              <li>• Las evoluciones se muestran en orden cronológico en formato timeline</li>
              <li>• Registra signos vitales, diagnóstico y tratamiento en cada evolución</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
