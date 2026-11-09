
import { apiFetch } from './api'

export function getVenues() {
  return apiFetch('/api/catalog/venue')
}

export function createVenue(venue) {
  return apiFetch('/api/catalog/venue', {
    method: 'POST',
    body: JSON.stringify(venue)
  })
}

export function getVenueById(id) {
  return apiFetch(`/api/catalog/venue/${id}`)
}
