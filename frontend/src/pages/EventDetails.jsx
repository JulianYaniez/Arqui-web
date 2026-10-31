
import { Link, useParams } from 'react-router-dom'
import '../styles/eventDetails.css'

function EventDetails() {
  const { id } = useParams()

  // Datos temporales hasta conectar el catalog-service
  const event = {
    id,
    name: 'Festival de Música',
    description:
      'Una noche increíble con música en vivo, artistas invitados y una experiencia única para disfrutar con amigos.',
    category: 'Música',
    organizer: 'Producciones XYZ',
    date: '15 de noviembre de 2026',
    time: '20:00 hs',
    location: 'Estadio Municipal',
    address: 'Av. Principal 123, Tandil',
    status: 'PUBLICADO',
    image: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=1200'
  }

  const sectors = [
    {
      id: 1,
      name: 'Campo',
      price: 25000,
      available: 150
    },
    {
      id: 2,
      name: 'Platea',
      price: 35000,
      available: 80
    },
    {
      id: 3,
      name: 'VIP',
      price: 50000,
      available: 20
    }
  ]

  return (
    <main className="event-details">
      <Link to="/eventos" className="event-details__back">
        ← Volver a eventos
      </Link>

      <section className="event-details__hero">
        <img
          src={event.image}
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
            {event.description}
          </p>

          <div className="event-details__organizer">
            <span>Organizado por</span>
            <strong>{event.organizer}</strong>
          </div>

          <div className="event-details__separator" />

          <h2>Información del evento</h2>

          <div className="event-details__data">
            <div>
              <span>Fecha</span>
              <strong>{event.date}</strong>
            </div>

            <div>
              <span>Horario</span>
              <strong>{event.time}</strong>
            </div>

            <div>
              <span>Lugar</span>
              <strong>{event.location}</strong>
            </div>

            <div>
              <span>Dirección</span>
              <strong>{event.address}</strong>
            </div>
          </div>
        </section>

        <aside className="event-details__purchase">
          <h2>Entradas disponibles</h2>
          <p>Elegí el sector que más te guste.</p>

          <div className="event-details__sectors">
            {sectors.map(sector => (
              <div className="event-details__sector" key={sector.id}>
                <div>
                  <strong>{sector.name}</strong>
                  <span>{sector.available} disponibles</span>
                </div>

                <strong>
                  ${sector.price.toLocaleString('es-AR')}
                </strong>
              </div>
            ))}
          </div>

          <Link
            to={`/eventos/${event.id}/comprar`}
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
