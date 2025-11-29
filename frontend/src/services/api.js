import axios from 'axios';

// Usar variable de entorno si está disponible, sino usar localhost
// En Docker, usar /api ya que Nginx hace proxy al backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 
  (window.location.hostname === 'localhost' ? 'http://localhost:8080/api' : '/api');

// Crear instancia de axios
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token a las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers.Authorization = `Bearer ${token}`;
      // Log solo para peticiones importantes (evitar spam)
      if (config.url?.includes('/mi-perfil') || config.url?.includes('/dashboard')) {
        console.log('📤 Petición con token:', config.url);
      }
    } else {
      console.warn('⚠️ Petición sin token:', config.url);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Variable para evitar múltiples redirecciones
let isRedirecting = false;

// Interceptor para manejar errores de respuesta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const requestUrl = error.config?.url || '';
      const currentPath = window.location.pathname;
      const token = localStorage.getItem('token');
      
      console.error('❌ Error 401 - No autorizado:', error.response?.data);
      console.error('❌ URL de la petición:', requestUrl);
      console.error('❌ Ruta actual:', currentPath);
      console.error('❌ Token actual:', token ? 'Presente' : 'Ausente');
      
      // NO redirigir si es una petición de login (el error ya se maneja en el componente)
      if (requestUrl.includes('/auth/login') || requestUrl.includes('/auth/register')) {
        console.log('⚠️ Error 401 en login/register, no redirigiendo (se maneja en el componente)');
        return Promise.reject(error);
      }
      
      // Solo redirigir si:
      // 1. No estamos ya en login/register
      // 2. No hay token válido (o el token es inválido)
      // 3. No estamos ya redirigiendo
      if (!currentPath.includes('/login') && !currentPath.includes('/register') && !isRedirecting) {
        // Verificar si el token existe y es válido
        if (!token || token === 'undefined' || token === 'null') {
          console.log('⚠️ No hay token válido, limpiando y redirigiendo');
          isRedirecting = true;
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          
          setTimeout(() => {
            isRedirecting = false;
            if (!window.location.pathname.includes('/login')) {
              window.location.href = '/login';
            }
          }, 100);
        } else {
          // Hay token pero la petición falló - podría ser:
          // 1. Token expirado
          // 2. Problema de permisos
          // 3. Token inválido
          console.warn('⚠️ Token presente pero petición falló con 401.');
          console.warn('⚠️ Esto podría indicar que el token expiró o es inválido.');
          
          // Verificar si el mensaje del error indica token expirado
          const errorMessage = error.response?.data?.message || '';
          if (errorMessage.toLowerCase().includes('token') || 
              errorMessage.toLowerCase().includes('expired') ||
              errorMessage.toLowerCase().includes('invalid')) {
            console.log('⚠️ Token parece estar expirado o inválido, limpiando y redirigiendo');
            isRedirecting = true;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            
            setTimeout(() => {
              isRedirecting = false;
              if (!window.location.pathname.includes('/login')) {
                window.location.href = '/login';
              }
            }, 100);
          } else {
            // No redirigir, dejar que el componente maneje el error
            console.log('⚠️ Dejando que el componente maneje el error 401');
          }
        }
      }
    }
    return Promise.reject(error);
  }
);

// Servicios de API
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
  resetPasswordByUsername: (data) => api.post('/auth/reset-password', data),
  registerPropietario: (data) => api.post('/auth/register-propietario', data),
};

export const usuarioService = {
  getAll: () => api.get('/usuarios'),
  getById: (id) => api.get(`/usuarios/${id}`),
  create: (data) => api.post('/usuarios', data),
  update: (id, data) => api.put(`/usuarios/${id}`, data),
  delete: (id) => api.delete(`/usuarios/${id}`),
  cambiarPassword: (id, data) => api.patch(`/usuarios/${id}/cambiar-password`, data),
};

export const mascotaService = {
  getAll: () => api.get('/mascotas'),
  getById: (id) => api.get(`/mascotas/${id}`),
  create: (data) => api.post('/mascotas', data),
  update: (id, data) => api.put(`/mascotas/${id}`, data),
  delete: (id) => api.delete(`/mascotas/${id}`),
  getByPropietario: (id) => api.get(`/mascotas/propietario/${id}`),
};

export const propietarioService = {
  getAll: () => api.get('/propietarios'),
  getActivos: () => api.get('/propietarios/activos'),
  getById: (id) => api.get(`/propietarios/${id}`),
  create: (data) => api.post('/propietarios', data),
  update: (id, data) => api.put(`/propietarios/${id}`, data),
  delete: (id) => api.delete(`/propietarios/${id}`),
  getByDocumento: (tipoDocumento, numeroDocumento) => api.get(`/propietarios/documento?tipoDocumento=${tipoDocumento}&numeroDocumento=${numeroDocumento}`),
  getByEmail: (email) => api.get(`/propietarios/email?email=${email}`),
  getByTelefono: (telefono) => api.get(`/propietarios/telefono?telefono=${telefono}`),
  buscarPorNombre: (nombre) => api.get(`/propietarios/buscar?nombre=${nombre}`),
  activar: (id) => api.patch(`/propietarios/${id}/activar`),
  obtenerOCrearMiPerfil: () => api.get('/propietarios/mi-perfil'), // Nuevo endpoint para propietarios autenticados
};

