
const API_URL = 'http://localhost:8080'

export async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && {
        Authorization: `Bearer ${token}`,
      }),
      ...options.headers,
    },
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => null)
    throw new Error(errorData?.message || `Error ${response.status}: ${response.statusText}`)
  }

  const text = await response.text()

  return text ? JSON.parse(text) : null
}
