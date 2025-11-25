import { useEffect, useState } from 'react';
import { inventarioService } from '../../services/api';
import { AlertTriangle } from 'lucide-react';

export default function InventarioPage() {
  const [inventario, setInventario] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadInventario();
  }, []);

  const loadInventario = async () => {
    try {
      const response = await inventarioService.getAll();
      setInventario(response.data);
    } catch (error) {
      console.error('Error al cargar inventario:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando inventario...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Inventario</h2>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Insumo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Actual</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Mínimo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {inventario.map((item) => {
                const nombreInsumo = item.insumo?.nombre || item.nombre || 'Insumo sin nombre';
                const cantidadActual = item.cantidadActual || 0;
                const stockMinimo = item.stockMinimo || item.insumo?.stockMinimo || 0;
                const esStockBajo = cantidadActual > 0 && cantidadActual <= stockMinimo;
                
                return (
                  <tr key={item.idInventario} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {nombreInsumo}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cantidadActual}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{stockMinimo}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {cantidadActual === 0 ? (
                        <span className="px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 flex items-center">
                          <AlertTriangle className="w-4 h-4 mr-1" />
                          Agotado
                        </span>
                      ) : esStockBajo ? (
                        <span className="px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 flex items-center">
                          <AlertTriangle className="w-4 h-4 mr-1" />
                          Stock Bajo
                        </span>
                      ) : (
                        <span className="px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                          Normal
                        </span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

