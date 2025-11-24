import { useEffect, useState } from 'react';
import { mascotaService, propietarioService, especieService, razaService } from '../../services/api';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import Modal from '../../components/Modal';

export default function MascotasPage() {
  const [mascotas, setMascotas] = useState([]);
  const [propietarios, setPropietarios] = useState([]);
  const [especies, setEspecies] = useState([]);
  const [razas, setRazas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMascota, setEditingMascota] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    sexo: 'Macho',
    fechaNacimiento: '',
    color: '',
    peso: '',
    numeroMicrochip: '',
    esterilizado: false,
    observaciones: '',
    fotoUrl: '',
    idPropietario: '',
    idEspecie: '',
    idRaza: '',
    activo: true,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (formData.idEspecie) {
      loadRazas(formData.idEspecie);
    } else {
      setRazas([]);
    }
  }, [formData.idEspecie]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [mascotasRes, propietariosRes, especiesRes] = await Promise.all([
        mascotaService.getAll(),
        propietarioService.getAll(),
        especieService.getAll(),
      ]);
      console.log('Datos cargados:', { 
        mascotas: mascotasRes.data?.length, 
        propietarios: propietariosRes.data?.length,
        especies: especiesRes.data?.length 
      });
      setMascotas(mascotasRes.data || []);
      setPropietarios(propietariosRes.data || []);
      setEspecies(especiesRes.data || []);
      setError('');
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError(error.response?.data?.message || 'Error al cargar datos. Verifique la conexión con el servidor.');
      setMascotas([]);
      setPropietarios([]);
      setEspecies([]);
    } finally {
      setLoading(false);
    }
  };

  const loadRazas = async (idEspecie) => {
    try {
      const response = await razaService.getByEspecie(idEspecie);
      setRazas(response.data);
    } catch (error) {
      console.error('Error al cargar razas:', error);
      setRazas([]);
    }
  };

  const handleOpenModal = (mascota = null) => {
    if (mascota) {
      setEditingMascota(mascota);
      setFormData({
        nombre: mascota.nombre || '',
        sexo: mascota.sexo || 'Macho',
        fechaNacimiento: mascota.fechaNacimiento || '',
        color: mascota.color || '',
        peso: mascota.peso || '',
        numeroMicrochip: mascota.numeroMicrochip || '',
        esterilizado: mascota.esterilizado || false,
        observaciones: mascota.observaciones || '',
        fotoUrl: mascota.fotoUrl || '',
        idPropietario: mascota.propietario?.idPropietario || '',
        idEspecie: mascota.especie?.idEspecie || '',
        idRaza: mascota.raza?.idRaza || '',
        activo: mascota.activo !== undefined ? mascota.activo : true,
      });
      if (mascota.especie?.idEspecie) {
        loadRazas(mascota.especie.idEspecie);
      }
    } else {
      setEditingMascota(null);
      setFormData({
        nombre: '',
        sexo: 'Macho',
        fechaNacimiento: '',
        color: '',
        peso: '',
        numeroMicrochip: '',
        esterilizado: false,
        observaciones: '',
        fotoUrl: '',
        idPropietario: '',
        idEspecie: '',
        idRaza: '',
        activo: true,
      });
      setRazas([]);
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingMascota(null);
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const data = {
        ...formData,
        idPropietario: parseInt(formData.idPropietario),
        idEspecie: parseInt(formData.idEspecie),
        idRaza: formData.idRaza ? parseInt(formData.idRaza) : null,
        peso: formData.peso ? parseFloat(formData.peso) : null,
      };

      if (editingMascota) {
        await mascotaService.update(editingMascota.idMascota, data);
        setSuccess('Mascota actualizada exitosamente');
      } else {
        await mascotaService.create(data);
        setSuccess('Mascota creada exitosamente');
      }

      await loadData();
      setTimeout(() => {
        handleCloseModal();
      }, 1500);
    } catch (error) {
      console.error('Error al guardar mascota:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al guardar mascota. Verifique los datos ingresados.';
      setError(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de que desea eliminar esta mascota?')) {
      return;
    }

    try {
      setError('');
      setSuccess('');
      await mascotaService.delete(id);
      setSuccess('Mascota eliminada exitosamente');
      await loadData();
    } catch (error) {
      console.error('Error al eliminar mascota:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al eliminar mascota. Verifique que no tenga citas o historias clínicas asociadas.';
      setError(errorMessage);
    }
  };

  const filteredMascotas = mascotas.filter(
    (m) =>
      m.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      m.propietario?.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Mascotas</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Mascota
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg animate-slideDown">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg animate-slideDown">
          {success}
        </div>
      )}

      <div className="bg-white rounded-lg shadow-lg overflow-hidden animate-scaleIn">
        <div className="p-4 border-b bg-gray-50">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar mascotas..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-smooth"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          {filteredMascotas.length === 0 ? (
            <div className="col-span-full text-center py-8 text-gray-500">
              No se encontraron mascotas
            </div>
          ) : (
            filteredMascotas.map((mascota) => (
              <div
                key={mascota.idMascota}
                className="border rounded-lg p-4 hover:shadow-md transition-all animate-scaleIn"
              >
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-semibold text-lg text-gray-900">{mascota.nombre}</h3>
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleOpenModal(mascota)}
                      className="text-primary-600 hover:text-primary-900 transition-colors"
                      title="Editar"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(mascota.idMascota)}
                      className="text-red-600 hover:text-red-900 transition-colors"
                      title="Eliminar"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
                <p className="text-sm text-gray-600">Propietario: {mascota.propietario?.nombreCompleto || 'N/A'}</p>
                <p className="text-sm text-gray-600">Especie: {mascota.especie?.nombre || 'N/A'}</p>
                <p className="text-sm text-gray-600">Raza: {mascota.raza?.nombre || 'N/A'}</p>
                <p className="text-sm text-gray-600">Sexo: {mascota.sexo || 'N/A'}</p>
                {mascota.peso && <p className="text-sm text-gray-600">Peso: {mascota.peso} kg</p>}
              </div>
            ))
          )}
        </div>
      </div>

      {/* Modal de formulario */}
      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title={editingMascota ? 'Editar Mascota' : 'Nueva Mascota'}
        size="lg"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
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

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre *
              </label>
              <input
                type="text"
                required
                value={formData.nombre}
                onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Sexo *
              </label>
              <select
                required
                value={formData.sexo}
                onChange={(e) => setFormData({ ...formData, sexo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="Macho">Macho</option>
                <option value="Hembra">Hembra</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de Nacimiento
              </label>
              <input
                type="date"
                value={formData.fechaNacimiento}
                onChange={(e) => setFormData({ ...formData, fechaNacimiento: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Peso (kg)
              </label>
              <input
                type="number"
                step="0.1"
                min="0"
                value={formData.peso}
                onChange={(e) => setFormData({ ...formData, peso: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Propietario *
              </label>
              <select
                required
                value={formData.idPropietario}
                onChange={(e) => setFormData({ ...formData, idPropietario: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                disabled={propietarios.length === 0}
              >
                <option value="">
                  {propietarios.length === 0 ? 'No hay propietarios disponibles. Cree uno primero.' : 'Seleccione un propietario'}
                </option>
                {propietarios.map((prop) => (
                  <option key={prop.idPropietario} value={prop.idPropietario}>
                    {prop.nombreCompleto || `${prop.nombres} ${prop.apellidos}`} - {prop.documento || prop.tipoDocumento}
                  </option>
                ))}
              </select>
              {propietarios.length === 0 && (
                <p className="mt-1 text-sm text-yellow-600">
                  No hay propietarios registrados. Por favor, cree un propietario primero.
                </p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Especie *
              </label>
              <select
                required
                value={formData.idEspecie}
                onChange={(e) => setFormData({ ...formData, idEspecie: e.target.value, idRaza: '' })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione una especie</option>
                {especies.map((esp) => (
                  <option key={esp.idEspecie} value={esp.idEspecie}>
                    {esp.nombre}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Raza
              </label>
              <select
                value={formData.idRaza}
                onChange={(e) => setFormData({ ...formData, idRaza: e.target.value })}
                disabled={!formData.idEspecie}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent disabled:bg-gray-100"
              >
                <option value="">Seleccione una raza</option>
                {razas.map((raza) => (
                  <option key={raza.idRaza} value={raza.idRaza}>
                    {raza.nombre}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Color
              </label>
              <input
                type="text"
                value={formData.color}
                onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Número de Microchip
              </label>
              <input
                type="text"
                value={formData.numeroMicrochip}
                onChange={(e) => setFormData({ ...formData, numeroMicrochip: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="esterilizado"
              checked={formData.esterilizado}
              onChange={(e) => setFormData({ ...formData, esterilizado: e.target.checked })}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label htmlFor="esterilizado" className="ml-2 block text-sm text-gray-700">
              Esterilizado/Castrado
            </label>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Observaciones
            </label>
            <textarea
              value={formData.observaciones}
              onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })}
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={handleCloseModal}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              {editingMascota ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
