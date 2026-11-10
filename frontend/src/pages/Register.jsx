
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/register.css'

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

  const handleSubmit = (e) => {
    e.preventDefault()

    // Crear objeto del usuario
    const user = {
      id: crypto.randomUUID(),
      name: form.name,
      email: form.email,
      role: 'USER'
    }

    // Guardar usuario en localStorage
    localStorage.setItem('user', JSON.stringify(user))

    console.log('Usuario registrado:', user)

    // Redirigir al inicio
    navigate('/')
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
