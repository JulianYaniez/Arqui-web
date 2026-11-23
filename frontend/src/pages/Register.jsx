
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/register.css'

import { register } from '../services/authService'

function Register() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    passwordRepeat: ''
  })

  const [error, setError] = useState('')

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

    const passwordRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/

    if (!passwordRegex.test(form.password)) {
      setError(
        'La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.'
      )
      return
    }

    if (form.password !== form.passwordRepeat) {
      setError('Las contraseñas no coinciden.')
      return
    }

    try {
      await register({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        password: form.password,
        passwordRepeat: form.passwordRepeat
      })

      navigate('/login')
    } catch (error) {
      console.error('Error al registrarse:', error)

      setError(
        error.response?.data?.message ||
        'No se pudo crear la cuenta. Intentá nuevamente.'
      )
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
          <label htmlFor="firstName">Nombre</label>
          <input
            type="text"
            name="firstName"
            id="firstName"
            autoComplete="given-name"
            value={form.firstName}
            onChange={handleChange}
            required
          />

          <label htmlFor="lastName">Apellido</label>
          <input
            type="text"
            name="lastName"
            id="lastName"
            autoComplete="family-name"
            value={form.lastName}
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
            minLength={8}
            required
          />

          <label htmlFor="passwordRepeat">Repetir contraseña</label>
            <input
              type="password"
              name="passwordRepeat"
              id="passwordRepeat"
              autoComplete="new-password"
              value={form.passwordRepeat}
              onChange={handleChange}
              required
            />

          <small className="register__hint">
            Mínimo 8 caracteres, incluyendo mayúscula, minúscula,
            número y símbolo.
          </small>

          {error && (
            <p className="register__error">
              {error}
            </p>
          )}

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
