import { useEffect, useState } from 'react';
import { mascotaService, mascotaFacadeService, propietarioService, especieService, razaService } from '../../services/api';

export default function MascotasPage() {
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [mascotaDetalle, setMascotaDetalle] = useState(null);
  const [loadingDetalle, setLoadingDetalle] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMascota, setEditingMascota] = useState(null);

  // Datos para formulario
  const [propietarios, setPropietarios] = useState([]);
  const [especies, setEspecies] = useState([]);
  const [razas, setRazas] = useState([]);
  const [razasFiltradas, setRazasFiltradas] = useState([]);

  const [formData, setFormData] = useState({
    nombre: '',
    idPropietario: '',
    idEspecie: '',
    idRaza: '',
    fechaNacimiento: '',
    sexo: '',
    color: '',
    peso: '',
    observaciones: '',
    activo: true
  });

  useEffect(() => {
    loadMascotas();
  }, []);

  const loadMascotas = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await mascotaService.getAll();
      console.log('✅ Mascotas cargadas:', response.data);
      setMascotas(response.data || []);
    } catch (error) {
      console.error('❌ Error al cargar mascotas:', error);
      setError(`Error al cargar mascotas: ${error.response?.data?.message || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const loadDatosFormulario = async () => {
    try {
      const [propietariosRes, especiesRes, razasRes] = await Promise.all([
        propietarioService.getActivos(),
        especieService.getActivas(),
        razaService.getActivas()
      ]);
      setPropietarios(propietariosRes.data || []);
      setEspecies(especiesRes.data || []);
      setRazas(razasRes.data || []);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos del formulario');
    }
  };

  const handleVerDetalle = async (mascota) => {
    try {
      setLoadingDetalle(true);
      setError('');

      // Intentar obtener información completa usando el facade
      try {
        const response = await mascotaFacadeService.getCompleta(mascota.idMascota);
        console.log('✅ Información completa de mascota:', response.data);
        setMascotaDetalle(response.data);
      } catch (facadeError) {
        // Si el facade no está disponible, usar la información básica
        console.warn('Facade no disponible, usando información básica');
        setMascotaDetalle({
          mascota: mascota,
          historiaClinica: null,
          proximasCitas: []
        });
      }
    } catch (error) {
      console.error('Error al cargar detalle:', error);
      setError('Error al cargar información de la mascota');
    } finally {
      setLoadingDetalle(false);
    }
  };

  const handleOpenModal = async (mascota = null) => {
    await loadDatosFormulario();

    if (mascota) {
      setEditingMascota(mascota);
      setFormData({
        nombre: mascota.nombre || '',
        idPropietario: mascota.propietario?.idPersonal || '',
        idEspecie: mascota.raza?.especie?.idEspecie || '',
        idRaza: mascota.raza?.idRaza || '',
        fechaNacimiento: mascota.fechaNacimiento || '',
        sexo: mascota.sexo || '',
        color: mascota.color || '',
        peso: mascota.peso || '',
        observaciones: mascota.observaciones || '',
        activo: mascota.activo !== undefined ? mascota.activo : true
      });

      // Filtrar razas por especie
      if (mascota.raza?.especie?.idEspecie) {
        const razasPorEspecie = razas.filter(r => r.especie?.idEspecie === mascota.raza.especie.idEspecie);
        setRazasFiltradas(razasPorEspecie);
      }
    } else {
      setEditingMascota(null);
      setFormData({
        nombre: '',
        idPropietario: '',
        idEspecie: '',
        idRaza: '',
        fechaNacimiento: '',
        sexo: '',
        color: '',
        peso: '',
        observaciones: '',
        activo: true
      });
      setRazasFiltradas([]);
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleEspecieChange = (idEspecie) => {
    setFormData({...formData, idEspecie, idRaza: ''});
    const razasPorEspecie = razas.filter(r => r.especie?.idEspecie === parseInt(idEspecie));
    setRazasFiltradas(razasPorEspecie);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const data = {
        ...formData,
        idPropietario: parseInt(formData.idPropietario),
        idRaza: parseInt(formData.idRaza),
        peso: formData.peso ? parseFloat(formData.peso) : null
      };

      if (editingMascota) {
        await mascotaService.update(editingMascota.idMascota, data);
        setSuccess('Mascota actualizada exitosamente');
      } else {
        await mascotaService.create(data);
        setSuccess('Mascota creada exitosamente');
      }

      await loadMascotas();
      setTimeout(() => {
        setModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar mascota:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const mascotasFiltradas = mascotas.filter(mascota =>
    mascota.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    mascota.propietario?.nombres?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    mascota.propietario?.apellidos?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return <div className="text-center py-8">Cargando mascotas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Mis Pacientes</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors inline-flex items-center gap-2"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nueva Mascota
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
          placeholder="Buscar por nombre de mascota o propietario..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
        />
      </div>

      {/* Grid de mascotas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {mascotasFiltradas.map((mascota) => (
          <div
            key={mascota.idMascota}
            className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow cursor-pointer"
            onClick={() => handleVerDetalle(mascota)}
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                  <h3 className="font-semibold text-lg text-gray-900">{mascota.nombre}</h3>
                  <p className="text-sm text-gray-600">
                    {mascota.raza?.nombre} - {mascota.raza?.especie?.nombre}
                  </p>
                </div>
                <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                  mascota.activo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                }`}>
                  {mascota.activo ? 'Activo' : 'Inactivo'}
                </span>
              </div>

              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Propietario:</span>
                  <span className="font-medium text-gray-900">
                    {mascota.propietario?.nombres} {mascota.propietario?.apellidos}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Sexo:</span>
                  <span className="font-medium text-gray-900">{mascota.sexo}</span>
                </div>
                {mascota.peso && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">Peso:</span>
                    <span className="font-medium text-gray-900">{mascota.peso} kg</span>
                  </div>
                )}
                {mascota.fechaNacimiento && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">Edad:</span>
                    <span className="font-medium text-gray-900">
                      {new Date().getFullYear() - new Date(mascota.fechaNacimiento).getFullYear()} años
                    </span>
                  </div>
                )}
              </div>

              <div className="mt-4 pt-4 border-t flex gap-2">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleOpenModal(mascota);
                  }}
                  className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 text-sm"
                >
                  Editar
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleVerDetalle(mascota);
                  }}
                  className="flex-1 border border-primary-600 text-primary-600 py-2 rounded-lg hover:bg-primary-50 text-sm"
                >
                  Ver más
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {mascotasFiltradas.length === 0 && (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          No se encontraron mascotas.
        </div>
      )}

      {/* Modal de detalle de mascota */}
      {mascotaDetalle && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h3 className="text-2xl font-bold text-gray-900">
                    {mascotaDetalle.mascota?.nombre || mascotaDetalle.nombre}
                  </h3>
                  <p className="text-gray-600">
                    {mascotaDetalle.mascota?.raza?.nombre || mascotaDetalle.raza?.nombre} - {mascotaDetalle.mascota?.raza?.especie?.nombre || mascotaDetalle.raza?.especie?.nombre}
                  </p>
                </div>
                <button
                  onClick={() => setMascotaDetalle(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              {loadingDetalle ? (
                <div className="text-center py-8">Cargando información...</div>
              ) : (
                <div className="space-y-6">
                  {/* Información básica */}
                  <div className="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-lg">
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Propietario</span>
                      <span className="font-medium">
                        {mascotaDetalle.mascota?.propietario?.nombres} {mascotaDetalle.mascota?.propietario?.apellidos}
                      </span>
                    </div>
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Teléfono</span>
                      <span className="font-medium">{mascotaDetalle.mascota?.propietario?.telefono || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Fecha de Nacimiento</span>
                      <span className="font-medium">{mascotaDetalle.mascota?.fechaNacimiento || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-sm text-gray-600 block mb-1">Sexo</span>
                      <span className="font-medium">{mascotaDetalle.mascota?.sexo}</span>
                    </div>
                    {mascotaDetalle.mascota?.peso && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Peso</span>
                        <span className="font-medium">{mascotaDetalle.mascota.peso} kg</span>
                      </div>
                    )}
                    {mascotaDetalle.mascota?.color && (
                      <div>
                        <span className="text-sm text-gray-600 block mb-1">Color</span>
                        <span className="font-medium">{mascotaDetalle.mascota.color}</span>
                      </div>
                    )}
                  </div>

                  {/* Historia clínica */}
                  {mascotaDetalle.historiaClinica && (
                    <div className="border-t pt-4">
                      <h4 className="font-semibold text-gray-900 mb-2">Historia Clínica</h4>
                      <div className="bg-blue-50 p-4 rounded-lg">
                        <p className="text-sm text-gray-700">
                          Historia clínica registrada - ID: {mascotaDetalle.historiaClinica.idHistoriaClinica}
                        </p>
                        {mascotaDetalle.historiaClinica.motivoConsulta && (
                          <p className="text-sm text-gray-700 mt-2">
                            <strong>Motivo:</strong> {mascotaDetalle.historiaClinica.motivoConsulta}
                          </p>
                        )}
                      </div>
                    </div>
                  )}

                  {/* Próximas citas */}
                  {mascotaDetalle.proximasCitas && mascotaDetalle.proximasCitas.length > 0 && (
                    <div className="border-t pt-4">
                      <h4 className="font-semibold text-gray-900 mb-2">Próximas Citas</h4>
                      <div className="space-y-2">
                        {mascotaDetalle.proximasCitas.map((cita, index) => (
                          <div key={index} className="bg-green-50 p-3 rounded-lg flex justify-between items-center">
                            <div>
                              <div className="font-medium">{cita.servicio?.nombre}</div>
                              <div className="text-sm text-gray-600">{cita.fechaCita} - {cita.horaCita}</div>
                            </div>
                            <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                              cita.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                            }`}>
                              {cita.estado}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {mascotaDetalle.mascota?.observaciones && (
                    <div className="border-t pt-4">
                      <h4 className="font-semibold text-gray-900 mb-2">Observaciones</h4>
                      <p className="text-gray-700 text-sm">{mascotaDetalle.mascota.observaciones}</p>
                    </div>
                  )}
                </div>
              )}

              <div className="mt-6 pt-4 border-t flex justify-end">
                <button
                  onClick={() => setMascotaDetalle(null)}
                  className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal de crear/editar mascota */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <h3 className="text-xl font-bold mb-4">
                {editingMascota ? 'Editar Mascota' : 'Nueva Mascota'}
              </h3>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.nombre}
                      onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Propietario *
                    </label>
                    <select
                      required
                      value={formData.idPropietario}
                      onChange={(e) => setFormData({...formData, idPropietario: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Seleccione un propietario</option>
                      {propietarios.map(prop => (
                        <option key={prop.idPersonal} value={prop.idPersonal}>
                          {prop.nombres} {prop.apellidos} - {prop.numeroDocumento}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Especie *
                    </label>
                    <select
                      required
                      value={formData.idEspecie}
                      onChange={(e) => handleEspecieChange(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Seleccione especie</option>
                      {especies.map(esp => (
                        <option key={esp.idEspecie} value={esp.idEspecie}>
                          {esp.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Raza *
                    </label>
                    <select
                      required
                      value={formData.idRaza}
                      onChange={(e) => setFormData({...formData, idRaza: e.target.value})}
                      disabled={!formData.idEspecie}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 disabled:bg-gray-100"
                    >
                      <option value="">Seleccione raza</option>
                      {razasFiltradas.map(raza => (
                        <option key={raza.idRaza} value={raza.idRaza}>
                          {raza.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Fecha de Nacimiento
                    </label>
                    <input
                      type="date"
                      value={formData.fechaNacimiento}
                      onChange={(e) => setFormData({...formData, fechaNacimiento: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Sexo *
                    </label>
                    <select
                      required
                      value={formData.sexo}
                      onChange={(e) => setFormData({...formData, sexo: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Seleccione</option>
                      <option value="MACHO">Macho</option>
                      <option value="HEMBRA">Hembra</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Color
                    </label>
                    <input
                      type="text"
                      value={formData.color}
                      onChange={(e) => setFormData({...formData, color: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Peso (kg)
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={formData.peso}
                      onChange={(e) => setFormData({...formData, peso: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Observaciones
                    </label>
                    <textarea
                      value={formData.observaciones}
                      onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                      rows="3"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
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
                    {editingMascota ? 'Actualizar' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
