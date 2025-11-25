import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import PropietarioDashboard from '../pages/propietario/Dashboard';
import MisMascotasPage from '../pages/propietario/MisMascotas';
import MisCitasPage from '../pages/propietario/MisCitas';
import AgendarCitaPage from '../pages/propietario/AgendarCita';
import VacunacionesPage from '../pages/propietario/Vacunaciones';
import PerfilPage from '../pages/propietario/Perfil';
import HistoriasClinicasPage from '../pages/propietario/HistoriasClinicas';
import { LayoutDashboard, User, Calendar, Syringe, UserCircle, FileText, CalendarPlus } from 'lucide-react';

const menuItems = [
  { path: '/propietario/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/propietario/mascotas', label: 'Mis Mascotas', icon: <User className="w-5 h-5" /> },
  { path: '/propietario/citas', label: 'Mis Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/propietario/agendar-cita', label: 'Agendar Cita', icon: <CalendarPlus className="w-5 h-5" /> },
  { path: '/propietario/historias', label: 'Historias Clínicas', icon: <FileText className="w-5 h-5" /> },
  { path: '/propietario/vacunaciones', label: 'Vacunaciones', icon: <Syringe className="w-5 h-5" /> },
  { path: '/propietario/perfil', label: 'Mi Perfil', icon: <UserCircle className="w-5 h-5" /> },
];

export default function PropietarioLayout() {
  return (
    <Layout menuItems={menuItems} title="Portal del Propietario">
      <Routes>
        <Route path="dashboard" element={<PropietarioDashboard />} />
        <Route path="mascotas" element={<MisMascotasPage />} />
        <Route path="citas" element={<MisCitasPage />} />
        <Route path="agendar-cita" element={<AgendarCitaPage />} />
        <Route path="historias" element={<HistoriasClinicasPage />} />
        <Route path="vacunaciones" element={<VacunacionesPage />} />
        <Route path="perfil" element={<PerfilPage />} />
      </Routes>
    </Layout>
  );
}

