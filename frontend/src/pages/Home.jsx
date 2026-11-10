
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import SearchBar from '../components/SearchBar'
import EventCard from '../components/EventCard'
import { getEvents } from '../services/eventService'
import '../styles/home.css'

function Home() {
  const [featuredEvents, setFeaturedEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadEvents = async () => {
      try {
        setLoading(true)
        setError('')

        const data = await getEvents()

        // Adaptarse tanto a una lista como a una respuesta paginada
        const events = Array.isArray(data)
          ? data
          : data.content || data.events || []

        // Mostrar únicamente eventos publicados
        const publishedEvents = events.filter(
          event => event.status === 'PUBLISHED'
        )

        setFeaturedEvents(publishedEvents.slice(0, 3))
      } catch (error) {
        console.error('Error cargando eventos destacados:', error)
        setError('No se pudieron cargar los eventos.')
      } finally {
        setLoading(false)
      }
    }

    loadEvents()
  }, [])

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

          <Link to="/event" className="home__button">
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

          <Link to="/event">Ver todos</Link>
        </div>

        <div className="home__events">
          {loading ? (
            <p>Cargando eventos...</p>
          ) : error ? (
            <p>{error}</p>
          ) : featuredEvents.length > 0 ? (
            featuredEvents.map(event => (
              <EventCard key={event.id} event={event} />
            ))
          ) : (
            <p>Por el momento no hay eventos destacados.</p>
          )}
        </div>
      </section>

      <section className="home__cta">
        <h2>¿Listo para tu próximo evento?</h2>
        <p>Encontrá tu próxima experiencia en TicketYa.</p>
        <Link to="/event">Ver eventos</Link>
      </section>
    </main>
  )
}

export default Home
