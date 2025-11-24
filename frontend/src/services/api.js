import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores de respuesta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Servicios de API
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
};

export const usuarioService = {
  getAll: () => api.get('/usuarios'),
  getById: (id) => api.get(`/usuarios/${id}`),
  create: (data) => api.post('/usuarios', data),
  update: (id, data) => api.put(`/usuarios/${id}`, data),
  delete: (id) => api.delete(`/usuarios/${id}`),
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
  create: (data) => api.post('/horarios', data),
  update: (id, data) => api.put(`/horarios/${id}`, data),
  delete: (id) => api.delete(`/horarios/${id}`),
  activar: (id) => api.put(`/horarios/${id}/activar`),
  desactivar: (id) => api.put(`/horarios/${id}/desactivar`),
};

export const tipoInsumoService = {
  getAll: () => api.get('/inventario/tipos-insumo'),
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

