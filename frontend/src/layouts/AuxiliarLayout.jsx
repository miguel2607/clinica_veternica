import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import AuxiliarDashboard from '../pages/auxiliar/Dashboard';
import InventarioPage from '../pages/auxiliar/Inventario';
import { LayoutDashboard, Package } from 'lucide-react';

const menuItems = [
  { path: '/auxiliar/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/auxiliar/inventario', label: 'Inventario', icon: <Package className="w-5 h-5" /> },
];

export default function AuxiliarLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Auxiliar">
      <Routes>
        <Route path="dashboard" element={<AuxiliarDashboard />} />
        <Route path="inventario" element={<InventarioPage />} />
      </Routes>
    </Layout>
  );
}

