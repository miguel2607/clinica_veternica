import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import PropietarioDashboard from '../pages/propietario/Dashboard';
import MisMascotasPage from '../pages/propietario/MisMascotas';
import MisCitasPage from '../pages/propietario/MisCitas';
import { LayoutDashboard, User, Calendar } from 'lucide-react';

const menuItems = [
  { path: '/propietario/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/propietario/mascotas', label: 'Mis Mascotas', icon: <User className="w-5 h-5" /> },
  { path: '/propietario/citas', label: 'Mis Citas', icon: <Calendar className="w-5 h-5" /> },
];

export default function PropietarioLayout() {
  return (
    <Layout menuItems={menuItems} title="Portal del Propietario">
      <Routes>
        <Route path="dashboard" element={<PropietarioDashboard />} />
        <Route path="mascotas" element={<MisMascotasPage />} />
        <Route path="citas" element={<MisCitasPage />} />
      </Routes>
    </Layout>
  );
}

