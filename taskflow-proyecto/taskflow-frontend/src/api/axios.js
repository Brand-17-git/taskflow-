import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

// Añade el JWT guardado en localStorage a cada petición
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Si el token expiró o es inválido (401) fuera de /auth, se cierra la sesión
api.interceptors.response.use(
  (respuesta) => respuesta,
  (error) => {
    const url = error.config?.url ?? ''
    if (error.response?.status === 401 && !url.startsWith('/auth/')) {
      localStorage.removeItem('token')
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
    }
    return Promise.reject(error)
  },
)

export default api
