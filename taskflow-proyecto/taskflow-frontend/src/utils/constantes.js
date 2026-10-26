export const ESTADOS_PROYECTO = {
  ACTIVO: { etiqueta: 'Activo', clases: 'bg-green-100 text-green-700 ring-green-600/20' },
  PAUSADO: { etiqueta: 'Pausado', clases: 'bg-yellow-100 text-yellow-800 ring-yellow-600/20' },
  COMPLETADO: { etiqueta: 'Completado', clases: 'bg-blue-100 text-blue-700 ring-blue-600/20' },
}

export const PRIORIDADES = {
  ALTA: { etiqueta: 'Alta', clases: 'bg-red-100 text-red-700 ring-red-600/20' },
  MEDIA: { etiqueta: 'Media', clases: 'bg-yellow-100 text-yellow-800 ring-yellow-600/20' },
  BAJA: { etiqueta: 'Baja', clases: 'bg-green-100 text-green-700 ring-green-600/20' },
}

// Columnas del tablero Kanban (el orden define cómo se muestran)
export const ESTADOS_TAREA = [
  { valor: 'PENDIENTE', etiqueta: 'Pendiente', acento: 'border-slate-400', punto: 'bg-slate-400' },
  { valor: 'EN_PROGRESO', etiqueta: 'En Progreso', acento: 'border-indigo-500', punto: 'bg-indigo-500' },
  { valor: 'COMPLETADA', etiqueta: 'Completada', acento: 'border-emerald-500', punto: 'bg-emerald-500' },
]
