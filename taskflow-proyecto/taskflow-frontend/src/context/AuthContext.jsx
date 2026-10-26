import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import api from '../api/axios'

const AuthContext = createContext(null)

// Decodifica el payload del JWT (solo para mostrar datos; la verificación real la hace el backend)
function decodificarToken(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join(''),
    )
    return JSON.parse(json)
  } catch {
    return null
  }
}

function usuarioDesdeToken(token) {
  const payload = token ? decodificarToken(token) : null
  if (!payload) return null
  if (payload.exp && payload.exp * 1000 < Date.now()) return null // expirado
  return { nombreUsuario: payload.sub, rol: payload.rol }
}

function leerSesionInicial() {
  const token = localStorage.getItem('token')
  const usuario = usuarioDesdeToken(token)
  if (!usuario) {
    localStorage.removeItem('token')
    return { token: null, usuario: null }
  }
  return { token, usuario }
}

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(leerSesionInicial)

  const login = useCallback(async (nombreUsuario, contrasena) => {
    const { data } = await api.post('/auth/login', { nombreUsuario, contrasena })
    localStorage.setItem('token', data.token)
    setSesion({ token: data.token, usuario: usuarioDesdeToken(data.token) })
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    setSesion({ token: null, usuario: null })
  }, [])

  const value = useMemo(
    () => ({ token: sesion.token, usuario: sesion.usuario, login, logout }),
    [sesion, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  }
  return ctx
}
