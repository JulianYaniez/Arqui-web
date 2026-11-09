
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
    id: event.id,
    name: event.name,
    description: event.description,
    category: eventTypes[event.type] ?? event.type,
    date: event.dateTime,
    location: event.city,
    status: eventStatuses[event.status] ?? event.status,
    sectors: event.sectors ?? [],
    price: event.sectors?.length
      ? Math.min(...event.sectors.map(sector => Number(sector.price)))
      : 0,
    image: null
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

// Crear evento
export async function createEvent(event) {
  return apiFetch(`${API_URL}/event`, {
    method: 'POST',
    body: JSON.stringify(event)
  })
}

// Publicar evento
export function publishEvent(id) {
  return apiFetch(`${API_URL}/event/${id}/publish`, {
    method: 'POST'
  })
}

// Cancelar evento
export function cancelEvent(id) {
  return apiFetch(`${API_URL}/event/${id}/cancel`, {
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

