import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import SearchBar from '../components/SearchBar'
import EventCard from '../components/EventCard'

import { getEvents } from '../services/eventService'

import '../styles/events.css'

function Events() {

  // Maps backend event types to the categories displayed in the UI.
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

  // Stores the currently selected category filter.
  const [category, setCategory] = useState('Todos')

  // Retrieves search parameters from the URL.
  const [searchParams] = useSearchParams()

  // Stores the events retrieved from the backend.
  const [events, setEvents] = useState([])

  // Indicates whether the events are being loaded.
  const [loading, setLoading] = useState(true)

  // Stores an error message if the request fails.
  const [error, setError] = useState(null)

  // Retrieves the user session stored in the browser.
  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  // Logs the stored user information for debugging purposes.
  if (user) {
    console.log('Hay una sesión guardada')
    console.log(user.email)
    console.log(user.role)
  } else {
    console.log('No hay sesión')
  }

  // Fetches the events when the component is mounted.
  useEffect(() => {
    async function fetchEvents() {
      try {
        setLoading(true)

        // Retrieves the events from the backend.
        const data = await getEvents()

        // Supports responses containing either an array directly
        // or an object with events inside content or events.
        const eventList = Array.isArray(data)
          ? data
          : data.content || data.events || []

        // Only published events are displayed,
        // regardless of the user's role.
        const publishedEvents = eventList.filter(
          event => event.status === 'PUBLISHED'
        )

        setEvents(publishedEvents)

      } catch (error) {
        // Stores the error message to display it in the UI.
        console.error('Error cargando eventos:', error)
        setError(error.message)
      } finally {
        // Stops the loading state after the request finishes.
        setLoading(false)
      }
    }

    fetchEvents()
  }, [])

  // Retrieves the search term from the URL,
  // converts it to lowercase and removes extra spaces.
  const search = searchParams.get('search')?.toLowerCase().trim() || ''

  // Available categories for the filtering buttons.
  const categories = [
    'Todos',
    'Música',
    'Deportes',
    'Teatro',
    'Tecnología',
    'Otros'
  ]

  // Filters events by the selected category and search term.
  const filteredEvents = events.filter(event => {
    const eventCategory = getCategory(event.type)

    const matchesCategory =
      category === 'Todos' || eventCategory === category

    // Checks whether the search term matches the event name,
    // category or city.
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
            {/* Renders a button for each category.
                The selected category receives an active CSS class. */}
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
            {/* Displays the loading state, an error message,
                the matching events or an empty-state message. */}
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