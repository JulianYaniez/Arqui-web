
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/login.css'

function Login() {
  const navigate = useNavigate()

  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  if (user) {
    console.log('Hay una sesión guardada')
    console.log(user.email)
    console.log(user.role)
  } else {
    console.log('No hay sesión')
  }

  const [form, setForm] = useState({
    email: '',
    password: ''
  })

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm({
      ...form,
      [name]: value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    // Simulamos los datos del usuario
    const user = {
      email: form.email,
      role: 'USER'
    }

    // Guardamos los datos en localStorage
    localStorage.setItem('user', JSON.stringify(user))

    console.log('Usuario guardado:', user)

    // Redirigimos al inicio
    navigate('/')
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
