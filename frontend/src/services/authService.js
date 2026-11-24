import { apiFetch } from './api'

const API_URL = '/api/identity/auth'

export const login = async (credentials) => {
  return apiFetch(`${API_URL}/login`, {
    method: 'POST',
    body: JSON.stringify({
      email: credentials.email,
      password: credentials.password,
    }),
  })
}

export const register = async (userData) => {
  return apiFetch(`${API_URL}/register`, {
    method: 'POST',
    body: JSON.stringify(userData),
  })
}

// Obtener información del usuario
export const getUserById = async (id) => {
  return apiFetch(`/api/identity/auth/${id}`)
}