import { useEffect, useState } from 'react';
import { citaService, mascotaService, veterinarioService, servicioService, propietarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Plus, Search, CheckCircle, XCircle, Clock, Eye, Edit } from 'lucide-react';
import Modal from '../../components/Modal';

export default function MisCitasPage() {
  const { user } = useAuth();
  const [citas, setCitas] = useState([]);
  const [mascotas, setMascotas] = useState([]);
  const [veterinarios, setVeterinarios] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [propietario, setPropietario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [selectedCita, setSelectedCita] = useState(null);
  const [editingCita, setEditingCita] = useState(null);
  const [cancelMotivo, setCancelMotivo] = useState('');
  const [formData, setFormData] = useState({
    idMascota: '',
    idVeterinario: '',
    idServicio: '',
    fechaCita: '',
    horaCita: '',
    motivo: '',
    observaciones: '',
    esEmergencia: false,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    console.log('Usuario actual:', user);
    console.log('Email del usuario:', user?.email);
    loadData();
  }, [user]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Obtener o crear propietario automáticamente
      if (user?.rol === 'PROPIETARIO') {
        try {
          console.log('Obteniendo o creando perfil de propietario para usuario:', user.username);
          const propietarioRes = await propietarioService.obtenerOCrearMiPerfil();
          console.log('Propietario obtenido/creado:', propietarioRes.data);
          setPropietario(propietarioRes.data);
          
          // Obtener mascotas del propietario
          if (propietarioRes.data?.idPropietario) {
            console.log('Obteniendo mascotas del propietario ID:', propietarioRes.data.idPropietario);
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            console.log('Mascotas obtenidas:', mascotasRes.data);
            setMascotas(mascotasRes.data || []);
            
            // Obtener citas y filtrar por propietario (filtrar por IDs de mascotas del propietario)
            const citasRes = await citaService.getAll();
            const mascotasIds = (mascotasRes.data || []).map(m => m.idMascota);
            console.log('IDs de mascotas del propietario:', mascotasIds);
            const citasFiltradas = (citasRes.data || []).filter(cita => 
              cita.mascota?.idMascota && mascotasIds.includes(cita.mascota.idMascota)
            );
            console.log('Citas filtradas:', citasFiltradas.length);
            // Verificar que todas las citas tengan idCita
            citasFiltradas.forEach(cita => {
              if (!cita.idCita) {
                console.warn('Cita sin idCita:', cita);
              }
            });
            setCitas(citasFiltradas);
          } else {
            console.warn('Propietario no tiene ID');
            setMascotas([]);
            setCitas([]);
          }
        } catch (error) {
          console.error('Error al obtener/crear propietario:', error);
          console.error('Status del error:', error.response?.status);
          console.error('Datos del error:', error.response?.data);
          setError(`Error al cargar la información del propietario: ${error.response?.data?.message || error.message}. Por favor, intenta de nuevo.`);
          setMascotas([]);
          setCitas([]);
        }
      } else if (user?.email) {
        // Fallback: intentar buscar por email si no es PROPIETARIO o si el endpoint anterior falla
        try {
          console.log('Buscando propietario con email (fallback):', user.email);
          const propietarioRes = await propietarioService.getByEmail(user.email);
          console.log('Propietario encontrado:', propietarioRes.data);
          setPropietario(propietarioRes.data);
          
          // Obtener mascotas del propietario
          if (propietarioRes.data?.idPropietario) {
            console.log('Obteniendo mascotas del propietario ID:', propietarioRes.data.idPropietario);
            const mascotasRes = await mascotaService.getByPropietario(propietarioRes.data.idPropietario);
            console.log('Mascotas obtenidas:', mascotasRes.data);
            setMascotas(mascotasRes.data || []);
            
            // Obtener citas y filtrar por propietario
            const citasRes = await citaService.getAll();
            const mascotasIds = (mascotasRes.data || []).map(m => m.idMascota);
            console.log('IDs de mascotas del propietario:', mascotasIds);
            const citasFiltradas = (citasRes.data || []).filter(cita => 
              cita.mascota?.idMascota && mascotasIds.includes(cita.mascota.idMascota)
            );
            console.log('Citas filtradas:', citasFiltradas.length);
            // Verificar que todas las citas tengan idCita
            citasFiltradas.forEach(cita => {
              if (!cita.idCita) {
                console.warn('Cita sin idCita:', cita);
              }
            });
            setCitas(citasFiltradas);
          } else {
            console.warn('Propietario no tiene ID');
            setMascotas([]);
            setCitas([]);
          }
        } catch (error) {
          console.error('Error al obtener propietario por email:', error);
          if (error.response?.status === 404) {
            setError(`No se encontró un registro de propietario. Se intentó crear automáticamente pero hubo un error. Por favor, contacta al administrador.`);
          } else {
            setError(`Error al cargar la información del propietario: ${error.response?.data?.message || error.message}. Por favor, intenta de nuevo.`);
          }
          setMascotas([]);
          setCitas([]);
        }
      } else {
        console.warn('Usuario no tiene email ni rol PROPIETARIO');
        setError('No se pudo identificar el usuario. Por favor, contacta al administrador.');
        setMascotas([]);
        setCitas([]);
      }
      
      // Obtener veterinarios y servicios disponibles
      const [veterinariosRes, serviciosRes] = await Promise.all([
        veterinarioService.getActivos(),
        servicioService.getAll(),
      ]);
      setVeterinarios(veterinariosRes.data || []);
      setServicios(serviciosRes.data || []);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      setError('Error al cargar datos. Por favor, recarga la página.');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (cita = null) => {
    // Si se pasa un evento (cuando se hace clic en "Nueva Cita"), ignorarlo
    if (cita && (cita.nativeEvent || cita.target || cita.type === 'click')) {
      cita = null;
    }
    
    // Si cita es null, undefined, o no es un objeto válido, abrir modal para crear nueva cita
    if (!cita || typeof cita !== 'object' || (!cita.idCita && !cita.id)) {
      // Es una nueva cita
      setEditingCita(null);
      setFormData({
        idMascota: '',
        idVeterinario: '',
        idServicio: '',
        fechaCita: '',
        horaCita: '',
        motivo: '',
        observaciones: '',
        esEmergencia: false,
      });
      setModalOpen(true);
      setError('');
      setSuccess('');
      return;
    }
    
    // Es una cita existente para editar
    console.log('Abriendo modal para editar cita con ID:', cita.idCita || cita.id);
    setEditingCita(cita);
    
    // Formatear fecha: convertir de formato "dd/MM/yyyy" o "yyyy-MM-dd" a "yyyy-MM-dd"
    let fechaFormateada = cita.fechaCita || '';
    if (fechaFormateada && fechaFormateada.includes('/')) {
      const [dia, mes, anio] = fechaFormateada.split('/');
      fechaFormateada = `${anio}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
    }

    // Formatear hora: convertir de "HH:mm:ss" a "HH:mm" para el input type="time"
    let horaFormateada = cita.horaCita || '';
    if (horaFormateada && horaFormateada.includes(':')) {
      horaFormateada = horaFormateada.substring(0, 5); // Tomar solo HH:mm
    }

    setFormData({
      idMascota: cita.mascota?.idMascota ? String(cita.mascota.idMascota) : '',
      idVeterinario: cita.veterinario?.idPersonal ? String(cita.veterinario.idPersonal) : '',
      idServicio: cita.servicio?.idServicio ? String(cita.servicio.idServicio) : '',
      fechaCita: fechaFormateada,
      horaCita: horaFormateada,
      motivo: cita.motivoConsulta || cita.motivo || '',
      observaciones: cita.observaciones || '',
      esEmergencia: cita.esEmergencia || false,
    });
    
    setModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingCita(null);
    setError('');
    setSuccess('');
  };

  const handleCancelCita = (cita) => {
    setSelectedCita(cita);
    setCancelMotivo('');
    setCancelModalOpen(true);
    setError('');
    setSuccess('');
  };

  const handleConfirmCancel = async () => {
    if (!cancelMotivo || cancelMotivo.trim().length < 5) {
      setError('El motivo de cancelación debe tener al menos 5 caracteres');
      return;
    }

    try {
      setError('');
      await citaService.cancelar(selectedCita.idCita, cancelMotivo.trim(), user?.username || 'Propietario');
      setSuccess('Cita cancelada exitosamente. Se enviará una notificación por correo electrónico.');
      setCancelModalOpen(false);
      setCancelMotivo('');
      await loadData();
    } catch (error) {
      console.error('Error al cancelar cita:', error);
      setError(error.response?.data?.message || 'Error al cancelar la cita');
    }
  };

  const handleViewCita = (cita) => {
    setSelectedCita(cita);
    setViewModalOpen(true);
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      console.log('FormData antes de validar:', formData);
      
      // Validaciones
      if (!formData.idMascota || formData.idMascota === '' || formData.idMascota === '0') {
        setError('Debe seleccionar una mascota');
        return;
      }

      if (!formData.idVeterinario || formData.idVeterinario === '' || formData.idVeterinario === '0') {
        setError('Debe seleccionar un veterinario');
        return;
      }

      if (!formData.idServicio || formData.idServicio === '' || formData.idServicio === '0') {
        setError('Debe seleccionar un servicio');
        return;
      }

      if (!formData.fechaCita) {
        setError('La fecha es obligatoria');
        return;
      }

      // Validar que la fecha no sea en el pasado
      const fechaSeleccionada = new Date(formData.fechaCita);
      const hoy = new Date();
      hoy.setHours(0, 0, 0, 0);
      fechaSeleccionada.setHours(0, 0, 0, 0);
      
      if (fechaSeleccionada < hoy) {
        setError('La fecha de la cita no puede ser en el pasado');
        return;
      }

      if (!formData.horaCita) {
        setError('La hora es obligatoria');
        return;
      }

      if (!formData.motivo || formData.motivo.trim().length < 5) {
        setError('El motivo de consulta debe tener al menos 5 caracteres');
        return;
      }

      // Formatear hora: convertir "HH:mm" a "HH:mm:00" si es necesario
      let horaFormateada = formData.horaCita;
      if (horaFormateada && horaFormateada.length === 5) {
        horaFormateada = horaFormateada + ':00';
      }

      // Validar que los IDs sean números válidos
      const idMascota = parseInt(formData.idMascota, 10);
      const idVeterinario = parseInt(formData.idVeterinario, 10);
      const idServicio = parseInt(formData.idServicio, 10);

      if (isNaN(idMascota) || idMascota <= 0) {
        setError('El ID de la mascota no es válido');
        return;
      }

      if (isNaN(idVeterinario) || idVeterinario <= 0) {
        setError('El ID del veterinario no es válido');
        return;
      }

      if (isNaN(idServicio) || idServicio <= 0) {
        setError('El ID del servicio no es válido');
        return;
      }

      // Validar que la mascota seleccionada pertenezca al propietario actual
      if (user?.rol === 'PROPIETARIO') {
        const mascotaSeleccionada = mascotas.find(m => m.idMascota === idMascota);
        if (!mascotaSeleccionada) {
          console.error('La mascota seleccionada no pertenece al propietario actual', {
            idMascota,
            mascotasDisponibles: mascotas.map(m => m.idMascota)
          });
          setError('La mascota seleccionada no pertenece a tu cuenta. Por favor, selecciona una de tus mascotas.');
          return;
        }
      }

      // Preparar datos según el formato que espera el backend
      const data = {
        idMascota: Number(idMascota),
        idVeterinario: Number(idVeterinario),
        idServicio: Number(idServicio),
        fechaCita: formData.fechaCita,
        horaCita: horaFormateada,
        motivo: formData.motivo.trim(),
        esEmergencia: formData.esEmergencia || false,
      };

      // Solo incluir observaciones si tiene valor
      if (formData.observaciones && formData.observaciones.trim()) {
        data.observaciones = formData.observaciones.trim();
      }

      console.log('Enviando datos al backend:', JSON.stringify(data, null, 2));

      if (editingCita) {
        // Validar que el ID de la cita esté presente
        const citaId = editingCita.idCita || editingCita.id;
        if (!citaId) {
          console.error('Error: No se encontró el ID de la cita a actualizar', editingCita);
          setError('Error: No se pudo identificar la cita a actualizar. Por favor, recarga la página e intenta de nuevo.');
          return;
        }
        
        // Actualizar cita existente
        console.log('Actualizando cita con ID:', citaId);
        const response = await citaService.update(citaId, data);
        console.log('Respuesta del backend (update):', response.data);
        setSuccess('Cita actualizada exitosamente.');
      } else {
        // Crear nueva cita
        const response = await citaService.create(data);
        console.log('Respuesta del backend (create):', response.data);
        setSuccess('Cita creada exitosamente. Recibirás una confirmación por correo electrónico.');
      }

      await loadData();
      setTimeout(() => {
        handleCloseModal();
      }, 1500);
    } catch (error) {
      console.error('Error al guardar cita:', error);
      console.error('Error completo:', JSON.stringify(error.response?.data, null, 2));
      console.error('Status del error:', error.response?.status);
      console.error('Headers del error:', error.response?.headers);
      
      let errorMessage = editingCita ? 'Error al actualizar la cita' : 'Error al crear la cita';
      
      if (error.response) {
        const status = error.response.status;
        const errorData = error.response.data;
        
        // Errores de autenticación/autorización
        if (status === 401) {
          errorMessage = 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.';
        } else if (status === 403) {
          errorMessage = 'No tienes permisos para realizar esta acción.';
        } else if (status === 404) {
          errorMessage = errorData?.message || 'No se encontró el recurso solicitado.';
        } else if (status === 400 || status === 422) {
          // Errores de validación
          if (errorData.validationErrors && typeof errorData.validationErrors === 'object') {
            const validationMessages = Object.entries(errorData.validationErrors)
              .map(([field, msg]) => `${field}: ${msg}`)
              .join(', ');
            errorMessage = errorData.message || 'Error de validación';
            if (validationMessages) {
              errorMessage += ' - ' + validationMessages;
            }
          }
          // Si hay errors (Lista de strings)
          else if (Array.isArray(errorData.errors) && errorData.errors.length > 0) {
            errorMessage = errorData.message || 'Error de validación';
            errorMessage += ' - ' + errorData.errors.join(', ');
          }
          // Mensaje directo
          else if (errorData.message) {
            errorMessage = errorData.message;
          } else if (errorData.error) {
            errorMessage = errorData.error;
          }
        } else if (status >= 500) {
          errorMessage = 'Error interno del servidor. Por favor, intenta de nuevo más tarde.';
        } else if (errorData?.message) {
          errorMessage = errorData.message;
        } else if (errorData?.error) {
          errorMessage = errorData.error;
        }
      } else if (error.message) {
        errorMessage = error.message;
      } else if (typeof error === 'string') {
        errorMessage = error;
      }
      
      setError(errorMessage);
    }
  };

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'CONFIRMADA':
        return 'bg-green-100 text-green-800';
      case 'ATENDIDA':
        return 'bg-gray-100 text-gray-800';
      case 'CANCELADA':
        return 'bg-red-100 text-red-800';
      case 'NO_ASISTIO':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-blue-100 text-blue-800';
    }
  };

  const filteredCitas = citas.filter((cita) =>
    cita.mascota?.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cita.veterinario?.nombreCompleto?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cita.servicio?.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cita.motivo?.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h2 className="text-2xl font-bold text-gray-900">Mis Citas</h2>
        <button
          onClick={handleOpenModal}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 flex items-center transition-smooth animate-scaleIn"
        >
          <Plus className="w-5 h-5 mr-2" />
          Nueva Cita
        </button>
      </div>

      {error && (
        <div className={`border px-4 py-3 rounded-lg animate-slideDown ${
          error.includes('No se encontró un registro de propietario') 
            ? 'bg-yellow-50 border-yellow-200 text-yellow-800' 
            : 'bg-red-50 border-red-200 text-red-700'
        }`}>
          <div className="flex items-start">
            <div className="flex-1">
              <p className="font-medium">{error.includes('No se encontró un registro de propietario') ? '⚠️ Información Requerida' : 'Error'}</p>
              <p className="text-sm mt-1">{error}</p>
            </div>
            {error.includes('No se encontró un registro de propietario') && (
              <button
                onClick={() => setError('')}
                className="ml-4 text-yellow-600 hover:text-yellow-800"
              >
                ✕
              </button>
            )}
          </div>
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
              placeholder="Buscar citas..."
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Veterinario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Servicio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredCitas.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    {citas.length === 0 ? 'No tienes citas programadas' : 'No se encontraron citas'}
                  </td>
                </tr>
              ) : (
                filteredCitas.map((cita) => (
                  <tr key={cita.idCita} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{cita.mascota?.nombre}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.veterinario?.nombreCompleto || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.servicio?.nombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.fechaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.horaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(cita.estado)}`}>
                        {cita.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleViewCita(cita)}
                          className="text-primary-600 hover:text-primary-900 transition-colors"
                          title="Ver detalles"
                        >
                          <Eye className="w-5 h-5" />
                        </button>
                        {(cita.estado === 'PROGRAMADA' || cita.estado === 'CONFIRMADA') && (
                          <>
                            <button
                              onClick={() => handleOpenModal(cita)}
                              className="text-blue-600 hover:text-blue-900 transition-colors"
                              title="Editar cita"
                            >
                              <Edit className="w-5 h-5" />
                            </button>
                            <button
                              onClick={() => handleCancelCita(cita)}
                              className="text-red-600 hover:text-red-900 transition-colors"
                              title="Cancelar cita"
                            >
                              <XCircle className="w-5 h-5" />
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal para crear/editar cita */}
      <Modal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        title={editingCita ? "Editar Cita" : "Nueva Cita"}
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
                name="idMascota"
                value={formData.idMascota}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                disabled={mascotas.length === 0}
              >
                <option value="">
                  {mascotas.length === 0 ? 'No hay mascotas disponibles' : 'Seleccione una mascota'}
                </option>
                {mascotas && mascotas.map((m) => (
                  <option key={m.idMascota} value={String(m.idMascota)}>
                    {m.nombre} - {m.especie?.nombre || 'N/A'}
                  </option>
                ))}
              </select>
              {mascotas.length === 0 && (
                <div className="mt-2 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <p className="text-xs text-yellow-800">
                    <strong>No tienes mascotas registradas.</strong> Por favor, contacta al administrador para que registre tus mascotas en el sistema.
                  </p>
                </div>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Veterinario *
              </label>
              <select
                required
                name="idVeterinario"
                value={formData.idVeterinario}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione un veterinario</option>
                {veterinarios && veterinarios.map((v) => {
                  const id = v.idPersonal || v.idVeterinario;
                  return (
                    <option key={id} value={String(id)}>
                      {v.nombreCompleto || `${v.nombres || ''} ${v.apellidos || ''}`.trim()}
                    </option>
                  );
                })}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Servicio *
              </label>
              <select
                required
                name="idServicio"
                value={formData.idServicio}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="">Seleccione un servicio</option>
                {servicios && servicios.map((s) => (
                  <option key={s.idServicio} value={String(s.idServicio)}>
                    {s.nombre} - ${s.precio}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Fecha *
              </label>
              <input
                type="date"
                required
                value={formData.fechaCita}
                onChange={(e) => setFormData({ ...formData, fechaCita: e.target.value })}
                min={new Date().toISOString().split('T')[0]}
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

            <div className="flex items-center">
              <input
                type="checkbox"
                id="esEmergencia"
                name="esEmergencia"
                checked={formData.esEmergencia}
                onChange={handleChange}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label htmlFor="esEmergencia" className="ml-2 block text-sm text-gray-700">
                Es una emergencia
              </label>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo de Consulta *
            </label>
            <textarea
              required
              value={formData.motivo}
              onChange={handleChange}
              name="motivo"
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Describa el motivo de la consulta..."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Observaciones
            </label>
            <textarea
              value={formData.observaciones}
              onChange={handleChange}
              name="observaciones"
              rows="2"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Observaciones adicionales (opcional)..."
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
              {editingCita ? 'Actualizar Cita' : 'Crear Cita'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal para ver detalles de la cita */}
      <Modal
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
        title="Detalles de la Cita"
        size="md"
      >
        {selectedCita && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Mascota</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.mascota?.nombre}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Veterinario</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.veterinario?.nombreCompleto}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Servicio</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.servicio?.nombre}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Estado</label>
                <span className={`mt-1 inline-block px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(selectedCita.estado)}`}>
                  {selectedCita.estado}
                </span>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Fecha</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.fechaCita}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Hora</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.horaCita}</p>
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Motivo</label>
              <p className="mt-1 text-sm text-gray-900">{selectedCita.motivo || 'No especificado'}</p>
            </div>
            {selectedCita.observaciones && (
              <div>
                <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                <p className="mt-1 text-sm text-gray-900">{selectedCita.observaciones}</p>
              </div>
            )}
            {selectedCita.esEmergencia && (
              <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-2 rounded-lg">
                ⚠️ Esta es una cita de emergencia
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Modal para cancelar cita */}
      <Modal
        isOpen={cancelModalOpen}
        onClose={() => {
          setCancelModalOpen(false);
          setCancelMotivo('');
          setError('');
        }}
        title="Cancelar Cita"
        size="md"
      >
        <div className="space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          {selectedCita && (
            <>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-600 mb-2">Mascota:</p>
                <p className="font-medium">{selectedCita.mascota?.nombre}</p>
                <p className="text-sm text-gray-600 mt-2 mb-2">Fecha:</p>
                <p className="font-medium">{selectedCita.fechaCita} a las {selectedCita.horaCita}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Motivo de Cancelación *
                </label>
                <textarea
                  required
                  value={cancelMotivo}
                  onChange={(e) => setCancelMotivo(e.target.value)}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="Por favor, indique el motivo de la cancelación (mínimo 5 caracteres)..."
                />
                <p className="text-xs text-gray-500 mt-1">Mínimo 5 caracteres</p>
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t">
                <button
                  type="button"
                  onClick={() => {
                    setCancelModalOpen(false);
                    setCancelMotivo('');
                    setError('');
                  }}
                  className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  onClick={handleConfirmCancel}
                  className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                  Confirmar Cancelación
                </button>
              </div>
            </>
          )}
        </div>
      </Modal>
    </div>
  );
}
