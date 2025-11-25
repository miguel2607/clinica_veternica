import { useEffect, useState } from 'react';
import { mascotaService, propietarioService, especieService, razaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import Modal from '../../components/Modal';

export default function MisMascotasPage() {
  const { user } = useAuth();
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMascota, setSelectedMascota] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [mascotaDetalles, setMascotaDetalles] = useState(null);
  const [loadingDetalles, setLoadingDetalles] = useState(false);
  
  // Estados para formulario de crear/editar
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingMascota, setEditingMascota] = useState(null);
  const [especies, setEspecies] = useState([]);
  const [razas, setRazas] = useState([]);
  const [razasFiltradas, setRazasFiltradas] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    nombre: '',
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
  }, [user]);

  const loadMascotas = async () => {
    try {
      setLoading(true);
      
      // Obtener o crear propietario automáticamente
      if (user?.rol === 'PROPIETARIO') {
        try {
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          
          // Obtener mascotas del propietario
          if (propietarioRes.data?.idPropietario) {
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            setMascotas(mascotasRes.data || []);
          } else {
            setMascotas([]);
          }
        } catch (error) {
          console.error('Error al obtener/crear propietario:', error);
          setMascotas([]);
        }
      } else if (user?.email) {
        // Fallback: intentar buscar por email
        try {
          const propietarioRes = await propietarioService.getByEmail(user.email);
          if (propietarioRes.data?.idPropietario) {
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            setMascotas(mascotasRes.data || []);
          } else {
            setMascotas([]);
          }
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
          setMascotas([]);
        }
      } else {
        setMascotas([]);
      }
    } catch (error) {
      console.error('Error al cargar mascotas:', error);
      setMascotas([]);
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalles = async (mascota) => {
    setSelectedMascota(mascota);
    setModalOpen(true);
    setLoadingDetalles(true);
    setMascotaDetalles(null);

    try {
      const response = await mascotaService.getById(mascota.idMascota);
      setMascotaDetalles(response.data);
    } catch (error) {
      console.error('Error al cargar detalles de la mascota:', error);
      // Si falla, usar los datos básicos que ya tenemos
      setMascotaDetalles(mascota);
    } finally {
      setLoadingDetalles(false);
    }
  };

  const calcularEdad = (fechaNacimiento) => {
    if (!fechaNacimiento) return 'N/A';
    const hoy = new Date();
    const nacimiento = new Date(fechaNacimiento);
    const años = Math.floor((hoy - nacimiento) / (365.25 * 24 * 60 * 60 * 1000));
    const meses = Math.floor(((hoy - nacimiento) % (365.25 * 24 * 60 * 60 * 1000)) / (30.44 * 24 * 60 * 60 * 1000));
    
    if (años === 0) {
      return meses > 0 ? `${meses} ${meses === 1 ? 'mes' : 'meses'}` : 'Menos de 1 mes';
    }
    return meses > 0 ? `${años} ${años === 1 ? 'año' : 'años'}, ${meses} ${meses === 1 ? 'mes' : 'meses'}` : `${años} ${años === 1 ? 'año' : 'años'}`;
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return 'N/A';
    return new Date(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const loadDatosFormulario = async () => {
    try {
      const [especiesRes, razasRes] = await Promise.all([
        especieService.getActivas(),
        razaService.getActivas()
      ]);
      const especiesData = especiesRes.data || [];
      const razasData = razasRes.data || [];
      setEspecies(especiesData);
      setRazas(razasData);
      return { especies: especiesData, razas: razasData };
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos del formulario');
      return { especies: [], razas: [] };
    }
  };

  const handleOpenFormModal = async (mascota = null) => {
    const { razas: razasData } = await loadDatosFormulario();
    setError('');
    setSuccess('');

    if (mascota) {
      setEditingMascota(mascota);
      const idEspecie = mascota.raza?.especie?.idEspecie || '';
      setFormData({
        nombre: mascota.nombre || '',
        idEspecie: idEspecie,
        idRaza: mascota.raza?.idRaza || '',
        fechaNacimiento: mascota.fechaNacimiento || '',
        sexo: mascota.sexo || '',
        color: mascota.color || '',
        peso: mascota.peso || '',
        observaciones: mascota.observaciones || '',
        activo: mascota.activo !== undefined ? mascota.activo : true
      });

      // Filtrar razas por especie usando los datos cargados
      if (idEspecie) {
        const razasPorEspecie = razasData.filter(r => r.especie?.idEspecie === idEspecie);
        setRazasFiltradas(razasPorEspecie);
      } else {
        setRazasFiltradas([]);
      }
    } else {
      setEditingMascota(null);
      setFormData({
        nombre: '',
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
    setFormModalOpen(true);
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
      // Para propietarios, NO enviamos idPropietario - el backend lo asigna automáticamente
      const data = {
        nombre: formData.nombre,
        sexo: formData.sexo, // Ya está en formato correcto (Macho/Hembra)
        fechaNacimiento: formData.fechaNacimiento || null,
        color: formData.color || null,
        peso: formData.peso ? parseFloat(formData.peso) : null,
        observaciones: formData.observaciones || null,
        activo: formData.activo !== undefined ? formData.activo : true,
        idEspecie: parseInt(formData.idEspecie),
        idRaza: formData.idRaza ? parseInt(formData.idRaza) : null,
        // idPropietario no se incluye - el backend lo asigna automáticamente
      };
      
      console.log('📤 Datos a enviar:', data);

      if (editingMascota) {
        await mascotaService.update(editingMascota.idMascota, data);
        setSuccess('Mascota actualizada exitosamente');
      } else {
        await mascotaService.create(data);
        setSuccess('Mascota creada exitosamente');
      }

      await loadMascotas();
      setTimeout(() => {
        setFormModalOpen(false);
        setSuccess('');
      }, 1500);
    } catch (error) {
      console.error('Error al guardar mascota:', error);
      setError(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando mascotas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Mis Mascotas</h2>
        <button
          onClick={() => handleOpenFormModal()}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors inline-flex items-center gap-2"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nueva Mascota
        </button>
      </div>

      {error && !formModalOpen && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {success && !formModalOpen && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {success}
        </div>
      )}

      {mascotas.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
          No tienes mascotas registradas
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {mascotas.map((mascota) => (
            <div key={mascota.idMascota} className="bg-white rounded-lg shadow p-6">
              <h3 className="text-xl font-semibold mb-2">{mascota.nombre}</h3>
              <div className="space-y-2 text-sm text-gray-600">
                <p><span className="font-medium">Especie:</span> {mascota.especie?.nombre || 'N/A'}</p>
                <p><span className="font-medium">Raza:</span> {mascota.raza?.nombre || 'N/A'}</p>
                <p><span className="font-medium">Edad:</span> {calcularEdad(mascota.fechaNacimiento)}</p>
              </div>
              <div className="mt-4 flex gap-2">
                <button 
                  onClick={() => handleVerDetalles(mascota)}
                  className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700 transition-colors"
                >
                  Ver Detalles
                </button>
                <button 
                  onClick={() => handleOpenFormModal(mascota)}
                  className="flex-1 border border-primary-600 text-primary-600 py-2 rounded-lg hover:bg-primary-50 transition-colors"
                >
                  Editar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal de Detalles */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setSelectedMascota(null);
          setMascotaDetalles(null);
        }}
        title={`Detalles de ${selectedMascota?.nombre || 'Mascota'}`}
        size="md"
      >
        {loadingDetalles ? (
          <div className="text-center py-8">Cargando detalles...</div>
        ) : mascotaDetalles ? (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Nombre</label>
                <p className="text-lg font-semibold text-gray-900">{mascotaDetalles.nombre}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Sexo</label>
                <p className="text-lg text-gray-900">{mascotaDetalles.sexo || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Especie</label>
                <p className="text-lg text-gray-900">{mascotaDetalles.especie?.nombre || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Raza</label>
                <p className="text-lg text-gray-900">{mascotaDetalles.raza?.nombre || 'N/A'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Fecha de Nacimiento</label>
                <p className="text-lg text-gray-900">{formatearFecha(mascotaDetalles.fechaNacimiento)}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Edad</label>
                <p className="text-lg text-gray-900">{calcularEdad(mascotaDetalles.fechaNacimiento)}</p>
              </div>
              {mascotaDetalles.peso && (
                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">Peso</label>
                  <p className="text-lg text-gray-900">{mascotaDetalles.peso} kg</p>
                </div>
              )}
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Estado</label>
                <span className={`inline-block px-3 py-1 text-sm font-semibold rounded-full ${
                  mascotaDetalles.activo 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {mascotaDetalles.activo ? 'Activa' : 'Inactiva'}
                </span>
              </div>
            </div>
            {mascotaDetalles.descripcion && (
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Descripción</label>
                <p className="text-gray-900">{mascotaDetalles.descripcion}</p>
              </div>
            )}
            {mascotaDetalles.observaciones && (
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Observaciones</label>
                <p className="text-gray-900">{mascotaDetalles.observaciones}</p>
              </div>
            )}
          </div>
        ) : (
          <div className="text-center py-8 text-red-500">
            Error al cargar los detalles de la mascota
          </div>
        )}
      </Modal>

      {/* Modal de crear/editar mascota */}
      {formModalOpen && (
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
                      <option value="Macho">Macho</option>
                      <option value="Hembra">Hembra</option>
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
                    onClick={() => {
                      setFormModalOpen(false);
                      setError('');
                      setSuccess('');
                    }}
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

