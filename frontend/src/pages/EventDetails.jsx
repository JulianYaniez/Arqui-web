
import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getEventById } from '../services/eventService'
import '../styles/eventDetails.css'

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
        <Link to="/eventos">Volver a eventos</Link>
      </main>
    )
  }

  return (
    <main className="event-details">
      <Link to="/eventos" className="event-details__back">
        ← Volver a eventos
      </Link>

      <section className="event-details__hero">
        <img
          src={event.image || 'https://placehold.co/1200x500?text=Evento'}
          alt={event.name}
          className="event-details__image"
        />

        <span className="event-details__category">
          {event.category}
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
              <span>Lugar</span>
              <strong>{event.city || 'Ubicación a confirmar'}</strong>
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
                      Capacidad: {sector.capacity}
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
