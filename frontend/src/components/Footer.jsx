
import '../styles/footer.css'

function Footer() {
  return (
    <footer className="footer">
      <div className="footer__brand">
        <h2>TicketYa</h2>
        <p>Encontrá tu próximo evento.</p>
      </div>

      <nav className="footer__links" aria-label="Enlaces del pie de página">
        <a href="/">Inicio</a>
        <a href="/eventos">Eventos</a>
        <a href="/contacto">Contacto</a>
      </nav>

      <div className="footer__bottom">
        <p>© {new Date().getFullYear()} TicketYa. Todos los derechos reservados.</p>
      </div>
    </footer>
  )
}

export default Footer
