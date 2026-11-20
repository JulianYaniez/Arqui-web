
import { apiFetch } from './api'

const API_URL = '/api/catalog'

const eventTypes = {
  RECITAL: 'Música',
  THEATER: 'Teatro',
  SPORTS: 'Deportes',
  CONFERENCE: 'Tecnología',
  WORKSHOP: 'Otros',
  SEMINAR: 'Otros',
  FESTIVAL: 'Otros'
}

const eventStatuses = {
  DRAFT: 'BORRADOR',
  PUBLISHED: 'PUBLICADO',
  CANCELLED: 'CANCELADO',
  FINISHED: 'FINALIZADO'
}

// Adaptar evento para el frontend

function adaptEvent(event) {
  return {
    ...event,

    category: eventTypes[event.type] ?? event.type,

    statusLabel: eventStatuses[event.status] ?? event.status,

    price: event.sectors?.length
      ? Math.min(
          ...event.sectors.map(sector => Number(sector.price))
        )
      : 0,

    image: event.image ?? null
  }
}

// Obtener todos los eventos
export async function getEvents(params = {}) {
  const query = new URLSearchParams(params).toString()
  const data = await apiFetch(`${API_URL}/event${query ? `?${query}` : ''}`)

  return {
    ...data,
    content: data.content.map(adaptEvent)
  }
}

export async function getEventById(id) {
  const event = await apiFetch(`${API_URL}/event/${id}`)
  return adaptEvent(event)
}

// Crear evento
export const createEvent = async (eventData) => {
  return apiFetch(`${API_URL}/event`, {
    method: 'POST',
    body: JSON.stringify(eventData)
  })
}

// Publicar evento
export const publishEvent = async (id) => {
  return apiFetch(`${API_URL}/event/${id}/publish`, {
    method: 'POST'
  })
}

// Cancelar evento
export const cancelEvent = async (id) => {
  return apiFetch(`/api/catalog/event/${id}/cancel`, {
    method: 'POST'
  })
}

// Consultar estado del evento
export async function getEventState(id) {
  return apiFetch(`${API_URL}/event/${id}/state`)
}

// Agregar sector
export function addEventSector(id, sector) {
  return apiFetch(`${API_URL}/event/${id}/sectors`, {
    method: 'POST',
    body: JSON.stringify(sector)
  })
}

// actualizar el estado del evento
export const updateEventStatus = async (id, status) => {
  const response = await fetch(`${API_URL}/api/events/${id}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status })
  })

  if (!response.ok) {
    throw new Error('No se pudo actualizar el estado del evento')
  }

  return response.json()
}

