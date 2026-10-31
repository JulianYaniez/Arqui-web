
import { useState } from 'react'
import SearchBar from '../components/SearchBar'
// import EventCard from '../components/EventCard'
import '../styles/events.css'

function Events() {
  const [category, setCategory] = useState('Todos')

  const categories = [
    'Todos',
    'Música',
    'Deportes',
    'Teatro',
    'Tecnología',
    'Otros'
  ]

  // Datos temporales hasta conectar el backend
  const events = []

  const filteredEvents = category === 'Todos'
    ? events
    : events.filter(event => event.category === category)

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
            <h2>Todos los eventos</h2>
            <span>{filteredEvents.length} eventos</span>
          </div>

          {/* <div className="events__grid">
            {filteredEvents.length > 0 ? (
              filteredEvents.map(event => (
                <EventCard key={event.id} event={event} />
              ))
            ) : (
              <p className="events__empty">
                No hay eventos disponibles por el momento.
              </p>
            )}
          </div> */}
        </div>
      </section>
    </main>
  )
}

export default Events
