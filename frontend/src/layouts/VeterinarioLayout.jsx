import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import VeterinarioDashboard from '../pages/veterinario/Dashboard';
import MisCitasPage from '../pages/veterinario/MisCitas';
import CalendarioCitasPage from '../pages/veterinario/CalendarioCitas';
import HistoriasClinicasPage from '../pages/veterinario/HistoriasClinicas';
import VacunacionesPage from '../pages/veterinario/Vacunaciones';
import HorariosPage from '../pages/veterinario/Horarios';
import MascotasPage from '../pages/veterinario/Mascotas';
import EvolucionesPage from '../pages/veterinario/Evoluciones';
import PropietariosPage from '../pages/veterinario/Propietarios';
import InventarioPage from '../pages/veterinario/Inventario';
import NotificacionesPage from '../pages/admin/Notificaciones';
import { LayoutDashboard, Calendar, FileText, Bell, Syringe, Clock, PawPrint, Activity, Users, Package, CalendarDays } from 'lucide-react';

const menuItems = [
  { path: '/veterinario/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/veterinario/calendario', label: 'Calendario', icon: <CalendarDays className="w-5 h-5" /> },
  { path: '/veterinario/citas', label: 'Mis Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/veterinario/horarios', label: 'Mis Horarios', icon: <Clock className="w-5 h-5" /> },
  { path: '/veterinario/mascotas', label: 'Mis Pacientes', icon: <PawPrint className="w-5 h-5" /> },
  { path: '/veterinario/propietarios', label: 'Propietarios', icon: <Users className="w-5 h-5" /> },
  { path: '/veterinario/inventario', label: 'Inventario', icon: <Package className="w-5 h-5" /> },
  { path: '/veterinario/historias', label: 'Historias Clínicas', icon: <FileText className="w-5 h-5" /> },
  { path: '/veterinario/evoluciones', label: 'Evoluciones', icon: <Activity className="w-5 h-5" /> },
  { path: '/veterinario/vacunaciones', label: 'Vacunaciones', icon: <Syringe className="w-5 h-5" /> },
  { path: '/veterinario/notificaciones', label: 'Notificaciones', icon: <Bell className="w-5 h-5" /> },
];

export default function VeterinarioLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Veterinario">
      <Routes>
        <Route path="dashboard" element={<VeterinarioDashboard />} />
        <Route path="calendario" element={<CalendarioCitasPage />} />
        <Route path="citas" element={<MisCitasPage />} />
        <Route path="horarios" element={<HorariosPage />} />
        <Route path="mascotas" element={<MascotasPage />} />
        <Route path="propietarios" element={<PropietariosPage />} />
        <Route path="inventario" element={<InventarioPage />} />
        <Route path="historias" element={<HistoriasClinicasPage />} />
        <Route path="evoluciones" element={<EvolucionesPage />} />
        <Route path="vacunaciones" element={<VacunacionesPage />} />
        <Route path="notificaciones" element={<NotificacionesPage />} />
      </Routes>
    </Layout>
  );
}

