
import { Link } from 'react-router-dom'
import '../styles/profile.css'

function Profile() {
  // Datos temporales hasta conectar el servicio de usuarios
  const user = {
    name: 'Nicolás Gómez',
    email: 'nico@email.com',
    phone: '11 1234-5678',
    role: 'CLIENTE'
  }

  return (
    <main className="profile">
      <section className="profile__header">
        <div>
          <span className="profile__tag">MI CUENTA</span>
          <h1>Mi perfil</h1>
          <p>Administrá tu información personal y tus preferencias.</p>
        </div>
      </section>

      <section className="profile__content">

        <div className="profile__card profile__user">
          <div className="profile__avatar">
            {user.name.charAt(0).toUpperCase()}
          </div>

          <div className="profile__user-info">
            <h2>{user.name}</h2>
            <p>{user.email}</p>
            <span className="profile__role">{user.role}</span>
          </div>
        </div>

        <div className="profile__card">
          <div className="profile__section-header">
            <div>
              <h2>Información personal</h2>
              <p>Consultá tus datos registrados.</p>
            </div>

            <button className="profile__edit">
              Editar
            </button>
          </div>

          <div className="profile__details">
            <div className="profile__field">
              <span>Nombre completo</span>
              <strong>{user.name}</strong>
            </div>

            <div className="profile__field">
              <span>Correo electrónico</span>
              <strong>{user.email}</strong>
            </div>

            <div className="profile__field">
              <span>Teléfono</span>
              <strong>{user.phone}</strong>
            </div>
          </div>
        </div>

        <div className="profile__card">
          <h2>Mis actividades</h2>
          <p>Accedé rápidamente a tu actividad en TicketYa.</p>

          <div className="profile__links">
            <Link to="/mis-entradas" className="profile__link">
              <div>
                <strong>Mis entradas</strong>
                <small>Consultá tus entradas y códigos QR.</small>
              </div>
              <span>→</span>
            </Link>

            <Link to="/eventos" className="profile__link">
              <div>
                <strong>Explorar eventos</strong>
                <small>Encontrá tu próxima experiencia.</small>
              </div>
              <span>→</span>
            </Link>
          </div>
        </div>

        <div className="profile__logout">
          <button type="button">
            Cerrar sesión
          </button>
        </div>

      </section>
    </main>
  )
}

export default Profile
