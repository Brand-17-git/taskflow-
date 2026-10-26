import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import KanbanBoard from '../components/KanbanBoard'
import Spinner from '../components/Spinner'

export default function MyTasksPage() {
  const [tareas, setTareas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let activo = true
    api
      .get('/tareas/mis-tareas')
      .then(({ data }) => {
        if (activo) setTareas(data)
      })
      .catch(() => {
        if (activo) setError('No se pudieron cargar tus tareas personales.')
      })
      .finally(() => {
        if (activo) setCargando(false)
      })
    return () => {
      activo = false
    }
  }, [])

  function actualizarTarea(actualizada) {
    setTareas((prev) => prev.map((t) => (t.id === actualizada.id ? actualizada : t)))
  }

  return (
    <div>
      <Link
        to="/"
        className="mb-4 inline-flex items-center gap-1 text-sm font-medium text-indigo-600 hover:text-indigo-800"
      >
        ← Volver al Dashboard
      </Link>

      <h1 className="mb-6 text-2xl font-bold text-slate-800">Mis tareas personales</h1>

      {cargando ? (
        <Spinner mensaje="Cargando tus tareas…" />
      ) : error ? (
        <div className="rounded-xl bg-red-50 p-6 text-center text-sm text-red-700">{error}</div>
      ) : (
        <KanbanBoard tareas={tareas} onTareaActualizada={actualizarTarea} />
      )}
    </div>
  )
}
