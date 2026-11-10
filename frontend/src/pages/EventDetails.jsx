
import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getEventById } from '../services/eventService'
import '../styles/eventDetails.css'

const categoryIcons = {
  RECITAL: '♫',
  THEATER: '✦',
  SPORTS: '⚽',
  CONFERENCE: '✧',
  WORKSHOP: '⌘',
  SEMINAR: '✧',
  FESTIVAL: '♫'
}

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
  const { id } = useParams()

  const [event, setEvent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadEvent() {
      try {
        setLoading(true)
        setError('')

        const data = await getEventById(id)
        setEvent(data)
      } catch (err) {
        console.error('Error al obtener el evento:', err)
        setError('No se pudo cargar el evento.')
      } finally {
        setLoading(false)
      }
    }

    loadEvent()
  }, [id])

  const formatDate = (dateTime) => {
    if (!dateTime) return 'Fecha a confirmar'

    return new Date(dateTime).toLocaleDateString('es-AR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    })
  }

  const formatTime = (dateTime) => {
    if (!dateTime) return 'Horario a confirmar'

    return new Date(dateTime).toLocaleTimeString('es-AR', {
      hour: '2-digit',
      minute: '2-digit'
    }) + ' hs'
  }

  if (loading) {
    return <main className="event-details">Cargando evento...</main>
  }

  if (error || !event) {
    return (
      <main className="event-details">
        <p>{error || 'Evento no encontrado.'}</p>
        <Link to="/event">Volver a eventos</Link>
      </main>
    )
  }

  // Datos de ubicación: pueden venir directamente o dentro de venue
  const address =
    event.address ||
    event.venue?.address ||
    event.venueAddress ||
    ''

  const city =
    event.city ||
    event.venue?.city ||
    event.venueCity ||
    ''

  const location = [address, city]
    .filter(Boolean)
    .join(', ')

  const category = event.type || event.category

  return (
    <main className="event-details">

      <Link to="/event" className="event-details__back">
        ← Volver a eventos
      </Link>

      {/* IMAGEN DEL EVENTO */}
      <section className="event-details__hero">

        {event.image ? (
          <img
            src={event.image}
            alt={event.name}
            className="event-details__image"
          />
        ) : (
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

        <span className="event-details__category">
          {categoryLabels[category] || category || 'Evento'}
        </span>

      </section>

      <div className="event-details__layout">

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

        <aside className="event-details__purchase">

          <h2>Entradas disponibles</h2>
          <p>Elegí el sector que más te guste.</p>

          <div className="event-details__sectors">

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

                  <strong>
                    ${Number(sector.price).toLocaleString('es-AR')}
                  </strong>
                </div>
              ))
            ) : (
              <p>No hay sectores disponibles.</p>
            )}

          </div>

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
