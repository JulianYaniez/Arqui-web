
import '../styles/navbar.css'

function Navbar() {
  return (
    <nav className="navbar">
      <h1>TicketYa</h1>

      <ul className="navbar__links">
        <li><a href="">Inicio</a></li>
        <li><a href="">Eventos</a></li>
        <li><a href="">Iniciar sesión</a></li>
      </ul>
    </nav>
  )
}

export default Navbar