export const citaService = {
  getAll: () => api.get('/citas'),
  getById: (id) => api.get(`/citas/${id}`),
  create: (data) => api.post('/citas', data),
  update: (id, data) => api.put(`/citas/${id}`, data),
  delete: (id) => api.delete(`/citas/${id}`),
  getByVeterinario: (id) => api.get(`/citas/veterinario/${id}`),
  getMisCitas: () => api.get('/citas/mis-citas'),
  getByMascota: (id) => api.get(`/citas/mascota/${id}`),
  getProgramadas: () => api.get('/citas/programadas'),
  confirmar: (id) => api.put(`/citas/${id}/confirmar`),
  cancelar: (id, motivo, usuario = 'Sistema') => api.put(`/citas/${id}/cancelar?motivo=${encodeURIComponent(motivo)}&usuario=${encodeURIComponent(usuario)}`),
  atender: (id) => api.put(`/citas/${id}/atender`),
  iniciarAtencion: (id) => api.put(`/citas/${id}/iniciar-atencion`),
  finalizarAtencion: (id) => api.put(`/citas/${id}/finalizar-atencion`),
};

export const veterinarioService = {
  getAll: () => api.get('/veterinarios'),
  getActivos: () => api.get('/veterinarios/activos'),
  getDisponibles: () => api.get('/veterinarios/disponibles'),
  getById: (id) => api.get(`/veterinarios/${id}`),
  getByRegistro: (registro) => api.get(`/veterinarios/registro/${registro}`),
  create: (data) => api.post('/veterinarios', data),
  update: (id, data) => api.put(`/veterinarios/${id}`, data),
  delete: (id) => api.delete(`/veterinarios/${id}`),
  getByEspecialidad: (especialidad) => api.get(`/veterinarios/especialidad?especialidad=${especialidad}`),
  buscarPorNombre: (nombre) => api.get(`/veterinarios/buscar?nombre=${nombre}`),
  activar: (id) => api.patch(`/veterinarios/${id}/activar`),
  obtenerMiPerfil: () => api.get('/veterinarios/mi-perfil'), // Endpoint para veterinarios autenticados
};

export const servicioService = {
  getAll: () => api.get('/servicios'),
  getById: (id) => api.get(`/servicios/${id}`),
  create: (data) => api.post('/servicios', data),
  update: (id, data) => api.put(`/servicios/${id}`, data),
  delete: (id) => api.delete(`/servicios/${id}`),
  getActivos: () => api.get('/servicios/activos'),
  getByTipo: (tipo) => api.get(`/servicios/tipo/${tipo}`),
  getByCategoria: (categoria) => api.get(`/servicios/categoria/${categoria}`),
  activar: (id) => api.put(`/servicios/${id}/activar`),
  desactivar: (id) => api.put(`/servicios/${id}/desactivar`),
};

export const inventarioService = {
  getAll: () => api.get('/inventario'),
  getStockBajo: () => api.get('/inventario/stock-bajo'),
  getAgotados: () => api.get('/inventario/agotados'),
};

export const insumoService = {
  getAll: () => api.get('/inventario/insumos'),
  getActivos: () => api.get('/inventario/insumos/activos'),
  getById: (id) => api.get(`/inventario/insumos/${id}`),
  getByCodigo: (codigo) => api.get(`/inventario/insumos/codigo/${codigo}`),
  create: (data) => api.post('/inventario/insumos', data),
  update: (id, data) => api.put(`/inventario/insumos/${id}`, data),
  delete: (id) => api.delete(`/inventario/insumos/${id}`),
  getStockBajo: () => api.get('/inventario/insumos/stock-bajo'),
  getAgotados: () => api.get('/inventario/insumos/agotados'),
  getByTipo: (idTipo) => api.get(`/inventario/insumos/tipo/${idTipo}`),
  buscarPorNombre: (nombre) => api.get(`/inventario/insumos/buscar?nombre=${nombre}`),
  activar: (id) => api.patch(`/inventario/insumos/${id}/activar`),
  desactivar: (id) => api.patch(`/inventario/insumos/${id}/desactivar`),
};

export const historiaClinicaService = {
  getAll: () => api.get('/historias-clinicas'),
  getActivas: () => api.get('/historias-clinicas/activas'),
  getById: (id) => api.get(`/historias-clinicas/${id}`),
  getByMascota: (id) => api.get(`/historias-clinicas/mascota/${id}`),
  create: (data) => api.post('/historias-clinicas', data),
  createConBuilder: (idMascota, data) => api.post(`/historias-clinicas/builder/${idMascota}`, data),
  update: (id, data) => api.put(`/historias-clinicas/${id}`, data),
  archivar: (id, motivo) => api.put(`/historias-clinicas/${id}/archivar?motivo=${encodeURIComponent(motivo)}`),
  reactivar: (id) => api.put(`/historias-clinicas/${id}/reactivar`),
  guardarMemento: (id) => api.post(`/historias-clinicas/${id}/memento`),
  restaurarUltimoMemento: (id) => api.put(`/historias-clinicas/${id}/restaurar-ultimo`),
  restaurarMemento: (id, indice) => api.put(`/historias-clinicas/${id}/restaurar/${indice}`),
  obtenerCantidadMementos: (id) => api.get(`/historias-clinicas/${id}/mementos/cantidad`),
};

