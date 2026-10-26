import TaskCard from './TaskCard'
import { ESTADOS_TAREA } from '../utils/constantes'

export default function KanbanBoard({ tareas, onTareaActualizada }) {
  return (
    <div className="grid gap-4 md:grid-cols-3">
      {ESTADOS_TAREA.map(({ valor, etiqueta, acento, punto }) => {
        const tareasColumna = tareas.filter((t) => t.estado === valor)

        return (
          <section
            key={valor}
            className={`rounded-2xl border-t-4 ${acento} bg-slate-100/70 p-3`}
          >
            <header className="mb-3 flex items-center justify-between px-1">
              <h3 className="flex items-center gap-2 font-semibold text-slate-700">
                <span className={`h-2.5 w-2.5 rounded-full ${punto}`} />
                {etiqueta}
              </h3>
              <span className="rounded-full bg-white px-2 py-0.5 text-xs font-medium text-slate-500 shadow-sm">
                {tareasColumna.length}
              </span>
            </header>

            <div className="space-y-3">
              {tareasColumna.length === 0 ? (
                <p className="rounded-xl border border-dashed border-slate-300 py-6 text-center text-sm text-slate-400">
                  Sin tareas
                </p>
              ) : (
                tareasColumna.map((tarea) => (
                  <TaskCard key={tarea.id} tarea={tarea} onActualizada={onTareaActualizada} />
                ))
              )}
            </div>
          </section>
        )
      })}
    </div>
  )
}
