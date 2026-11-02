
import { apiFetch } from './api'

export function getEvents() {
    return apiFetch('/api/eventos')
}

export function getEventById(id) {
    return apiFetch(`/api/eventos/${id}`)
}
