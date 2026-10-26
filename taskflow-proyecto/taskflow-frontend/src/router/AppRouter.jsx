import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Layout from '../components/Layout'
import ProtectedRoute from '../components/ProtectedRoute'
import DashboardPage from '../pages/DashboardPage'
import LoginPage from '../pages/LoginPage'
import MyTasksPage from '../pages/MyTasksPage'
import ProjectDetailsPage from '../pages/ProjectDetailsPage'

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        {/* Rutas protegidas: comparten el Layout con el Navbar */}
        <Route
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route path="/" element={<DashboardPage />} />
          <Route path="/proyecto/:id" element={<ProjectDetailsPage />} />
          <Route path="/mis-tareas" element={<MyTasksPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
