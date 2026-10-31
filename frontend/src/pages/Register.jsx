
import { Link } from 'react-router-dom'
import '../styles/register.css'

function Register() {
  return (
    <main className="register">
      <section className="register__card">
        <h1 className="register__title">Crear cuenta</h1>

        <p className="register__description">
          Registrate para descubrir y comprar entradas para tus eventos favoritos.
        </p>

        <form className="register__form" onSubmit={(e) => e.preventDefault()}>
          <label htmlFor="name">Nombre completo</label>
          <input
            type="text"
            name="name"
            id="name"
            autoComplete="name"
            required
          />

          <label htmlFor="email">Correo electrónico</label>
          <input
            type="email"
            name="email"
            id="email"
            autoComplete="email"
            required
          />

          <label htmlFor="password">Contraseña</label>
          <input
            type="password"
            name="password"
            id="password"
            autoComplete="new-password"
            required
          />

          <button type="submit" className="register__submit">
            Crear cuenta
          </button>
        </form>
        <p className="register__login">
          ¿Ya tenés cuenta?  <Link to="/inicio-sesion">Iniciá sesión.</Link>
        </p>
      </section>
    </main>
  )
}

export default Register
