
import { Link, useNavigate, Navigate } from 'react-router-dom'
import '../styles/profile.css'

function Profile() {
  const navigate = useNavigate()

  // Recuperar usuario de localStorage
  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  // Si no hay sesión, volver al login
  if (!user) {
    return <Navigate to="/login" replace />
  }

  // Datos del usuario
  const fullName = `${user.firstName || ''} ${user.lastName || ''}`.trim()
  const displayName = fullName || user.name || 'Usuario'

  // Cerrar sesión
  const handleLogout = () => {
    localStorage.removeItem('user')
    navigate('/login')
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

        {/* Tarjeta del usuario */}
        <div className="profile__card profile__user">
          <div className="profile__avatar">
            {displayName.charAt(0).toUpperCase()}
          </div>

          <div className="profile__user-info">
            <h2>{displayName}</h2>
            <p>{user.email}</p>
            <span className="profile__role">{user.role}</span>
          </div>
        </div>

        {/* Información personal */}
        <div className="profile__card">
          <div className="profile__section-header">
            <div>
              <h2>Información personal</h2>
              <p>Consultá tus datos registrados.</p>
            </div>
          </div>

          <div className="profile__details">

            <div className="profile__field">
              <span>Nombre</span>
              <strong>{user.firstName || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Apellido</span>
              <strong>{user.lastName || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Correo electrónico</span>
              <strong>{user.email || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Teléfono</span>
              <strong>{user.phone || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Tipo de cuenta</span>
              <strong>{user.role || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Estado de cuenta</span>
              <strong>{user.isActive ? 'Activa' : 'Inactiva'}</strong>
            </div>

          </div>
        </div>

        {/* Actividades */}
        <div className="profile__card">
          <h2>Mis actividades</h2>
          <p>Accedé rápidamente a tu actividad en TicketYa.</p>

          <div className="profile__links">

            <Link to="/my-tickets" className="profile__link">
              <div>
                <strong>Mis entradas</strong>
                <small>Consultá tus entradas y códigos QR.</small>
              </div>
              <span>→</span>
            </Link>

            <Link to="/event" className="profile__link">
              <div>
                <strong>Explorar eventos</strong>
                <small>Encontrá tu próxima experiencia.</small>
              </div>
              <span>→</span>
            </Link>

          </div>
        </div>

        {/* Cerrar sesión */}
        <div className="profile__logout">
          <button
            id="btn__logout"
            type="button"
            onClick={handleLogout}
          >
            Cerrar sesión
          </button>
        </div>

      </section>
    </main>
  )
}

export default Profile
