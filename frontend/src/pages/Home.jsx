
import { Link } from 'react-router-dom'
import SearchBar from '../components/SearchBar'
import EventCard from '../components/EventCard'
import '../styles/home.css'

function Home() {
  // Eventos temporales para visualizar la sección
  const featuredEvents = [
    {
      id: 1,
      name: 'Festival de Música',
      category: 'Música',
      date: '15/11/2026',
      location: 'Tandil',
      price: 25000,
      image: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 2,
      name: 'Tech Conference',
      category: 'Tecnología',
      date: '30/11/2026',
      location: 'Centro de Convenciones',
      price: 18000,
      image: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 3,
      name: 'Noche de Teatro',
      category: 'Teatro',
      date: '25/11/2026',
      location: 'Teatro Central',
      price: 12000,
      image: 'https://images.unsplash.com/photo-1503095396549-807759245b35?w=800',
      status: 'PUBLICADO'
    }
  ]

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
          {/*
            Cuando conectemos el backend, reemplazamos
            los eventos temporales por los eventos recibidos.
          */}
          {featuredEvents.map(event => (
            <EventCard key={event.id} event={event} />
          ))}
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
