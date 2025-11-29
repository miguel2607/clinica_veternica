import { useEffect, useState } from 'react';
import { historiaClinicaService, mascotaService } from '../../services/api';
import { Plus, Edit, Search, FileText, Eye } from 'lucide-react';
import Modal from '../../components/Modal';

export default function HistoriasClinicasPage() {
  const [historias, setHistorias] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedHistoria, setSelectedHistoria] = useState(null);
  const [editingHistoria, setEditingHistoria] = useState(null);
  const [formData, setFormData] = useState({
    idMascota: '',
    numeroHistoria: '',
    grupoSanguineo: '',
    alergias: '',
    enfermedadesCronicas: '',
    cirugiasPrevias: '',
    medicamentosActuales: '',
    observaciones: '',
    activo: true,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [historiasRes, mascotasRes] = await Promise.all([
        historiaClinicaService.getAll(),
        mascotaService.getAll(),
      ]);
      setHistorias(historiasRes.data || []);
      setMascotas(mascotasRes.data || []);
      setError('');
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (historia = null) => {
    if (historia) {
      setEditingHistoria(historia);
      setFormData({
        idMascota: historia.mascota?.idMascota || '',
        numeroHistoria: historia.numeroHistoria || '',
        grupoSanguineo: historia.grupoSanguineo || '',
        alergias: historia.alergias || '',
        enfermedadesCronicas: historia.enfermedadesCronicas || '',
        cirugiasPrevias: historia.cirugiasPrevias || '',
        medicamentosActuales: historia.medicamentosActuales || '',
        observaciones: historia.observaciones || '',
        activo: historia.activo !== undefined ? historia.activo : true,
      });
    } else {
      setEditingHistoria(null);
      setFormData({
        idMascota: '',
        numeroHistoria: '',
        grupoSanguineo: '',
        alergias: '',
        enfermedadesCronicas: '',
        cirugiasPrevias: '',
        medicamentosActuales: '',
        observaciones: '',
        activo: true,
      });
    }
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingHistoria(null);
    setError('');
    setSuccess('');
  };

  const handleViewHistoria = (historia) => {
    setSelectedHistoria(historia);
    setViewModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const data = {
        ...formData,
        idMascota: parseInt(formData.idMascota),
      };

      if (editingHistoria) {
        await historiaClinicaService.update(editingHistoria.idHistoriaClinica, data);
        setSuccess('Historia clínica actualizada exitosamente');
      } else {
        await historiaClinicaService.create(data);
        setSuccess('Historia clínica creada exitosamente');
      }
      await loadData();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al guardar historia clínica:', error);
      setError(error.response?.data?.message || 'Error al guardar historia clínica');
    }
  };

  const filteredHistorias = historias.filter(
    (h) => 
      h.mascota?.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      h.numeroHistoria?.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h2 className="text-2xl font-bold text-gray-900">Gestión de Historias Clínicas</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Historia Clínica
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
              placeholder="Buscar historias clínicas..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-smooth"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Número</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Propietario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Grupo Sanguíneo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredHistorias.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron historias clínicas
                  </td>
                </tr>
              ) : (
                filteredHistorias.map((historia) => (
                  <tr key={historia.idHistoriaClinica} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{historia.numeroHistoria}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{historia.mascota?.nombre || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{historia.mascota?.propietarioNombre || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{historia.grupoSanguineo || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        historia.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {historia.activo ? 'Activa' : 'Inactiva'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleViewHistoria(historia)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Ver detalles"
                        >
                          <Eye className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => handleOpenModal(historia)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Editar"
                        >
                          <Edit className="w-5 h-5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title={editingHistoria ? 'Editar Historia Clínica' : 'Nueva Historia Clínica'}
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
                Mascota *
              </label>
              <select
                required
                value={formData.idMascota}
                onChange={(e) => setFormData({ ...formData, idMascota: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione una mascota</option>
                {mascotas.map((m) => (
                  <option key={m.idMascota} value={m.idMascota}>
                    {m.nombre} - {m.propietario?.nombreCompleto}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Número de Historia *
              </label>
              <input
                type="text"
                required
                value={formData.numeroHistoria}
                onChange={(e) => setFormData({ ...formData, numeroHistoria: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Grupo Sanguíneo
              </label>
              <input
                type="text"
                value={formData.grupoSanguineo}
                onChange={(e) => setFormData({ ...formData, grupoSanguineo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Alergias
            </label>
            <textarea
              value={formData.alergias}
              onChange={(e) => setFormData({ ...formData, alergias: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Enfermedades Crónicas
            </label>
            <textarea
              value={formData.enfermedadesCronicas}
              onChange={(e) => setFormData({ ...formData, enfermedadesCronicas: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Cirugías Previas
            </label>
            <textarea
              value={formData.cirugiasPrevias}
              onChange={(e) => setFormData({ ...formData, cirugiasPrevias: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Medicamentos Actuales
            </label>
            <textarea
              value={formData.medicamentosActuales}
              onChange={(e) => setFormData({ ...formData, medicamentosActuales: e.target.value })}
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
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

          <div className="flex items-center">
            <input
              type="checkbox"
              id="activo"
              checked={formData.activo}
              onChange={(e) => setFormData({ ...formData, activo: e.target.checked })}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label htmlFor="activo" className="ml-2 block text-sm text-gray-700">
              Activa
            </label>
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
              {editingHistoria ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        title="Detalles de Historia Clínica"
        size="lg"
      >
        {selectedHistoria && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <h3 className="text-sm font-medium text-gray-500">Número de Historia</h3>
                <p className="text-lg font-semibold text-gray-900">{selectedHistoria.numeroHistoria}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Mascota</h3>
                <p className="text-lg text-gray-900">{selectedHistoria.mascota?.nombre}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Propietario</h3>
                <p className="text-lg text-gray-900">{selectedHistoria.mascota?.propietarioNombre || 'N/A'}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Grupo Sanguíneo</h3>
                <p className="text-lg text-gray-900">{selectedHistoria.grupoSanguineo || 'N/A'}</p>
              </div>
            </div>

            {selectedHistoria.alergias && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Alergias</h3>
                <p className="text-gray-900">{selectedHistoria.alergias}</p>
              </div>
            )}

            {selectedHistoria.enfermedadesCronicas && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Enfermedades Crónicas</h3>
                <p className="text-gray-900">{selectedHistoria.enfermedadesCronicas}</p>
              </div>
            )}

            {selectedHistoria.cirugiasPrevias && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Cirugías Previas</h3>
                <p className="text-gray-900">{selectedHistoria.cirugiasPrevias}</p>
              </div>
            )}

            {selectedHistoria.medicamentosActuales && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Medicamentos Actuales</h3>
                <p className="text-gray-900">{selectedHistoria.medicamentosActuales}</p>
              </div>
            )}

            {selectedHistoria.observaciones && (
              <div>
                <h3 className="text-sm font-medium text-gray-500">Observaciones</h3>
                <p className="text-gray-900">{selectedHistoria.observaciones}</p>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}

