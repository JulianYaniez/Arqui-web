import { useEffect, useState } from 'react'
import { Link, useNavigate, Navigate } from 'react-router-dom'
import { getUserById } from '../services/authService'
import '../styles/profile.css'

function Profile() {

  const navigate = useNavigate()

  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user?.id) return

    const loadProfile = async () => {
      try {
        setLoading(true)
        setError('')

        // Fetch the user's profile information
        const data = await getUserById(user.id)
        setProfile(data)

      } catch (error) {
        console.error('Error cargando perfil:', error)
        setError('No se pudo cargar la información del usuario.')

      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [user?.id])

  // Redirect if the user is not authenticated
  if (!user) {
    return <Navigate to="/login" replace />
  }

  const handleLogout = () => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    navigate('/login')
  }

  if (loading) {
    return <main className="profile">Cargando perfil...</main>
  }

  if (error) {
    return (
      <main className="profile">
        <p>{error}</p>
      </main>
    )
  }

  const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim()
  const displayName = fullName || profile.email

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
            {displayName.charAt(0).toUpperCase()}
          </div>

          <div className="profile__user-info">
            <h2>{displayName}</h2>
            <p>{profile.email}</p>
            <span className="profile__role">{profile.role}</span>
          </div>
        </div>

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
              <strong>{profile.firstName || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Apellido</span>
              <strong>{profile.lastName || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Correo electrónico</span>
              <strong>{profile.email || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Tipo de cuenta</span>
              <strong>{profile.role || '-'}</strong>
            </div>

            <div className="profile__field">
              <span>Estado de cuenta</span>
              <strong>{profile.isActive ? 'Activa' : 'Inactiva'}</strong>
            </div>

          </div>
        </div>

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