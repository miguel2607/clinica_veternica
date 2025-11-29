import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { vacunacionService, historiaClinicaService, veterinarioService, insumoService, citaService } from '../../services/api';
import { Plus, Search, Eye, Syringe } from 'lucide-react';
import Modal from '../../components/Modal';

export default function VacunacionesPage() {
  const { user } = useAuth();
  const [vacunaciones, setVacunaciones] = useState([]);
  const [historiasClinicas, setHistoriasClinicas] = useState([]);
  const [veterinario, setVeterinario] = useState(null);
  const [insumos, setInsumos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedVacunacion, setSelectedVacunacion] = useState(null);
  const [formData, setFormData] = useState({
    idHistoriaClinica: '',
    nombreVacuna: '',
    laboratorio: '',
    lote: '',
    fechaAplicacion: '',
    fechaProximaDosis: '',
    viaAdministracion: 'SUBCUTANEA',
    observaciones: '',
    esquemaCompleto: false,
    idInsumo: '',
    cantidadUsada: 1,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadVeterinario();
  }, [user]);

  useEffect(() => {
    if (veterinario) {
      loadData();
    }
  }, [veterinario]);

  const loadVeterinario = async () => {
    try {
      console.log('🔍 Obteniendo perfil del veterinario...');
      console.log('👤 Usuario actual:', user);
      
      let vet = null;
      
      // Intentar obtener el perfil usando el endpoint mi-perfil
      try {
        console.log('📞 Intentando obtener perfil con obtenerMiPerfil()...');
        const veterinarioRes = await veterinarioService.obtenerMiPerfil();
        vet = veterinarioRes.data;
        console.log('✅ Veterinario obtenido con obtenerMiPerfil:', vet);
      } catch (error404) {
        console.log('⚠️ obtenerMiPerfil falló, buscando manualmente...', error404);
        // Si no existe el endpoint, buscar manualmente
        try {
          const todosVeterinarios = await veterinarioService.getAll();
          console.log('📋 Total de veterinarios encontrados:', todosVeterinarios.data?.length || 0);
          
          // Buscar por email (correo)
          vet = todosVeterinarios.data.find(v => {
            const match = v.correo && v.correo.toLowerCase() === user?.email?.toLowerCase();
            console.log(`🔍 Comparando email (correo): "${v.correo}" === "${user?.email}" = ${match}`);
            return match;
          });

          // Buscar por email del usuario asociado
          if (!vet) {
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario?.email && v.usuario.email.toLowerCase() === user?.email?.toLowerCase();
              console.log(`🔍 Comparando email (usuario.email): "${v.usuario?.email}" === "${user?.email}" = ${match}`);
              return match;
            });
          }

          // Buscar por idUsuario
          if (!vet && user?.idUsuario) {
            console.log(`🔍 Buscando por idUsuario: ${user.idUsuario}`);
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario && v.usuario.idUsuario === user.idUsuario;
              console.log(`🔍 Comparando usuario: v.usuario.idUsuario=${v.usuario?.idUsuario} === ${user.idUsuario} = ${match}`);
              return match;
            });
          }

          // Buscar por username
          if (!vet && user?.username) {
            console.log(`🔍 Buscando por username: ${user.username}`);
            vet = todosVeterinarios.data.find(v => {
              const match = v.usuario?.username && v.usuario.username === user.username;
              console.log(`🔍 Comparando username: v.usuario.username="${v.usuario?.username}" === "${user.username}" = ${match}`);
              return match;
            });
          }

          // Último recurso: buscar por similitud de nombre
          if (!vet && (user?.nombre || user?.username)) {
            console.log('🔍 Buscando por similitud de nombre (último recurso)...');
            const nombreUsuario = (user.nombre || user.username || '').toLowerCase().split(' ')[0];
            vet = todosVeterinarios.data.find(v => {
              const match = v.nombres && v.nombres.toLowerCase().includes(nombreUsuario);
              console.log(`🔍 Comparando nombre: v.nombres="${v.nombres}" incluye "${nombreUsuario}" = ${match}`);
              return match;
            });
          }
          
          if (vet) {
            console.log('✅ Veterinario encontrado manualmente:', vet);
          } else {
            console.error('❌ No se encontró veterinario con ningún método');
          }
        } catch (error2) {
          console.error('❌ Error al buscar veterinarios:', error2);
        }
      }
      
      if (vet && vet.idPersonal) {
        setVeterinario(vet);
      } else {
        console.error('❌ No se pudo encontrar el veterinario o no tiene idPersonal');
        setError('No se encontró el perfil del veterinario');
      }
    } catch (error) {
      console.error('Error al cargar veterinario:', error);
      setError('Error al cargar el perfil del veterinario');
    }
  };

  const loadData = async () => {
    try {
      setLoading(true);
      
      // Obtener historias clínicas de los pacientes del veterinario
      const citasRes = await citaService.getByVeterinario(veterinario.idPersonal);
      const citas = citasRes.data || [];
      
      const historiasUnicas = new Map();
      for (const cita of citas) {
        if (cita.mascota?.idMascota) {
          try {
            const historiaRes = await historiaClinicaService.getByMascota(cita.mascota.idMascota);
            const historia = historiaRes.data;
            if (historia && !historiasUnicas.has(historia.idHistoriaClinica)) {
              historiasUnicas.set(historia.idHistoriaClinica, historia);
            }
          } catch (error) {
            // La mascota puede no tener historia clínica aún
          }
        }
      }
      
      const historias = Array.from(historiasUnicas.values());
      setHistoriasClinicas(historias);
      
      // Obtener todas las vacunaciones de estas historias
      const todasVacunaciones = [];
      for (const historia of historias) {
        try {
          const vacunacionesRes = await vacunacionService.getByHistoriaClinica(historia.idHistoriaClinica);
          todasVacunaciones.push(...(vacunacionesRes.data || []));
        } catch (error) {
          console.error(`Error al cargar vacunaciones de historia ${historia.idHistoriaClinica}:`, error);
        }
      }
      setVacunaciones(todasVacunaciones);
      
      // Cargar insumos disponibles
      const insumosRes = await insumoService.getActivos();
      setInsumos((insumosRes.data || []).filter(i => i.activo));
      
      setError('');
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = () => {
    setFormData({
      idHistoriaClinica: '',
      nombreVacuna: '',
      laboratorio: '',
      lote: '',
      fechaAplicacion: new Date().toISOString().split('T')[0],
      fechaProximaDosis: '',
      viaAdministracion: 'SUBCUTANEA',
      observaciones: '',
      esquemaCompleto: false,
      idInsumo: '',
      cantidadUsada: 1,
    });
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setError('');
    setSuccess('');
  };

  const handleViewVacunacion = (vacunacion) => {
    setSelectedVacunacion(vacunacion);
    setViewModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!veterinario) {
      setError('No se encontró el perfil del veterinario');
      return;
    }

    // Validar que se haya seleccionado un insumo
    if (!formData.idInsumo || formData.idInsumo === '') {
      setError('Debe seleccionar un insumo del inventario para la vacunación');
      return;
    }

    // Validar cantidad
    const cantidadUsada = parseInt(formData.cantidadUsada) || 1;
    const selectedInsumo = insumos.find(i => i.idInsumo.toString() === formData.idInsumo);
    if (selectedInsumo && cantidadUsada > (selectedInsumo.cantidadStock || 0)) {
      setError(`La cantidad (${cantidadUsada}) excede el stock disponible (${selectedInsumo.cantidadStock || 0})`);
      return;
    }

    try {
      const data = {
        idVeterinario: veterinario.idPersonal,
        nombreVacuna: formData.nombreVacuna,
        laboratorio: formData.laboratorio,
        lote: formData.lote,
        fechaAplicacion: formData.fechaAplicacion,
        fechaProximaDosis: formData.fechaProximaDosis,
        viaAdministracion: formData.viaAdministracion,
        observaciones: formData.observaciones,
        esquemaCompleto: formData.esquemaCompleto,
        idInsumo: parseInt(formData.idInsumo),
        cantidadUsada: cantidadUsada,
      };

      await vacunacionService.create(parseInt(formData.idHistoriaClinica), data);
      setSuccess('Vacunación creada exitosamente');
      await loadData();
      setTimeout(() => handleCloseModal(), 1500);
    } catch (error) {
      console.error('Error al crear vacunación:', error);
      console.error('Error response:', error.response?.data);
      
      let errorMessage = 'Error al crear vacunación';
      
      if (error.response?.data) {
        const errorData = error.response.data;
        
        // Si hay errores de validación como lista (errors)
        if (Array.isArray(errorData.errors) && errorData.errors.length > 0) {
          errorMessage = `Errores de validación: ${errorData.errors.join(', ')}`;
        }
        // Si hay errores de validación como mapa (validationErrors)
        else if (errorData.validationErrors && typeof errorData.validationErrors === 'object') {
          const validationErrors = Object.entries(errorData.validationErrors)
            .map(([field, message]) => `${field}: ${message}`)
            .join(', ');
          errorMessage = `Errores de validación: ${validationErrors}`;
        }
        // Si hay un mensaje directo
        else if (errorData.message) {
          errorMessage = errorData.message;
        }
        // Si hay un error genérico
        else if (errorData.error) {
          errorMessage = errorData.error;
        }
        // Si es un string directo
        else if (typeof errorData === 'string') {
          errorMessage = errorData;
        }
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      setError(errorMessage);
    }
  };

  const filteredVacunaciones = vacunaciones.filter((v) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      v.nombreVacuna?.toLowerCase().includes(searchLower) ||
      v.laboratorio?.toLowerCase().includes(searchLower) ||
      v.lote?.toLowerCase().includes(searchLower)
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
        <h2 className="text-2xl font-bold text-gray-900">Vacunaciones de Mis Pacientes</h2>
        <button
          onClick={handleOpenModal}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Vacunación
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vacuna</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Laboratorio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lote</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha Aplicación</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Próxima Dosis</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredVacunaciones.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-gray-500">
                    No se encontraron vacunaciones
                  </td>
                </tr>
              ) : (
                filteredVacunaciones.map((vacunacion) => (
                  <tr key={vacunacion.idVacunacion} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{vacunacion.nombreVacuna}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.laboratorio}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{vacunacion.lote}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaAplicacion ? new Date(vacunacion.fechaAplicacion).toLocaleDateString('es-ES') : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacunacion.fechaProximaDosis ? new Date(vacunacion.fechaProximaDosis).toLocaleDateString('es-ES') : 'N/A'}
                    </td>
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

      {/* Modal para crear vacunación */}
      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title="Nueva Vacunación"
        size="xl"
      >
        <form onSubmit={handleSubmit} className="space-y-4 max-h-[70vh] overflow-y-auto">
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
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Historia Clínica *
              </label>
              <select
                required
                value={formData.idHistoriaClinica}
                onChange={(e) => setFormData({ ...formData, idHistoriaClinica: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione una historia clínica</option>
                {historiasClinicas.map((historia) => (
                  <option key={historia.idHistoriaClinica} value={historia.idHistoriaClinica}>
                    {historia.mascota?.nombre || 'Mascota'} - {historia.numeroHistoria || historia.idHistoriaClinica}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre de la Vacuna *
              </label>
              <input
                type="text"
                required
                value={formData.nombreVacuna}
                onChange={(e) => setFormData({ ...formData, nombreVacuna: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Ej: Antirrábica, Polivalente"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Laboratorio *
              </label>
              <input
                type="text"
                required
                value={formData.laboratorio}
                onChange={(e) => setFormData({ ...formData, laboratorio: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Lote *
              </label>
              <input
                type="text"
                required
                value={formData.lote}
                onChange={(e) => setFormData({ ...formData, lote: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Vía de Administración
              </label>
              <select
                value={formData.viaAdministracion}
                onChange={(e) => setFormData({ ...formData, viaAdministracion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="SUBCUTANEA">Subcutánea</option>
                <option value="INTRAMUSCULAR">Intramuscular</option>
                <option value="INTRANASAL">Intranasal</option>
                <option value="ORAL">Oral</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de Aplicación *
              </label>
              <input
                type="date"
                required
                value={formData.fechaAplicacion}
                onChange={(e) => setFormData({ ...formData, fechaAplicacion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha Próxima Dosis *
              </label>
              <input
                type="date"
                required
                value={formData.fechaProximaDosis}
                onChange={(e) => setFormData({ ...formData, fechaProximaDosis: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Insumo (Vacuna del Inventario) *
              </label>
              <select
                required
                value={formData.idInsumo}
                onChange={(e) => {
                  setFormData({ 
                    ...formData, 
                    idInsumo: e.target.value,
                    cantidadUsada: 1 // Resetear cantidad cuando cambia el insumo
                  });
                }}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione un insumo del inventario</option>
                {insumos.map((insumo) => (
                  <option key={insumo.idInsumo} value={insumo.idInsumo}>
                    {insumo.nombre} - Stock: {insumo.cantidadStock || 0} {insumo.unidadMedida || 'unidades'}
                    {insumo.cantidadStock <= (insumo.stockMinimo || 0) ? ' ⚠️ Stock bajo' : ''}
                  </option>
                ))}
              </select>
              {insumos.length === 0 && (
                <p className="mt-1 text-sm text-amber-600">
                  No hay insumos disponibles. Contacte al administrador para crear insumos.
                </p>
              )}
            </div>

            {formData.idInsumo && (() => {
              const selectedInsumo = insumos.find(i => i.idInsumo.toString() === formData.idInsumo);
              const stockDisponible = selectedInsumo?.cantidadStock || 0;
              const stockMinimo = selectedInsumo?.stockMinimo || 0;
              
              return (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Cantidad Usada *
                  </label>
                  <input
                    type="number"
                    required
                    min="1"
                    max={stockDisponible}
                    value={formData.cantidadUsada}
                    onChange={(e) => {
                      const cantidad = parseInt(e.target.value) || 1;
                      if (cantidad > stockDisponible) {
                        setError(`La cantidad no puede ser mayor al stock disponible (${stockDisponible})`);
                      } else {
                        setError('');
                        setFormData({ ...formData, cantidadUsada: cantidad });
                      }
                    }}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                  <div className="mt-1 text-sm text-gray-600">
                    <p>Stock disponible: <span className="font-medium">{stockDisponible} {selectedInsumo?.unidadMedida || 'unidades'}</span></p>
                    {stockDisponible <= stockMinimo && (
                      <p className="text-amber-600 font-medium">⚠️ Stock bajo (mínimo: {stockMinimo})</p>
                    )}
                    {formData.cantidadUsada > stockDisponible && (
                      <p className="text-red-600 font-medium">❌ Cantidad excede el stock disponible</p>
                    )}
                  </div>
                </div>
              );
            })()}

            <div className="md:col-span-2">
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

            <div className="md:col-span-2 flex items-center">
              <input
                type="checkbox"
                id="esquemaCompleto"
                checked={formData.esquemaCompleto}
                onChange={(e) => setFormData({ ...formData, esquemaCompleto: e.target.checked })}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label htmlFor="esquemaCompleto" className="ml-2 block text-sm text-gray-700">
                Esquema completo
              </label>
            </div>
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
              Crear Vacunación
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal para ver detalles */}
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
    </div>
  );
}

