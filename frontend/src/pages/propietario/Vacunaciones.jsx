import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { vacunacionService, mascotaService, propietarioService, historiaClinicaService, citaService, veterinarioService, servicioService } from '../../services/api';
import { Plus, Search, Eye, Calendar, Syringe } from 'lucide-react';
import Modal from '../../components/Modal';
import { useNavigate } from 'react-router-dom';

export default function VacunacionesPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [vacunaciones, setVacunaciones] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [agendarModalOpen, setAgendarModalOpen] = useState(false);
  const [selectedVacunacion, setSelectedVacunacion] = useState(null);
  const [selectedMascota, setSelectedMascota] = useState(null);
  const [formData, setFormData] = useState({
    idMascota: '',
    idVeterinario: '',
    idServicio: '',
    fechaCita: '',
    horaCita: '',
    motivo: 'Vacunación',
    observaciones: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, [user]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');

      // Obtener propietario y sus mascotas
      let propietario = null;
      if (user?.rol === 'PROPIETARIO') {
        try {
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          propietario = propietarioRes.data;
        } catch (error) {
          console.error('Error al obtener propietario:', error);
        }
      } else if (user?.email) {
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          propietario = propietarioRes.data;
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
        }
      }

      if (propietario?.idPropietario) {
        // Obtener mascotas del propietario
        const mascotasRes = await mascotaService.getByPropietario(propietario.idPropietario);
        const mascotasData = mascotasRes.data || [];
        setMascotas(mascotasData);

        // Obtener todas las vacunaciones de las mascotas
        const todasVacunaciones = [];
        for (const mascota of mascotasData) {
          try {
            // Obtener historia clínica de la mascota
            const historiaRes = await historiaClinicaService.getByMascota(mascota.idMascota);
            const historia = historiaRes.data;
            
            if (historia?.idHistoriaClinica) {
              // Obtener vacunaciones de la historia clínica
              const vacunacionesRes = await vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica);
              const vacunacionesData = vacunacionesRes.data || [];
              // Agregar información de la mascota a cada vacunación
              vacunacionesData.forEach(v => {
                v.mascotaNombre = mascota.nombre;
                v.mascotaId = mascota.idMascota;
              });
              todasVacunaciones.push(...vacunacionesData);
            }
          } catch (error) {
            // La mascota puede no tener historia clínica o vacunaciones aún
            console.debug(`Mascota ${mascota.idMascota} no tiene historia clínica o vacunaciones`);
          }
        }
        setVacunaciones(todasVacunaciones);
      }

      // Obtener veterinarios y servicios disponibles
      const [veterinariosRes, serviciosRes] = await Promise.all([
        veterinarioService.getActivos(),
        servicioService.getAll(),
      ]);
      setVeterinarios(veterinariosRes.data || []);
      // Filtrar servicios de vacunación
      const serviciosVacunacion = (serviciosRes.data || []).filter(s => 
        s.tipoServicio === 'VACUNACION' || s.nombre?.toLowerCase().includes('vacun')
      );
      setServicios(serviciosVacunacion.length > 0 ? serviciosVacunacion : serviciosRes.data || []);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos. Por favor, recarga la página.');
    } finally {
      setLoading(false);
    }
  };

  const handleViewVacunacion = (vacunacion) => {
    setSelectedVacunacion(vacunacion);
    setViewModalOpen(true);
  };

  const handleAgendarVacunacion = (mascota = null) => {
    if (mascota) {
      setSelectedMascota(mascota);
      setFormData({
        ...formData,
        idMascota: mascota.idMascota,
      });
    } else {
      setSelectedMascota(null);
      setFormData({
        idMascota: '',
        idVeterinario: '',
        idServicio: '',
        fechaCita: '',
        horaCita: '',
        motivo: 'Vacunación',
        observaciones: '',
      });
    }
    setAgendarModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseAgendarModal = () => {
    setAgendarModalOpen(false);
    setSelectedMascota(null);
    setError('');
    setSuccess('');
  };

  const handleSubmitAgendar = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const citaData = {
        idMascota: parseInt(formData.idMascota),
        idVeterinario: parseInt(formData.idVeterinario),
        idServicio: parseInt(formData.idServicio),
        fechaCita: formData.fechaCita,
        horaCita: formData.horaCita,
        motivo: formData.motivo || 'Vacunación',
        observaciones: formData.observaciones || 'Cita para vacunación',
        esEmergencia: false,
      };

      await citaService.create(citaData);
      setSuccess('Cita para vacunación agendada exitosamente');
      setTimeout(() => {
        handleCloseAgendarModal();
        navigate('/propietario/citas');
      }, 1500);
    } catch (error) {
      console.error('Error al agendar cita:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Error al agendar la cita';
      setError(errorMessage);
    }
  };

  const filteredVacunaciones = vacunaciones.filter((v) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      v.nombreVacuna?.toLowerCase().includes(searchLower) ||
      v.laboratorio?.toLowerCase().includes(searchLower) ||
      v.lote?.toLowerCase().includes(searchLower) ||
      v.mascotaNombre?.toLowerCase().includes(searchLower)
    );
  });

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
        <h2 className="text-2xl font-bold text-gray-900">Vacunaciones de Mis Mascotas</h2>
        <button
          onClick={() => handleAgendarVacunacion()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Agendar Vacunación
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

      {/* Resumen de vacunaciones por mascota */}
      {mascotas.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mascotas.map((mascota) => {
            const vacunacionesMascota = vacunaciones.filter(v => v.mascotaId === mascota.idMascota);
            const proximasVacunaciones = vacunacionesMascota.filter(v => {
              if (!v.fechaProximaDosis) return false;
              const fechaProxima = new Date(v.fechaProximaDosis);
              return fechaProxima >= new Date();
            }).sort((a, b) => new Date(a.fechaProximaDosis) - new Date(b.fechaProximaDosis));

            return (
              <div key={mascota.idMascota} className="bg-white rounded-lg shadow-md p-4">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">{mascota.nombre}</h3>
                    <p className="text-sm text-gray-500">{mascota.especie?.nombre || 'N/A'}</p>
                  </div>
                  <button
                    onClick={() => handleAgendarVacunacion(mascota)}
                    className="text-primary-600 hover:text-primary-800"
                    title="Agendar vacunación"
                  >
                    <Calendar className="w-5 h-5" />
                  </button>
                </div>
                <div className="space-y-2">
                  <p className="text-sm text-gray-600">
                    <span className="font-medium">Total vacunaciones:</span> {vacunacionesMascota.length}
                  </p>
                  {proximasVacunaciones.length > 0 && (
                    <div className="mt-2 p-2 bg-yellow-50 rounded">
                      <p className="text-xs font-medium text-yellow-800">Próxima vacunación:</p>
                      <p className="text-xs text-yellow-700">
                        {proximasVacunaciones[0].nombreVacuna} - {new Date(proximasVacunaciones[0].fechaProximaDosis).toLocaleDateString('es-ES')}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      <div className="bg-white rounded-lg shadow-lg overflow-hidden animate-scaleIn">
        <div className="p-4 border-b bg-gray-50">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar vacunaciones..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mascota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vacuna</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Laboratorio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha Aplicación</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Próxima Dosis</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredVacunaciones.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    {vacunaciones.length === 0 
                      ? 'No hay vacunaciones registradas para tus mascotas. Agenda una cita para vacunación.'
                      : 'No se encontraron vacunaciones'}
                  </td>
                </tr>
              ) : (
                filteredVacunaciones.map((vacunacion) => (
                  <tr key={vacunacion.idVacunacion} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{vacunacion.mascotaNombre || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{vacunacion.nombreVacuna}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.laboratorio}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaAplicacion ? new Date(vacunacion.fechaAplicacion).toLocaleDateString('es-ES') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaProximaDosis ? (
                        <span className={new Date(vacunacion.fechaProximaDosis) <= new Date() ? 'text-red-600 font-medium' : ''}>
                          {new Date(vacunacion.fechaProximaDosis).toLocaleDateString('es-ES')}
                        </span>
                      ) : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.veterinario?.nombreCompleto || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleViewVacunacion(vacunacion)}
                        className="text-primary-600 hover:text-primary-900 transition-colors"
                        title="Ver detalles"
                      >
                        <Eye className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal para ver detalles de vacunación */}
      <Modal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        title="Detalles de Vacunación"
        size="md"
      >
        {selectedVacunacion && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Mascota</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.mascotaNombre || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Vacuna</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.nombreVacuna}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Laboratorio</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.laboratorio}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Lote</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.lote}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Vía de Administración</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.viaAdministracion || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Fecha de Aplicación</label>
                <p className="mt-1 text-sm text-gray-900">
                  {selectedVacunacion.fechaAplicacion ? new Date(selectedVacunacion.fechaAplicacion).toLocaleDateString('es-ES') : 'N/A'}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Próxima Dosis</label>
                <p className="mt-1 text-sm text-gray-900">
                  {selectedVacunacion.fechaProximaDosis ? new Date(selectedVacunacion.fechaProximaDosis).toLocaleDateString('es-ES') : 'N/A'}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Veterinario</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.veterinario?.nombreCompleto || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Esquema Completo</label>
                <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.esquemaCompleto ? 'Sí' : 'No'}</p>
              </div>
              {selectedVacunacion.observaciones && (
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                  <p className="mt-1 text-sm text-gray-900">{selectedVacunacion.observaciones}</p>
                </div>
              )}
            </div>
          </div>
        )}
      </Modal>

      {/* Modal para agendar vacunación */}
      <Modal
        isOpen={agendarModalOpen}
        onClose={handleCloseAgendarModal}
        title="Agendar Cita para Vacunación"
        size="md"
      >
        <form onSubmit={handleSubmitAgendar} className="space-y-4">
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
              {mascotas.map((mascota) => (
                <option key={mascota.idMascota} value={mascota.idMascota}>
                  {mascota.nombre} - {mascota.especie?.nombre || 'N/A'}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Veterinario *
            </label>
            <select
              required
              value={formData.idVeterinario}
              onChange={(e) => setFormData({ ...formData, idVeterinario: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Seleccione un veterinario</option>
              {veterinarios.map((vet) => (
                <option key={vet.idPersonal} value={vet.idPersonal}>
                  {vet.nombreCompleto}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Servicio de Vacunación *
            </label>
            <select
              required
              value={formData.idServicio}
              onChange={(e) => setFormData({ ...formData, idServicio: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Seleccione un servicio</option>
              {servicios.map((servicio) => (
                <option key={servicio.idServicio} value={servicio.idServicio}>
                  {servicio.nombre} - ${servicio.precio || 0}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha *
              </label>
              <input
                type="date"
                required
                min={new Date().toISOString().split('T')[0]}
                value={formData.fechaCita}
                onChange={(e) => setFormData({ ...formData, fechaCita: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora *
              </label>
              <input
                type="time"
                required
                value={formData.horaCita}
                onChange={(e) => setFormData({ ...formData, horaCita: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo
            </label>
            <input
              type="text"
              value={formData.motivo}
              onChange={(e) => setFormData({ ...formData, motivo: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Ej: Vacunación anual, Refuerzo, etc."
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
              placeholder="Información adicional sobre la vacunación..."
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={handleCloseAgendarModal}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              Agendar Cita
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

