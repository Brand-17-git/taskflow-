import { useState } from 'react'
import api from '../api/axios'
import Modal from './Modal'
import { ESTADOS_PROYECTO } from '../utils/constantes'

function mensajeDeError(error) {
  if (!error.response) return 'No se pudo conectar con el servidor'
  if (error.response.status === 403) return 'Solo un ADMIN puede crear proyectos'
  if (error.response.status === 400) return 'Revisa los datos: el nombre es obligatorio (máx. 100) y la descripción admite hasta 500 caracteres'
  return 'No se pudo crear el proyecto'
}

export default function NuevoProyectoModal({ onCerrar, onCreado }) {
  const [nombre, setNombre] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [estado, setEstado] = useState('ACTIVO')
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')

  async function manejarEnvio(e) {
    e.preventDefault()
    setError('')
    setGuardando(true)
    try {
      await api.post('/proyectos', { nombre: nombre.trim(), descripcion: descripcion.trim(), estado })
      await onCreado() // el padre recarga la lista y cierra el modal
    } catch (err) {
      setError(mensajeDeError(err))
      setGuardando(false)
    }
  }

  const claseInput =
    'w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500'

  return (
    <Modal titulo="Nuevo proyecto" onCerrar={onCerrar}>
      <form onSubmit={manejarEnvio} className="space-y-4">
        {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

        <div>
          <label htmlFor="nombre" className="mb-1 block text-sm font-medium text-slate-700">
            Nombre
          </label>
          <input
            id="nombre"
            type="text"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            maxLength={100}
            required
            className={claseInput}
          />
        </div>

        <div>
          <label htmlFor="descripcion" className="mb-1 block text-sm font-medium text-slate-700">
            Descripción
          </label>
          <textarea
            id="descripcion"
            rows={3}
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            maxLength={500}
            className={claseInput}
          />
        </div>

        <div>
          <label htmlFor="estado" className="mb-1 block text-sm font-medium text-slate-700">
            Estado
          </label>
          <select id="estado" value={estado} onChange={(e) => setEstado(e.target.value)} className={claseInput}>
            {Object.entries(ESTADOS_PROYECTO).map(([valor, { etiqueta }]) => (
              <option key={valor} value={valor}>
                {etiqueta}
              </option>
            ))}
          </select>
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <button
            type="button"
            onClick={onCerrar}
            className="rounded-lg px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={guardando}
            className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-60"
          >
            {guardando ? 'Guardando…' : 'Guardar'}
          </button>
        </div>
      </form>
    </Modal>
  )
}
