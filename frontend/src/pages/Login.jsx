
import { Link } from 'react-router-dom'
import '../styles/login.css'

function Login() {
  return (
    <main className="login">
      <section className="login__card">
        <h1 className="login__title">¡Qué bueno verte!</h1>

        <p className="login__description">
          Iniciá sesión para continuar en TicketYa.
        </p>

        <form
          className="login__form"
          onSubmit={(e) => e.preventDefault()}
        >
          <label htmlFor="email">Correo electrónico</label>
          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            placeholder="nombre@ejemplo.com"
            required
          />

          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            placeholder="Ingresá tu contraseña"
            required
          />

          <button type="submit" className="login__submit">
            Ingresar
          </button>
        </form>

        <p className="login__register">
          ¿No tenés cuenta? <Link to="/registro">Creá una</Link>
        </p>
      </section>
    </main>
  )
}

export default Login
