import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import NuevoProyectoModal from '../components/NuevoProyectoModal'
import ProyectoCard from '../components/ProyectoCard'
import Spinner from '../components/Spinner'

export default function DashboardPage() {
  const { usuario } = useAuth()
  const [proyectos, setProyectos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [modalAbierto, setModalAbierto] = useState(false)

  const cargarProyectos = useCallback(async () => {
    setCargando(true)
    setError('')
    try {
      const { data } = await api.get('/proyectos')
      setProyectos(data)
    } catch {
      setError('No se pudieron cargar los proyectos. Verifica que el servidor esté en ejecución.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    cargarProyectos()
  }, [cargarProyectos])

  const cerrarModal = useCallback(() => setModalAbierto(false), [])

  async function alCrearProyecto() {
    setModalAbierto(false)
    await cargarProyectos()
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Mis proyectos</h1>
          <p className="text-sm text-slate-500">Bienvenido, {usuario?.nombreUsuario}</p>
        </div>

        <div className="flex flex-wrap gap-2">
          <Link
            to="/mis-tareas"
            className="rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm hover:bg-slate-50"
          >
            Mis Tareas Personales
          </Link>
          {usuario?.rol === 'ADMIN' && (
            <button
              type="button"
              onClick={() => setModalAbierto(true)}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700"
            >
              + Nuevo Proyecto
            </button>
          )}
        </div>
      </div>

      {cargando ? (
        <Spinner mensaje="Cargando proyectos…" />
      ) : error ? (
        <div className="rounded-xl bg-red-50 p-6 text-center text-sm text-red-700">
          <p>{error}</p>
          <button
            type="button"
            onClick={cargarProyectos}
            className="mt-3 rounded-lg bg-red-600 px-3 py-1.5 text-white hover:bg-red-700"
          >
            Reintentar
          </button>
        </div>
      ) : proyectos.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white py-16 text-center">
          <p className="text-lg font-medium text-slate-700">Aún no tienes proyectos</p>
          <p className="mt-1 text-sm text-slate-500">
            {usuario?.rol === 'ADMIN'
              ? 'Crea el primero con el botón "Nuevo Proyecto".'
              : 'Cuando te agreguen a un proyecto, aparecerá aquí.'}
          </p>
        </div>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {proyectos.map((proyecto) => (
            <ProyectoCard key={proyecto.id} proyecto={proyecto} />
          ))}
        </div>
      )}

      {modalAbierto && <NuevoProyectoModal onCerrar={cerrarModal} onCreado={alCrearProyecto} />}
    </div>
  )
}
