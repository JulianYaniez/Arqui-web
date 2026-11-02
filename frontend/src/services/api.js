const API_URL = 'http://localhost:8080'

export async function apiFetch(endpoint, options = {}) {
const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers: {
    'Content-Type': 'application/json',
    ...options.headers
    }
})

    if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`)
}

    if (response.status === 204) return null

    return response.json()
}
