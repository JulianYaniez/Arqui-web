
import { Link } from 'react-router-dom'
import '../styles/eventcard.css'

function EventCard({ event }) {
  const categoriesMap = {
    RECITAL: 'Música',
    THEATER: 'Teatro',
    SPORTS: 'Deportes',
    CONFERENCE: 'Tecnología',
    WORKSHOP: 'Tecnología',
    SEMINAR: 'Tecnología',
    FESTIVAL: 'Música'
  }

  const categoryIcons = {
    RECITAL: '♫',
    THEATER: '✦',
    SPORTS: '⚽',
    CONFERENCE: '✧',
    WORKSHOP: '⌘',
    SEMINAR: '✧',
    FESTIVAL: '♫'
  }

  const category = categoriesMap[event.type] || 'Otros'

  const price = event.sectors?.length
    ? Math.min(...event.sectors.map(sector => sector.price))
    : 0

  const formattedDate = event.dateTime
    ? new Date(event.dateTime).toLocaleString('es-AR', {
        dateStyle: 'short',
        timeStyle: 'short'
      })
    : 'Fecha a confirmar'

  return (
    <Link
      to={`/event/${event.id}`}
      className="event-card"
    >
      <div className={`event-card__image-container ${!event.image ? 'event-card__image-container--empty' : ''}`}>

        {event.image ? (
          <img
            src={event.image}
            alt={event.name}
            className="event-card__image"
          />
        ) : (
          <div className="event-card__placeholder">
            <span className="event-card__placeholder-decoration">
              {categoryIcons[event.type] || '✦'}
            </span>

            <div className="event-card__placeholder-content">
              <span className="event-card__placeholder-brand">
                TICKETYA
              </span>

              <span className="event-card__placeholder-icon">
                {categoryIcons[event.type] || '✦'}
              </span>

              <span className="event-card__placeholder-category">
                {category}
              </span>
            </div>

            <span className="event-card__placeholder-circle"></span>
          </div>
        )}

        <span className="event-card__category">
          {category}
        </span>

      </div>

      <div className="event-card__content">
        <h3>{event.name}</h3>

        <div className="event-card__info">
          <span>📅 {formattedDate}</span>
          <span>📍 {event.city || 'Ubicación a confirmar'}</span>
        </div>

        <div className="event-card__footer">
          <div className="event-card__price">
            <small>Desde</small>
            <strong>
              ${price.toLocaleString('es-AR')}
            </strong>
          </div>

          <span className="event-card__arrow">→</span>
        </div>
      </div>
    </Link>
  )
}

export default EventCard
