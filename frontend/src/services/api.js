
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
    const errorData = await response.json().catch(() => null)

    throw new Error(
      errorData?.message || `Error ${response.status}: ${response.statusText}`
    )
  }

  // Respuestas sin contenido
  if (
    response.status === 204 ||
    response.headers.get('content-length') === '0'
  ) {
    return null
  }

  // Algunas respuestas pueden no tener body
  if (response.status === 201 && !response.headers.get('content-type')?.includes('application/json')) {
    return null
  }

  return response.json()
}
