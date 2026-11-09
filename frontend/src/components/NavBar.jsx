
import { Link, useNavigate } from 'react-router-dom'
import '../styles/navbar.css'

function Navbar() {
  const navigate = useNavigate()

  // Recuperar usuario de localStorage
  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  // Verificar si es administrador
  const isAdmin = user?.role === 'ADMIN'

  // Cerrar sesión
  const handleLogout = () => {
    localStorage.removeItem('user')
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="navbar__logo">
        <h1>TicketYa</h1>
      </Link>

      <ul className="navbar__links">
        <li>
          <Link to="/">Inicio</Link>
        </li>

        <li>
          <Link to="/event">Eventos</Link>
        </li>

        {/* Solo administradores */}
        {isAdmin && (
          <li>
            <Link to="/managment">Management</Link>
          </li>
        )}

        {/* Cualquier usuario autenticado */}
        {user ? (
          <>
            <li>
              <Link to="/profile">Perfil</Link>
            </li>

            <li>
              <button
                className="navbar__logout"
                onClick={handleLogout}
              >
                Cerrar sesión
              </button>
            </li>
          </>
        ) : (
          <li>
            <Link to="/login">Iniciar sesión</Link>
          </li>
        )}
      </ul>
    </nav>
  )
}

export default Navbar
