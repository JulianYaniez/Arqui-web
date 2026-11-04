
import { apiFetch } from './api'

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

export async function getEvents() {
  const data = await apiFetch('/api/event')

  return {
    ...data,
    content: data.content.map(adaptEvent)
  }
}

export async function createEvent(event) {
  return apiFetch('/api/event', {
    method: 'POST',
    body: JSON.stringify(event)
  })
}

export function publishEvent(id) {
  return apiFetch(`/api/event/${id}/publish`, {
    method: 'POST'
  })
}
