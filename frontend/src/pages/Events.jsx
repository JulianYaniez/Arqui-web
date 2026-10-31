
import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import SearchBar from '../components/SearchBar'
import EventCard from '../components/EventCard'
import '../styles/events.css'

function Events() {
  const [category, setCategory] = useState('Todos')
  const [searchParams] = useSearchParams()

  const search = searchParams.get('search')?.toLowerCase().trim() || ''

  const categories = [
    'Todos',
    'Música',
    'Deportes',
    'Teatro',
    'Tecnología',
    'Otros'
  ]

  // Datos temporales hasta conectar el backend
  const events = [
    {
      id: 1,
      name: 'Festival de Música',
      category: 'Música',
      date: '15/11/2026',
      location: 'Tandil',
      price: 25000,
      image: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 2,
      name: 'Campeonato de Fútbol',
      category: 'Deportes',
      date: '20/11/2026',
      location: 'Estadio Municipal',
      price: 15000,
      image: 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 3,
      name: 'Noche de Teatro',
      category: 'Teatro',
      date: '25/11/2026',
      location: 'Teatro Central',
      price: 12000,
      image: 'https://images.unsplash.com/photo-1503095396549-807759245b35?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 4,
      name: 'Tech Conference',
      category: 'Tecnología',
      date: '30/11/2026',
      location: 'Centro de Convenciones',
      price: 18000,
      image: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800',
      status: 'PUBLICADO'
    },
    {
      id: 5,
      name: 'Festival de Rock',
      category: 'Música',
      date: '05/12/2026',
      location: 'Anfiteatro Municipal',
      price: 30000,
      image: 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=800',
      status: 'AGOTADO'
    },
    {
      id: 6,
      name: 'Torneo de Básquet',
      category: 'Deportes',
      date: '10/12/2026',
      location: 'Polideportivo',
      price: 10000,
      image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800',
      status: 'PUBLICADO'
    }
  ]

  // Filtra por categoría y por texto de búsqueda
  const filteredEvents = events.filter(event => {
    const matchesCategory =
      category === 'Todos' || event.category === category

    const matchesSearch =
      !search ||
      event.name.toLowerCase().includes(search) ||
      event.category.toLowerCase().includes(search) ||
      event.location.toLowerCase().includes(search)

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
            {filteredEvents.length > 0 ? (
              filteredEvents.map(event => (
                <EventCard key={event.id} event={event} />
              ))

              /*
                Cuando conectemos el backend, reemplazaremos
                los datos temporales por los eventos recibidos.
                Se mantiene el mismo map para renderizar las tarjetas.
              */
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
