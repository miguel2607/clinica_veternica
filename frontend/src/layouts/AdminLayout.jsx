import { Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout';
import AdminDashboard from '../pages/admin/Dashboard';
import UsuariosPage from '../pages/admin/Usuarios';
import MascotasPage from '../pages/admin/Mascotas';
import PropietariosPage from '../pages/admin/Propietarios';
import CitasPage from '../pages/admin/Citas';
import AgendarCitaPage from '../pages/admin/AgendarCita';
import InventarioPage from '../pages/admin/Inventario';
import InsumosPage from '../pages/admin/Insumos';
import ReportesPage from '../pages/admin/Reportes';
import NotificacionesPage from '../pages/admin/Notificaciones';
import EspeciesPage from '../pages/admin/Especies';
import RazasPage from '../pages/admin/Razas';
import VeterinariosPage from '../pages/admin/Veterinarios';
import HorariosPage from '../pages/admin/Horarios';
import HistoriasClinicasPage from '../pages/admin/HistoriasClinicas';
import ServiciosPage from '../pages/admin/Servicios';
import TiposInsumoPage from '../pages/admin/TiposInsumo';
import VacunacionesPage from '../pages/admin/Vacunaciones';
import {
  LayoutDashboard,
  Users,
  Calendar,
  Package,
  FileText,
  Bell,
  PawPrint,
  Stethoscope,
  Clock,
  FileCheck,
  Scissors,
  Syringe,
  CalendarPlus
} from 'lucide-react';

const menuItems = [
  { path: '/admin/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
  { path: '/admin/usuarios', label: 'Usuarios', icon: <Users className="w-5 h-5" /> },
  { path: '/admin/propietarios', label: 'Propietarios', icon: <Users className="w-5 h-5" /> },
  { path: '/admin/mascotas', label: 'Mascotas', icon: <PawPrint className="w-5 h-5" /> },
  { path: '/admin/especies', label: 'Especies', icon: <PawPrint className="w-5 h-5" /> },
  { path: '/admin/razas', label: 'Razas', icon: <PawPrint className="w-5 h-5" /> },
  { path: '/admin/veterinarios', label: 'Veterinarios', icon: <Stethoscope className="w-5 h-5" /> },
  { path: '/admin/horarios', label: 'Horarios', icon: <Clock className="w-5 h-5" /> },
  { path: '/admin/servicios', label: 'Servicios', icon: <Scissors className="w-5 h-5" /> },
  { path: '/admin/citas', label: 'Citas', icon: <Calendar className="w-5 h-5" /> },
  { path: '/admin/agendar-cita', label: 'Agendar Cita', icon: <CalendarPlus className="w-5 h-5" /> },
  { path: '/admin/historias-clinicas', label: 'Historias Clínicas', icon: <FileCheck className="w-5 h-5" /> },
  { path: '/admin/vacunaciones', label: 'Vacunaciones', icon: <Syringe className="w-5 h-5" /> },
  { path: '/admin/notificaciones', label: 'Notificaciones', icon: <Bell className="w-5 h-5" /> },
  { path: '/admin/inventario', label: 'Inventario', icon: <Package className="w-5 h-5" /> },
  { path: '/admin/tipos-insumo', label: 'Tipos de Insumo', icon: <Package className="w-5 h-5" /> },
  { path: '/admin/insumos', label: 'Insumos', icon: <Package className="w-5 h-5" /> },
  { path: '/admin/reportes', label: 'Reportes', icon: <FileText className="w-5 h-5" /> },
];

export default function AdminLayout() {
  return (
    <Layout menuItems={menuItems} title="Panel de Administración">
      <Routes>
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="usuarios" element={<UsuariosPage />} />
        <Route path="propietarios" element={<PropietariosPage />} />
        <Route path="mascotas" element={<MascotasPage />} />
        <Route path="especies" element={<EspeciesPage />} />
        <Route path="razas" element={<RazasPage />} />
        <Route path="veterinarios" element={<VeterinariosPage />} />
        <Route path="horarios" element={<HorariosPage />} />
        <Route path="servicios" element={<ServiciosPage />} />
        <Route path="citas" element={<CitasPage />} />
        <Route path="agendar-cita" element={<AgendarCitaPage />} />
        <Route path="historias-clinicas" element={<HistoriasClinicasPage />} />
        <Route path="vacunaciones" element={<VacunacionesPage />} />
        <Route path="notificaciones" element={<NotificacionesPage />} />
        <Route path="inventario" element={<InventarioPage />} />
        <Route path="tipos-insumo" element={<TiposInsumoPage />} />
        <Route path="insumos" element={<InsumosPage />} />
        <Route path="reportes" element={<ReportesPage />} />
      </Routes>
    </Layout>
  );
}

