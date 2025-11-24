import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import RecepcionistaDashboard from '../pages/recepcionista/Dashboard';
import CitasPage from '../pages/recepcionista/Citas';
import PropietariosPage from '../pages/recepcionista/Propietarios';
import MascotasPage from '../pages/recepcionista/Mascotas';
import { LayoutDashboard, Calendar, Users, User } from 'lucide-react';

const menuItems = [
  { path: '/recepcionista/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/recepcionista/citas', label: 'Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/recepcionista/propietarios', label: 'Propietarios', icon: <Users className="w-5 h-5" /> },
  { path: '/recepcionista/mascotas', label: 'Mascotas', icon: <User className="w-5 h-5" /> },
];

export default function RecepcionistaLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Recepcionista">
      <Routes>
        <Route path="dashboard" element={<RecepcionistaDashboard />} />
        <Route path="citas" element={<CitasPage />} />
        <Route path="propietarios" element={<PropietariosPage />} />
        <Route path="mascotas" element={<MascotasPage />} />
      </Routes>
    </Layout>
  );
}

