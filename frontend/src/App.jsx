import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import AdminLayout from './layouts/AdminLayout';
import VeterinarioLayout from './layouts/VeterinarioLayout';
import RecepcionistaLayout from './layouts/RecepcionistaLayout';
import AuxiliarLayout from './layouts/AuxiliarLayout';
import PropietarioLayout from './layouts/PropietarioLayout';
import LoadingSpinner from './components/LoadingSpinner';

// Componente para rutas protegidas
const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { isAuthenticated, hasAnyRole, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !hasAnyRole(allowedRoles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Componente para redirigir según el rol
const RoleRedirect = () => {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  switch (user?.rol) {
    case 'ADMIN':
      return <Navigate to="/admin/dashboard" replace />;
    case 'VETERINARIO':
      return <Navigate to="/veterinario/dashboard" replace />;
    case 'RECEPCIONISTA':
      return <Navigate to="/recepcionista/dashboard" replace />;
    case 'AUXILIAR':
      return <Navigate to="/auxiliar/dashboard" replace />;
    case 'PROPIETARIO':
      return <Navigate to="/propietario/dashboard" replace />;
    default:
      return <Navigate to="/login" replace />;
  }
};

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<RoleRedirect />} />

      {/* Rutas de Administrador */}
      <Route
        path="/admin/*"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AdminLayout />
          </ProtectedRoute>
        }
      />

      {/* Rutas de Veterinario */}
      <Route
        path="/veterinario/*"
        element={
          <ProtectedRoute allowedRoles={['VETERINARIO']}>
            <VeterinarioLayout />
          </ProtectedRoute>
        }
      />

      {/* Rutas de Recepcionista */}
      <Route
        path="/recepcionista/*"
        element={
          <ProtectedRoute allowedRoles={['RECEPCIONISTA']}>
            <RecepcionistaLayout />
          </ProtectedRoute>
        }
      />

      {/* Rutas de Auxiliar */}
      <Route
        path="/auxiliar/*"
        element={
          <ProtectedRoute allowedRoles={['AUXILIAR']}>
            <AuxiliarLayout />
          </ProtectedRoute>
        }
      />

      {/* Rutas de Propietario */}
      <Route
        path="/propietario/*"
        element={
          <ProtectedRoute allowedRoles={['PROPIETARIO']}>
            <PropietarioLayout />
          </ProtectedRoute>
        }
      />

      <Route path="/unauthorized" element={<div className="p-8 text-center">No tienes permisos para acceder a esta página</div>} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;

