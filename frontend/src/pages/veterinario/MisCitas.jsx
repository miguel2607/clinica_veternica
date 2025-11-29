import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { citaService } from '../../services/api';

export default function MisCitasPage() {
  const { user } = useAuth();
  const [citas, setCitas] = useState([]);
  const [todasLasCitas, setTodasLasCitas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [mostrarSoloActivas, setMostrarSoloActivas] = useState(true);

  useEffect(() => {
    if (user) {
      loadCitas();
    }
  }, [user]);

  const loadCitas = async () => {
    try {
      setLoading(true);
      setError('');
      
      console.log('🔍 Obteniendo mis citas...');
      console.log('👤 Usuario actual:', user);
      
      // Usar el nuevo endpoint que devuelve solo las citas del veterinario autenticado
      const response = await citaService.getMisCitas();
      console.log('✅ Citas obtenidas:', response.data);
      console.log('📊 Total de citas:', response.data?.length || 0);
      
      // Mostrar todas las citas primero para debug
      const todasLasCitasArray = response.data || [];
      console.log('📋 Todas las citas:', todasLasCitasArray);
      console.log('📊 Estados de las citas:', todasLasCitasArray.map(c => c.estado));
      
      // Guardar todas las citas en el estado para poder filtrarlas después
      setTodasLasCitas(todasLasCitasArray);
      
      // Ordenar por fecha y hora
      todasLasCitasArray.sort((a, b) => {
        const fechaA = new Date(a.fechaCita + ' ' + a.horaCita);
        const fechaB = new Date(b.fechaCita + ' ' + b.horaCita);
        return fechaA - fechaB;
      });
      
      // Filtrar citas según el filtro seleccionado
      let citasFiltradas = todasLasCitasArray;
      if (mostrarSoloActivas) {
        citasFiltradas = todasLasCitasArray.filter(cita => 
          cita.estado === 'PROGRAMADA' || 
          cita.estado === 'CONFIRMADA' || 
          cita.estado === 'EN_ATENCION' || 
          cita.estado === 'EN_ATENCIÓN'
        );
        console.log('✅ Citas activas:', citasFiltradas.length);
      } else {
        console.log('✅ Mostrando todas las citas:', citasFiltradas.length);
      }
      
      setCitas(citasFiltradas);
    } catch (error) {
      console.error('❌ Error al cargar citas:', error);
      console.error('❌ Detalles del error:', error.response?.data || error.message);
      if (error.response?.status === 404) {
        setError('No se encontró un perfil de veterinario asociado a tu usuario. Por favor, contacta al administrador para crear tu perfil de veterinario.');
      } else {
        setError(`Error al cargar las citas: ${error.response?.data?.message || error.message}. Por favor, intenta nuevamente.`);
      }
      setCitas([]);
      setTodasLasCitas([]);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmar = async (idCita) => {
    try {
      await citaService.confirmar(idCita);
      await loadCitas(); // Recargar las citas después de confirmar
    } catch (error) {
      console.error('Error al confirmar cita:', error);
      setError(`Error al confirmar la cita: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleCancelar = async (idCita) => {
    const motivo = prompt('Ingrese el motivo de cancelación:');
    if (!motivo || motivo.trim() === '') {
      return; // El usuario canceló o no ingresó motivo
    }
    try {
      await citaService.cancelar(idCita, motivo.trim(), user?.username || 'Veterinario');
      await loadCitas(); // Recargar las citas después de cancelar
    } catch (error) {
      console.error('Error al cancelar cita:', error);
      setError(`Error al cancelar la cita: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleIniciarAtencion = async (idCita) => {
    try {
      setError(''); // Limpiar errores anteriores
      console.log('🔄 Iniciando atención de cita ID:', idCita);
      const response = await citaService.iniciarAtencion(idCita);
      console.log('✅ Atención iniciada exitosamente:', response.data);
      await loadCitas(); // Recargar las citas después de iniciar atención
    } catch (error) {
      console.error('❌ Error al iniciar atención:', error);
      console.error('❌ Detalles del error:', error.response?.data);
      setError(`Error al iniciar la atención: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleAtender = async (idCita) => {
    try {
      await citaService.atender(idCita);
      await loadCitas(); // Recargar las citas después de atender
    } catch (error) {
      console.error('Error al atender cita:', error);
      setError(`Error al atender la cita: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleFinalizarAtencion = async (idCita) => {
    try {
      await citaService.finalizarAtencion(idCita);
      await loadCitas(); // Recargar las citas después de finalizar atención
    } catch (error) {
      console.error('Error al finalizar atención:', error);
      setError(`Error al finalizar la atención: ${error.response?.data?.message || error.message}`);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando citas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">
          {mostrarSoloActivas ? 'Mis Citas Activas' : 'Todas mis Citas'}
        </h2>
        <div className="flex gap-3">
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={mostrarSoloActivas}
              onChange={(e) => {
                const nuevoValor = e.target.checked;
                setMostrarSoloActivas(nuevoValor);
                // Filtrar las citas ya cargadas sin necesidad de recargar
                if (nuevoValor) {
                  const citasActivas = todasLasCitas.filter(cita => 
                    cita.estado === 'PROGRAMADA' || 
                    cita.estado === 'CONFIRMADA' || 
                    cita.estado === 'EN_ATENCION' || 
                    cita.estado === 'EN_ATENCIÓN'
                  );
                  setCitas(citasActivas);
                } else {
                  setCitas(todasLasCitas);
                }
              }}
              className="rounded"
            />
            <span className="text-sm text-gray-700">Solo activas</span>
          </label>
          <button
            onClick={loadCitas}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
          >
            Actualizar
          </button>
        </div>
      </div>
      
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}
      
      <div className="bg-white rounded-lg shadow overflow-hidden">
        {citas.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Mascota</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Propietario</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Servicio</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {citas.map((cita) => (
                  <tr key={cita.idCita} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {cita.mascota?.nombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.mascota?.propietarioNombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {cita.servicio?.nombre || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.fechaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cita.horaCita}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        cita.estado === 'CONFIRMADA' 
                          ? 'bg-green-100 text-green-800' 
                          : cita.estado === 'PROGRAMADA'
                          ? 'bg-blue-100 text-blue-800'
                          : cita.estado === 'EN_ATENCION' || cita.estado === 'EN_ATENCIÓN'
                          ? 'bg-yellow-100 text-yellow-800'
                          : cita.estado === 'ATENDIDA'
                          ? 'bg-purple-100 text-purple-800'
                          : cita.estado === 'CANCELADA'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}>
                        {cita.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex gap-2">
                        {cita.estado === 'PROGRAMADA' && (
                          <>
                            <button
                              onClick={() => handleConfirmar(cita.idCita)}
                              className="text-green-600 hover:text-green-900 font-medium"
                            >
                              Confirmar
                            </button>
                            <button
                              onClick={() => handleCancelar(cita.idCita)}
                              className="text-red-600 hover:text-red-900 font-medium"
                            >
                              Cancelar
                            </button>
                          </>
                        )}
                        {cita.estado === 'CONFIRMADA' && (
                          <>
                            <button
                              onClick={() => handleIniciarAtencion(cita.idCita)}
                              className="text-blue-600 hover:text-blue-900 font-medium"
                            >
                              Iniciar Atención
                            </button>
                            <button
                              onClick={() => handleAtender(cita.idCita)}
                              className="text-primary-600 hover:text-primary-900 font-medium"
                            >
                              Atender
                            </button>
                          </>
                        )}
                        {(cita.estado === 'EN_ATENCION' || cita.estado === 'EN_ATENCIÓN') && (
                          <button
                            onClick={() => handleFinalizarAtencion(cita.idCita)}
                            className="text-purple-600 hover:text-purple-900 font-medium"
                          >
                            Finalizar Atención
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="p-8 text-center text-gray-500">
            {error ? '' : 'No tienes citas activas en este momento'}
          </div>
        )}
      </div>
    </div>
  );
}

