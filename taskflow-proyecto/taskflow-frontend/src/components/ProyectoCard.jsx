import { useNavigate } from 'react-router-dom'
import Badge from './Badge'
import { ESTADOS_PROYECTO } from '../utils/constantes'

export default function ProyectoCard({ proyecto }) {
  const navigate = useNavigate()
  const estado = ESTADOS_PROYECTO[proyecto.estado]

  return (
    <article className="flex flex-col justify-between rounded-2xl border border-slate-100 bg-white p-5 shadow-sm transition hover:shadow-md">
      <div>
        <div className="mb-2 flex items-start justify-between gap-3">
          <h3 className="text-lg font-semibold text-slate-800">{proyecto.nombre}</h3>
          {estado && <Badge clases={estado.clases}>{estado.etiqueta}</Badge>}
        </div>
        <p className="line-clamp-3 text-sm text-slate-500">
          {proyecto.descripcion || 'Sin descripción'}
        </p>
      </div>

      <div className="mt-5 flex items-center justify-between">
        <span className="text-xs text-slate-400">Creado por {proyecto.creadoPor}</span>
        <button
          type="button"
          onClick={() => navigate(`/proyecto/${proyecto.id}`)}
          className="rounded-lg bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-indigo-700"
        >
          Ver Tareas
        </button>
      </div>
    </article>
  )
}
