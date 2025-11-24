import { useEffect, useState } from 'react';
import { inventarioService } from '../../services/api';
import { AlertTriangle, Package } from 'lucide-react';

export default function AuxiliarDashboard() {
  const [stockBajo, setStockBajo] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStockBajo();
  }, []);

  const loadStockBajo = async () => {
    try {
      const response = await inventarioService.getStockBajo();
      setStockBajo(response.data);
    } catch (error) {
      console.error('Error al cargar stock bajo:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Dashboard Auxiliar</h2>

      {stockBajo.length > 0 ? (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
          <div className="flex items-center mb-4">
            <AlertTriangle className="w-6 h-6 text-yellow-600 mr-2" />
            <h3 className="text-xl font-semibold text-yellow-800">Alertas de Stock Bajo</h3>
          </div>
          <div className="space-y-3">
            {stockBajo.map((item) => (
              <div key={item.idInventario} className="bg-white rounded-lg p-4">
                <p className="font-semibold">{item.insumo?.nombre}</p>
                <p className="text-sm text-gray-600">
                  Stock actual: {item.cantidadActual} | Mínimo: {item.insumo?.stockMinimo}
                </p>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="bg-green-50 border border-green-200 rounded-lg p-6">
          <p className="text-green-800">No hay alertas de stock bajo</p>
        </div>
      )}
    </div>
  );
}

