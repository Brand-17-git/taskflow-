export default function Spinner({ mensaje = 'Cargando...' }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-slate-500" role="status">
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />
      <span className="text-sm">{mensaje}</span>
    </div>
  )
}
