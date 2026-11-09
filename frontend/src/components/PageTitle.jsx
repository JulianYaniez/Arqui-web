import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

function PageTitle() {
  const location = useLocation()

  useEffect(() => {
    const titles = {
      '/': 'Inicio',
      '/event': 'Eventos',
      '/login': 'Iniciar sesión',
      '/register': 'Registrarse',
      '/profile': 'Mi perfil',
      '/my-tickets': 'Mis entradas',
      '/managment': 'Administración',
    }

    let title = titles[location.pathname]

    // Detectar páginas dinámicas
    if (location.pathname.match(/^\/event\/[^/]+\/buy$/)) {
      title = 'Comprar entradas'
    } else if (location.pathname.match(/^\/event\/[^/]+$/)) {
      title = 'Detalle del evento'
    }

    document.title = title
      ? `TicketYa | ${title}`
      : 'TicketYa'
  }, [location.pathname])

  return null
}

export default PageTitle