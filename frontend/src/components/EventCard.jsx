
import { Link } from 'react-router-dom'
import '../styles/eventcard.css'

function EventCard({ event }) {
  return (
    <Link
      to={`/eventos/${event.id}`}
      className="event-card"
    >
      <div className="event-card__image-container">
        <img
          src={event.image}
          alt={event.name}
          className="event-card__image"
        />

        <span className="event-card__category">
          {event.category}
        </span>

        {event.status === 'AGOTADO' && (
          <span className="event-card__sold-out">
            Agotado
          </span>
        )}
      </div>

      <div className="event-card__content">
        <h3>{event.name}</h3>

        <div className="event-card__info">
          <span>📅 {event.date}</span>
          <span>📍 {event.location}</span>
        </div>

        <div className="event-card__footer">
          <div className="event-card__price">
            <small>Desde</small>
            <strong>
              ${event.price.toLocaleString('es-AR')}
            </strong>
          </div>

          <span className="event-card__arrow">→</span>
        </div>
      </div>
    </Link>
  )
}

export default EventCard
