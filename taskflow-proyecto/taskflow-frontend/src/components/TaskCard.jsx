import { useState } from 'react'
import api from '../api/axios'
import Badge from './Badge'
import { ESTADOS_TAREA, PRIORIDADES } from '../utils/constantes'

function formatearFecha(fecha) {
  // fechaLimite llega como "YYYY-MM-DD"; se fuerza hora local para evitar desfases de zona horaria
  return new Date(`${fecha}T00:00:00`).toLocaleDateString('es', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

export default function TaskCard({ tarea, onActualizada }) {
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')
  const prioridad = PRIORIDADES[tarea.prioridad]

  async function cambiarEstado(e) {
    const nuevoEstado = e.target.value
    if (nuevoEstado === tarea.estado) return

    setError('')
    setGuardando(true)
    try {
      const { data } = await api.patch(`/tareas/${tarea.id}/estado`, { estado: nuevoEstado })
      onActualizada(data)
    } catch (err) {
      setError(
        err.response?.status === 403
          ? 'Solo el asignado o el líder del proyecto puede cambiar el estado'
          : 'No se pudo actualizar el estado',
      )
    } finally {
      setGuardando(false)
    }
  }

  return (
    <article className="rounded-xl border border-slate-100 bg-white p-4 shadow-sm">
      <div className="mb-2 flex items-start justify-between gap-2">
        <h4 className="font-medium text-slate-800">{tarea.titulo}</h4>
        {prioridad && <Badge clases={prioridad.clases}>{prioridad.etiqueta}</Badge>}
      </div>

      {tarea.descripcion && (
        <p className="mb-3 line-clamp-2 text-sm text-slate-500">{tarea.descripcion}</p>
      )}

      <div className="mb-3 space-y-1 text-xs text-slate-500">
        <p>
          <span className="text-slate-400">Asignado a:</span>{' '}
          <span className="font-medium text-slate-600">{tarea.asignadoA || 'Sin asignar'}</span>
        </p>
        {tarea.fechaLimite && (
          <p>
            <span className="text-slate-400">Fecha límite:</span>{' '}
            <span className="font-medium text-slate-600">{formatearFecha(tarea.fechaLimite)}</span>
          </p>
        )}
      </div>

      <label className="sr-only" htmlFor={`estado-${tarea.id}`}>
        Cambiar estado
      </label>
      <select
        id={`estado-${tarea.id}`}
        value={tarea.estado}
        onChange={cambiarEstado}
        disabled={guardando}
        className="w-full rounded-lg border border-slate-300 bg-white px-2 py-1.5 text-sm text-slate-700 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 disabled:opacity-60"
      >
        {ESTADOS_TAREA.map(({ valor, etiqueta }) => (
          <option key={valor} value={valor}>
            {etiqueta}
          </option>
        ))}
      </select>

      {error && <p className="mt-2 text-xs text-red-600">{error}</p>}
    </article>
  )
}
