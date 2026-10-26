import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function mensajeDeError(error) {
  if (!error.response) return 'No se pudo conectar con el servidor'
  if (error.response.status === 401) return 'Usuario o contraseña incorrectos'
  if (error.response.status === 400) return 'Revisa los datos ingresados'
  return 'Ocurrió un error inesperado'
}

export default function LoginPage() {
  const { token, login } = useAuth()
  const navigate = useNavigate()
  const [nombreUsuario, setNombreUsuario] = useState('')
  const [contrasena, setContrasena] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  if (token) return <Navigate to="/" replace />

  async function manejarEnvio(e) {
    e.preventDefault()
    setError('')
    setCargando(true)
    try {
      await login(nombreUsuario, contrasena)
      navigate('/', { replace: true })
    } catch (err) {
      setError(mensajeDeError(err))
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <form
        onSubmit={manejarEnvio}
        className="w-full max-w-sm space-y-4 rounded-xl bg-white p-8 shadow"
      >
        <h1 className="text-2xl font-bold text-slate-800">TaskFlow</h1>
        <p className="text-sm text-slate-500">Inicia sesión para continuar</p>

        {error && (
          <div className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
        )}

        <div>
          <label htmlFor="usuario" className="mb-1 block text-sm font-medium text-slate-700">
            Usuario
          </label>
          <input
            id="usuario"
            type="text"
            value={nombreUsuario}
            onChange={(e) => setNombreUsuario(e.target.value)}
            required
            autoComplete="username"
            className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label htmlFor="contrasena" className="mb-1 block text-sm font-medium text-slate-700">
            Contraseña
          </label>
          <input
            id="contrasena"
            type="password"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
            autoComplete="current-password"
            className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        <button
          type="submit"
          disabled={cargando}
          className="w-full rounded-md bg-indigo-600 px-4 py-2 font-medium text-white hover:bg-indigo-700 disabled:opacity-60"
        >
          {cargando ? 'Entrando…' : 'Entrar'}
        </button>
      </form>
    </div>
  )
}