export const evolucionClinicaService = {
  create: (idHistoriaClinica, data) => api.post(`/evoluciones-clinicas?idHistoriaClinica=${idHistoriaClinica}`, data),
  getByHistoriaClinica: (idHistoriaClinica) => api.get(`/evoluciones-clinicas/historia-clinica/${idHistoriaClinica}`),
};

export const vacunacionService = {
  getAll: () => api.get('/vacunaciones'),
  create: (idHistoriaClinica, data) => api.post(`/vacunaciones?idHistoriaClinica=${idHistoriaClinica}`, data),
  getByHistoriaClinica: (idHistoriaClinica) => api.get(`/vacunaciones/historia-clinica/${idHistoriaClinica}`),
};

export const notificacionService = {
  getAll: () => api.get('/notificaciones'),
  getById: (id) => api.get(`/notificaciones/${id}`),
  create: (data) => api.post('/notificaciones', data),
  getByUsuario: (id) => api.get(`/notificaciones/usuario/${id}`),
  getByCanal: (canal) => api.get(`/notificaciones/canal/${canal}`),
  getEnviadas: () => api.get('/notificaciones/enviadas'),
  getPendientes: () => api.get('/notificaciones/pendientes'),
};

// Facade services
export const dashboardService = {
  getDashboard: () => api.get('/facade/dashboard'),
};

export const citaFacadeService = {
  crearConNotificacion: (data) => api.post('/facade/citas/crear-con-notificacion', data),
  getCalendario: (fecha) => api.get(`/facade/citas/calendario?fecha=${fecha}`),
};

export const mascotaFacadeService = {
  registroCompleto: (data) => api.post('/facade/mascotas/registro-completo', data),
  getCompleta: (id) => api.get(`/facade/mascotas/${id}/completa`),
};

export const especieService = {
  getAll: () => api.get('/especies'),
  getActivas: () => api.get('/especies/activas'),
  getById: (id) => api.get(`/especies/${id}`),
  create: (data) => api.post('/especies', data),
  update: (id, data) => api.put(`/especies/${id}`, data),
  delete: (id) => api.delete(`/especies/${id}`),
  buscarPorNombre: (nombre) => api.get(`/especies/buscar?nombre=${nombre}`),
  existePorNombre: (nombre) => api.get(`/especies/existe?nombre=${nombre}`),
  activar: (id) => api.patch(`/especies/${id}/activar`),
};

export const razaService = {
  getAll: () => api.get('/razas'),
  getActivas: () => api.get('/razas/activas'),
  getById: (id) => api.get(`/razas/${id}`),
  create: (data) => api.post('/razas', data),
  update: (id, data) => api.put(`/razas/${id}`, data),
  delete: (id) => api.delete(`/razas/${id}`),
  getByEspecie: (idEspecie) => api.get(`/razas/especie/${idEspecie}`),
  buscarPorNombre: (nombre) => api.get(`/razas/buscar?nombre=${nombre}`),
  activar: (id) => api.patch(`/razas/${id}/activar`),
};

export const horarioService = {
  getAll: () => api.get('/horarios'),
  getActivos: () => api.get('/horarios/activos'),
  getById: (id) => api.get(`/horarios/${id}`),
  getByVeterinario: (idVeterinario) => api.get(`/horarios/veterinario/${idVeterinario}`),
  getByDia: (diaSemana) => api.get(`/horarios/dia/${diaSemana}`),
  getDisponibilidad: (idVeterinario, fecha) => api.get(`/horarios/veterinario/${idVeterinario}/disponibilidad`, {
    params: { fecha }
  }),
  create: (data) => api.post('/horarios', data),
  update: (id, data) => api.put(`/horarios/${id}`, data),
  delete: (id) => api.delete(`/horarios/${id}`),
  activar: (id) => api.put(`/horarios/${id}/activar`),
  desactivar: (id) => api.put(`/horarios/${id}/desactivar`),
};

export const tipoInsumoService = {
  getAll: () => api.get('/inventario/tipos-insumo'),
  getActivos: () => api.get('/inventario/tipos-insumo/activos'),
  getById: (id) => api.get(`/inventario/tipos-insumo/${id}`),
  create: (data) => api.post('/inventario/tipos-insumo', data),
  update: (id, data) => api.put(`/inventario/tipos-insumo/${id}`, data),
  delete: (id) => api.delete(`/inventario/tipos-insumo/${id}`),
};

export const reporteService = {
  getReporteCitas: (fechaInicio, fechaFin) => api.get(`/facade/reportes/citas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`),
  getReporteInventario: () => api.get('/facade/reportes/inventario'),
  getReporteVeterinarios: (fechaInicio, fechaFin) => api.get(`/facade/reportes/veterinarios?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`),
};

export default api;

