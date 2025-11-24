import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import VeterinarioDashboard from '../pages/veterinario/Dashboard';
import MisCitasPage from '../pages/veterinario/MisCitas';
import HistoriasClinicasPage from '../pages/veterinario/HistoriasClinicas';
import { LayoutDashboard, Calendar, FileText } from 'lucide-react';

const menuItems = [
  { path: '/veterinario/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/veterinario/citas', label: 'Mis Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/veterinario/historias', label: 'Historias Clínicas', icon: <FileText className="w-5 h-5" /> },
];

export default function VeterinarioLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Veterinario">
      <Routes>
        <Route path="dashboard" element={<VeterinarioDashboard />} />
        <Route path="citas" element={<MisCitasPage />} />
        <Route path="historias" element={<HistoriasClinicasPage />} />
      </Routes>
    </Layout>
  );
}

