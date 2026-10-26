import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../api/axios'
import Badge from '../components/Badge'
import KanbanBoard from '../components/KanbanBoard'
import Spinner from '../components/Spinner'
import { ESTADOS_PROYECTO } from '../utils/constantes'

function mensajeDeError(error) {
  if (error.response?.status === 403) return 'No eres miembro de este proyecto.'
  if (error.response?.status === 404) return 'Proyecto no encontrado.'
  return 'No se pudieron cargar los datos del proyecto.'
}

export default function ProjectDetailsPage() {
  const { id } = useParams()
  const [proyecto, setProyecto] = useState(null)
  const [tareas, setTareas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let activo = true
    setCargando(true)
    setError('')

    Promise.all([api.get(`/proyectos/${id}`), api.get(`/tareas/proyecto/${id}`)])
      .then(([resProyecto, resTareas]) => {
        if (!activo) return
        setProyecto(resProyecto.data)
        setTareas(resTareas.data)
      })
      .catch((err) => {
        if (activo) setError(mensajeDeError(err))
      })
      .finally(() => {
        if (activo) setCargando(false)
      })

    return () => {
      activo = false
    }
  }, [id])

  function actualizarTarea(actualizada) {
    setTareas((prev) => prev.map((t) => (t.id === actualizada.id ? actualizada : t)))
  }

  const estado = proyecto && ESTADOS_PROYECTO[proyecto.estado]

  return (
    <div>
      <Link
        to="/"
        className="mb-4 inline-flex items-center gap-1 text-sm font-medium text-indigo-600 hover:text-indigo-800"
      >
        ← Volver al Dashboard
      </Link>

      {cargando ? (
        <Spinner mensaje="Cargando tablero…" />
      ) : error ? (
        <div className="rounded-xl bg-red-50 p-6 text-center text-sm text-red-700">{error}</div>
      ) : (
        <>
          <div className="mb-6">
            <div className="flex flex-wrap items-center gap-3">
              <h1 className="text-2xl font-bold text-slate-800">{proyecto.nombre}</h1>
              {estado && <Badge clases={estado.clases}>{estado.etiqueta}</Badge>}
            </div>
            {proyecto.descripcion && (
              <p className="mt-1 max-w-3xl text-sm text-slate-500">{proyecto.descripcion}</p>
            )}
          </div>

          <KanbanBoard tareas={tareas} onTareaActualizada={actualizarTarea} />
        </>
      )}
    </div>
  )
}
