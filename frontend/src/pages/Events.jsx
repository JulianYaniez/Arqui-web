import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import SearchBar from '../components/SearchBar'
import EventCard from '../components/EventCard'

import { getEvents } from '../services/eventService'

import '../styles/events.css'

function Events() {

  const getCategory = (type) => {
    const categoriesMap = {
      RECITAL: 'Música',
      THEATER: 'Teatro',
      SPORTS: 'Deportes',
      CONFERENCE: 'Tecnología',
      WORKSHOP: 'Tecnología',
      SEMINAR: 'Tecnología',
      FESTIVAL: 'Música'
    }

    return categoriesMap[type] || 'Otros'
  }

  const [category, setCategory] = useState('Todos')
  const [searchParams] = useSearchParams()

  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  if (user) {
    console.log('Hay una sesión guardada')
    console.log(user.email)
    console.log(user.role)
  } else {
    console.log('No hay sesión')
  }

  useEffect(() => {
    async function fetchEvents() {
      try {
        setLoading(true)

        const data = await getEvents()

        setEvents(data.content)

      } catch (error) {
        console.error('Error cargando eventos:', error)
        setError(error.message)
      } finally {
        setLoading(false)
      }
    }

    fetchEvents()
  }, [])

  const search = searchParams.get('search')?.toLowerCase().trim() || ''

  const categories = [
    'Todos',
    'Música',
    'Deportes',
    'Teatro',
    'Tecnología',
    'Otros'
  ]

  // Filtra por categoría y por texto de búsqueda
  const filteredEvents = events.filter(event => {
    const eventCategory = getCategory(event.type)

    const matchesCategory =
      category === 'Todos' || eventCategory === category

    const matchesSearch =
      !search ||
      event.name?.toLowerCase().includes(search) ||
      eventCategory.toLowerCase().includes(search) ||
      event.city?.toLowerCase().includes(search)

    return matchesCategory && matchesSearch
  })

  return (
    <main className="events">
      <section className="events__header">
        <h1>Explorá eventos</h1>
        <p>
          Descubrí experiencias únicas y encontrá tu próximo plan.
        </p>
      </section>

      <section className="events__search">
        <SearchBar />
      </section>

      <section className="events__content">
        <div className="events__filters">
          <h2>Categorías</h2>

          <div className="events__categories">
            {categories.map((item) => (
              <button
                key={item}
                className={
                  category === item
                    ? 'events__category events__category--active'
                    : 'events__category'
                }
                onClick={() => setCategory(item)}
              >
                {item}
              </button>
            ))}
          </div>
        </div>

        <div className="events__list">
          <div className="events__list-header">
            <h2>
              {search ? `Resultados para "${search}"` : 'Todos los eventos'}
            </h2>
            <span>{filteredEvents.length} eventos</span>
          </div>

          <div className="events__grid">
            {loading ? (
              <p>Cargando eventos...</p>
            ) : error ? (
              <p className="events__empty">{error}</p>
            ) : filteredEvents.length > 0 ? (
              filteredEvents.map(event => (
                <EventCard key={event.id} event={event} />
              ))
            ) : (
              <p className="events__empty">
                No hay eventos disponibles por el momento.
              </p>
            )}
          </div>
        </div>
      </section>
    </main>
  )
}

export default Events
