
function Home() {
  return (
    <main className="home">
      <section className="home__hero">
        <div className="home__hero-content">
          <span className="home__tag">
            Tu próximo plan empieza acá
          </span>

          <h1>
            Viví experiencias <br />
            <span>únicas.</span>
          </h1>

          <p>
            Descubrí eventos, encontrá tus favoritos
            y asegurá tu lugar.
          </p>

          <Link to="/eventos" className="home__button">
            Explorar eventos
          </Link>
        </div>
      </section>

      <section className="home__search">
        <h2>¿Qué estás buscando?</h2>
        <SearchBar />
      </section>

      <section className="home__featured">
        <div className="home__section-header">
          <div>
            <h2>Eventos destacados</h2>
            <p>Descubrí los eventos que no te podés perder.</p>
          </div>

          <Link to="/eventos">Ver todos</Link>
        </div>

        <div className="home__events">
          {/* Acá van los EventCard */}
        </div>
      </section>

      <section className="home__cta">
        <h2>¿Listo para tu próximo evento?</h2>
        <p>Encontrá tu próxima experiencia en TicketYa.</p>
        <Link to="/eventos">Ver eventos</Link>
      </section>
    </main>
  )
}

export default Home
