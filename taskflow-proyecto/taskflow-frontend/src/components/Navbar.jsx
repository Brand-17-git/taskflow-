import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { usuario, logout } = useAuth()

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
        <Link to="/" className="text-xl font-bold text-indigo-600">
          TaskFlow
        </Link>
        <div className="flex items-center gap-3">
          <div className="hidden text-right text-sm sm:block">
            <p className="font-medium text-slate-800">{usuario?.nombreUsuario}</p>
            <p className="text-xs text-slate-500">{usuario?.rol}</p>
          </div>
          <button
            type="button"
            onClick={logout}
            className="rounded-lg bg-slate-800 px-3 py-1.5 text-sm font-medium text-white hover:bg-slate-700"
          >
            Cerrar sesión
          </button>
        </div>
      </div>
    </header>
  )
}
