
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/register.css'

import { register } from '../services/authService'

function Register() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    name: '',
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

  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      const [firstName, ...lastNameParts] = form.name.trim().split(' ')

      await register({
        firstName,
        lastName: lastNameParts.join(' '),
        email: form.email,
        password: form.password
      })

      navigate('/login')
    } catch (error) {
      console.error('Error al registrarse:', error)
    }
  }

  return (
    <main className="register">
      <section className="register__card">
        <h1 className="register__title">Crear cuenta</h1>

        <p className="register__description">
          Registrate para descubrir y comprar entradas para tus eventos favoritos.
        </p>

        <form
          className="register__form"
          onSubmit={handleSubmit}
        >
          <label htmlFor="name">Nombre completo</label>
          <input
            type="text"
            name="name"
            id="name"
            autoComplete="name"
            value={form.name}
            onChange={handleChange}
            required
          />

          <label htmlFor="email">Correo electrónico</label>
          <input
            type="email"
            name="email"
            id="email"
            autoComplete="email"
            value={form.email}
            onChange={handleChange}
            required
          />

          <label htmlFor="password">Contraseña</label>
          <input
            type="password"
            name="password"
            id="password"
            autoComplete="new-password"
            value={form.password}
            onChange={handleChange}
            required
          />

          <button type="submit" className="register__submit">
            Crear cuenta
          </button>
        </form>

        <p className="register__login">
          ¿Ya tenés cuenta? <Link to="/login">Iniciá sesión.</Link>
        </p>
      </section>
    </main>
  )
}

export default Register
