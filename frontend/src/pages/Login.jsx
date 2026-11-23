import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/login.css'

import { login } from '../services/authService'

function Login() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    email: '',
    password: ''
  })

  const [error, setError] = useState('')

  // Verificar si ya existe una sesión
  useEffect(() => {
    const storedUser = localStorage.getItem('user')

    if (storedUser) {
      const user = JSON.parse(storedUser)

      if (user.role === 'ADMIN') {
        navigate('/managment', { replace: true })
      } else {
        navigate('/', { replace: true })
      }
    }
  }, [navigate])

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm({
      ...form,
      [name]: value
    })

    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    try {
      const response = await login(form)

      const user = {
        id: response.userId,
        email: response.email,
        role: response.role
      }

      localStorage.setItem('token', response.accessToken)
      localStorage.setItem('user', JSON.stringify(user))

      if (user.role === 'ADMIN') {
        navigate('/managment')
      } else {
        navigate('/')
      }
    } catch (error) {
      setError(error.message || 'No se pudo iniciar sesión.')
    }
  }

  return (
    <main className="login">
      <section className="login__card">
        <h1 className="login__title">¡Qué bueno verte!</h1>

        <p className="login__description">
          Iniciá sesión para continuar en TicketYa.
        </p>

        <form
          className="login__form"
          onSubmit={handleSubmit}
        >
          <label htmlFor="email">Correo electrónico</label>

          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            placeholder="nombre@ejemplo.com"
            value={form.email}
            onChange={handleChange}
            required
          />

          <label htmlFor="password">Contraseña</label>

          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            placeholder="Ingresá tu contraseña"
            value={form.password}
            onChange={handleChange}
            required
          />

          {error && (
            <p className="login__error">
              {error}
            </p>
          )}

          <button type="submit" className="login__submit">
            Ingresar
          </button>
        </form>

        <p className="login__register">
          ¿No tenés cuenta? <Link to="/register">Creá una</Link>
        </p>
      </section>
    </main>
  )
}

export default Login