import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import RecepcionistaDashboard from '../pages/recepcionista/Dashboard';
import CitasPage from '../pages/recepcionista/Citas';
import AgendarCitaPage from '../pages/recepcionista/AgendarCita';
import PropietariosPage from '../pages/recepcionista/Propietarios';
import MascotasPage from '../pages/recepcionista/Mascotas';
import HorariosPage from '../pages/recepcionista/Horarios';
import ServiciosPage from '../pages/recepcionista/Servicios';
import EspeciesRazasPage from '../pages/recepcionista/EspeciesRazas';
import { LayoutDashboard, Calendar, Users, User, Clock, Scissors, PawPrint, CalendarPlus } from 'lucide-react';

const menuItems = [
  { path: '/recepcionista/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/recepcionista/citas', label: 'Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/recepcionista/agendar-cita', label: 'Agendar Cita', icon: <CalendarPlus className="w-5 h-5" /> },
  { path: '/recepcionista/horarios', label: 'Horarios', icon: <Clock className="w-5 h-5" /> },
  { path: '/recepcionista/servicios', label: 'Servicios', icon: <Scissors className="w-5 h-5" /> },
  { path: '/recepcionista/especies-razas', label: 'Especies y Razas', icon: <PawPrint className="w-5 h-5" /> },
  { path: '/recepcionista/propietarios', label: 'Propietarios', icon: <Users className="w-5 h-5" /> },
  { path: '/recepcionista/mascotas', label: 'Mascotas', icon: <User className="w-5 h-5" /> },
];

export default function RecepcionistaLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Recepcionista">
      <Routes>
        <Route path="dashboard" element={<RecepcionistaDashboard />} />
        <Route path="citas" element={<CitasPage />} />
        <Route path="agendar-cita" element={<AgendarCitaPage />} />
        <Route path="horarios" element={<HorariosPage />} />
        <Route path="servicios" element={<ServiciosPage />} />
        <Route path="especies-razas" element={<EspeciesRazasPage />} />
        <Route path="propietarios" element={<PropietariosPage />} />
        <Route path="mascotas" element={<MascotasPage />} />
      </Routes>
    </Layout>
  );
}

