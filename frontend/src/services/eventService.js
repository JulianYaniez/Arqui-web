
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

// Obtener todos los eventos publicados para el catálogo
export async function getEvents(params = {}) {
  const pageSize = 100
  let page = 0
  let allEvents = []
  let totalPages = 1

  do {
    const queryParams = new URLSearchParams({
      ...params,
      page: page.toString(),
      size: pageSize.toString()
    })

    const data = await apiFetch(
      `${API_URL}/event?${queryParams.toString()}`
    )

    const eventList = Array.isArray(data)
      ? data
      : data?.content || []

    allEvents = [...allEvents, ...eventList]

    totalPages = data?.page?.totalPages ?? 1
    page++

  } while (page < totalPages)

  return allEvents.map(adaptEvent)
}

// Obtener todos los eventos para administración
export async function findAllEvents() {
  const pageSize = 100
  let page = 0
  let allEvents = []
  let totalPages = 1

  do {
    const params = new URLSearchParams({
      page: page.toString(),
      size: pageSize.toString()
    })

    const data = await apiFetch(
      `${API_URL}/event?${params.toString()}`
    )

    const eventList = Array.isArray(data)
      ? data
      : data?.content || []

    allEvents = [...allEvents, ...eventList]

    totalPages = data?.page?.totalPages ?? 1
    page++

  } while (page < totalPages)

  return allEvents.map(adaptEvent)
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
  switch (status) {
    case 'PUBLISHED':
      return publishEvent(id)

    case 'CANCELLED':
      return cancelEvent(id)

    default:
      throw new Error(`Estado no soportado: ${status}`)
  }
}

