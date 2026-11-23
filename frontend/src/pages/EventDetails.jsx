import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getEventById } from '../services/eventService'
import '../styles/eventDetails.css'

// Icons associated with each event category.
// Used when the event does not have an image.
const categoryIcons = {
  RECITAL: '♫',
  THEATER: '✦',
  SPORTS: '⚽',
  CONFERENCE: '✧',
  WORKSHOP: '⌘',
  SEMINAR: '✧',
  FESTIVAL: '♫'
}

// Translates event categories into user-friendly labels.
const categoryLabels = {
  RECITAL: 'Recital',
  THEATER: 'Teatro',
  SPORTS: 'Deportes',
  CONFERENCE: 'Conferencia',
  WORKSHOP: 'Workshop',
  SEMINAR: 'Seminario',
  FESTIVAL: 'Festival'
}

function EventDetails() {
  // Retrieves the event ID from the current URL.
  const { id } = useParams()

  // Stores the event data retrieved from the backend.
  const [event, setEvent] = useState(null)

  // Indicates whether the event data is still being loaded.
  const [loading, setLoading] = useState(true)

  // Stores an error message if the request fails.
  const [error, setError] = useState('')

  // Runs whenever the event ID in the URL changes.
  useEffect(() => {
    async function loadEvent() {
      try {
        // Resets the states before making a new request.
        setLoading(true)
        setError('')

        // Requests the event data from the backend.
        const data = await getEventById(id)
        setEvent(data)
      } catch (err) {
        // Logs the error and stores a message to display to the user.
        console.error('Error al obtener el evento:', err)
        setError('No se pudo cargar el evento.')
      } finally {
        // Runs whether the request succeeds or fails.
        setLoading(false)
      }
    }

    loadEvent()
  }, [id])

  // Formats the event date using the Argentine locale.
  const formatDate = (dateTime) => {
    if (!dateTime) return 'Fecha a confirmar'

    return new Date(dateTime).toLocaleDateString('es-AR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    })
  }

  // Formats the event time using the 24-hour format.
  const formatTime = (dateTime) => {
    if (!dateTime) return 'Horario a confirmar'

    return new Date(dateTime).toLocaleTimeString('es-AR', {
      hour: '2-digit',
      minute: '2-digit'
    }) + ' hs'
  }

  // Displays a loading message while waiting for the backend response.
  if (loading) {
    return <main className="event-details">Cargando evento...</main>
  }

  // Displays an error message if the request fails
  // or if the event does not exist.
  if (error || !event) {
    return (
      <main className="event-details">
        <p>{error || 'Evento no encontrado.'}</p>
        <Link to="/event">Volver a eventos</Link>
      </main>
    )
  }

  // The address may be returned directly or inside the venue object.
  const address =
    event.address ||
    event.venue?.address ||
    event.venueAddress ||
    ''

  // The city may also be returned using different property names.
  const city =
    event.city ||
    event.venue?.city ||
    event.venueCity ||
    ''

  // Combines the address and city, ignoring empty values.
  const location = [address, city]
    .filter(Boolean)
    .join(', ')

  // Supports both possible property names for the event category.
  const category = event.type || event.category

  return (
    <main className="event-details">

      <Link to="/event" className="event-details__back">
        ← Volver a eventos
      </Link>

      {/* Displays the event image or a placeholder if no image is available. */}
      <section className="event-details__hero">

        {event.image ? (
          <img
            src={event.image}
            alt={event.name}
            className="event-details__image"
          />
        ) : (
          // Placeholder containing the platform name,
          // category icon and event category.
          <div className="event-details__placeholder">
            <div className="event-details__placeholder-decoration event-details__placeholder-decoration--one" />
            <div className="event-details__placeholder-decoration event-details__placeholder-decoration--two" />

            <div className="event-details__placeholder-content">
              <span className="event-details__placeholder-brand">
                TICKETYA
              </span>

              <span className="event-details__placeholder-icon">
                {categoryIcons[category] || '✦'}
              </span>

              <span className="event-details__placeholder-category">
                {categoryLabels[category] || category || 'EVENTO'}
              </span>
            </div>
          </div>
        )}

        {/* Displays the event category over the image. */}
        <span className="event-details__category">
          {categoryLabels[category] || category || 'Evento'}
        </span>

      </section>

      <div className="event-details__layout">

        {/* Main section containing the event information. */}
        <section className="event-details__information">

          <h1>{event.name}</h1>

          <p className="event-details__description">
            {event.description || 'Sin descripción disponible.'}
          </p>

          <div className="event-details__organizer">
            <span>Organizado por</span>
            <strong>{event.organizerId || 'Organizador'}</strong>
          </div>

          <div className="event-details__separator" />

          <h2>Información del evento</h2>

          <div className="event-details__data">

            <div>
              <span>Fecha</span>
              <strong>{formatDate(event.dateTime)}</strong>
            </div>

            <div>
              <span>Horario</span>
              <strong>{formatTime(event.dateTime)}</strong>
            </div>

            <div>
              <span>Dirección</span>
              <strong>
                {location || 'Ubicación a confirmar'}
              </strong>
            </div>

            <div>
              <span>Estado</span>
              <strong>{event.statusLabel || event.status}</strong>
            </div>

          </div>

        </section>

        {/* Side panel containing ticket sectors and prices. */}
        <aside className="event-details__purchase">

          <h2>Entradas disponibles</h2>
          <p>Elegí el sector que más te guste.</p>

          <div className="event-details__sectors">

            {/* Renders each available sector or displays a message
                if the event has no sectors. */}
            {event.sectors?.length > 0 ? (
              event.sectors.map(sector => (
                <div
                  className="event-details__sector"
                  key={sector.sectorId}
                >
                  <div>
                    <strong>{sector.name}</strong>
                    <span>
                      Capacidad: {sector.capacity ?? '-'}
                    </span>
                  </div>

                  {/* Formats the price using the Argentine locale. */}
                  <strong>
                    ${Number(sector.price).toLocaleString('es-AR')}
                  </strong>
                </div>
              ))
            ) : (
              <p>No hay sectores disponibles.</p>
            )}

          </div>

          {/* Navigates to the purchase page for the current event. */}
          <Link
            to={`/event/${event.id}/buy`}
            className="event-details__buy"
          >
            Comprar entradas
          </Link>

          <small>La disponibilidad puede cambiar.</small>

        </aside>

      </div>

    </main>
  )
}

export default EventDetails