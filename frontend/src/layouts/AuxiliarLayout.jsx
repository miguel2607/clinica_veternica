import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import AuxiliarDashboard from '../pages/auxiliar/Dashboard';
import InventarioPage from '../pages/auxiliar/Inventario';
import InsumosPage from '../pages/auxiliar/Insumos';
import TiposInsumoPage from '../pages/auxiliar/TiposInsumo';
import HistoriasClinicasPage from '../pages/auxiliar/HistoriasClinicas';
import VacunacionesPage from '../pages/auxiliar/Vacunaciones';
import { LayoutDashboard, Package, Box, Layers, FileText, Syringe } from 'lucide-react';

const menuItems = [
  { path: '/auxiliar/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/auxiliar/historias', label: 'Historias Clínicas', icon: <FileText className="w-5 h-5" /> },
  { path: '/auxiliar/vacunaciones', label: 'Vacunaciones', icon: <Syringe className="w-5 h-5" /> },
  { path: '/auxiliar/inventario', label: 'Inventario', icon: <Package className="w-5 h-5" /> },
  { path: '/auxiliar/insumos', label: 'Gestión de Insumos', icon: <Box className="w-5 h-5" /> },
  { path: '/auxiliar/tipos-insumo', label: 'Tipos de Insumo', icon: <Layers className="w-5 h-5" /> },
];

export default function AuxiliarLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Auxiliar">
      <Routes>
        <Route path="dashboard" element={<AuxiliarDashboard />} />
        <Route path="historias" element={<HistoriasClinicasPage />} />
        <Route path="vacunaciones" element={<VacunacionesPage />} />
        <Route path="inventario" element={<InventarioPage />} />
        <Route path="insumos" element={<InsumosPage />} />
        <Route path="tipos-insumo" element={<TiposInsumoPage />} />
      </Routes>
    </Layout>
  );
}

