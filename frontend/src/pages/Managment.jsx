
import { useState, useEffect } from 'react'
import { Link, Navigate } from 'react-router-dom'

import VenueForm from '../components/VenueForm'
import '../styles/managment.css'

import {
  getEvents,
  createEvent,
  cancelEvent
} from '../services/eventService'
import { getVenues } from '../services/venueService'

function Managment() {

  // Recuperar usuario de localStorage
  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  // Verificar permisos
  const isAdmin = user?.role === 'ADMIN'

  const [showVenueForm, setShowVenueForm] = useState(false)

  // Estados de eventos
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  // Estados del formulario
  const [showForm, setShowForm] = useState(false)

  const [form, setForm] = useState({
    name: '',
    description: '',
    type: 'RECITAL',
    dateTime: ''
  })

  const getMinDateTime = () => {
    const now = new Date()
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset())

    return now.toISOString().slice(0, 16)
  }

  // Estados de Venue
  const [venues, setVenues] = useState([])
  const [venue, setVenue] = useState(null)
  const [loadingVenues, setLoadingVenues] = useState(false)

  // Sectores seleccionados
  const [selectedSectors, setSelectedSectors] = useState([])

  const loadVenues = async () => {
    try {
      setLoadingVenues(true)

      const data = await getVenues()

      setVenues(data)
    } catch (error) {
      console.error('Error cargando lugares:', error)
      setError('No se pudieron cargar los lugares disponibles.')
    } finally {
      setLoadingVenues(false)
    }
  }

  // Obtener eventos del backend
  const loadEvents = async () => {
    try {
      setLoading(true)

      const data = await getEvents()

      setEvents(
        Array.isArray(data)
          ? data
          : data.content || []
      )

    } catch (error) {
      console.error('Error cargando eventos:', error)
      setError('No se pudieron cargar los eventos.')
    } finally {
      setLoading(false)
    }
  }

  // Cargar eventos al entrar
  useEffect(() => {
    if (!isAdmin) return

    loadEvents()
    loadVenues()
  }, [isAdmin])

  // Estadísticas
  const publishedEvents = events.filter(
    event => event.status === 'PUBLISHED'
  ).length

  const draftEvents = events.filter(
    event => event.status === 'DRAFT'
  ).length

  const cancelledEvents = events.filter(
    event => event.status === 'CANCELLED'
  ).length

  // Formatear fecha
  const formatDate = (date) => {
    if (!date) return '-'

    return new Date(date).toLocaleString('es-AR', {
      dateStyle: 'short',
      timeStyle: 'short'
    })
  }

  // Abrir formulario
  const openCreateForm = () => {
    setForm({
      name: '',
      description: '',
      type: 'RECITAL',
      dateTime: ''
    })

    setVenue(null)
    setSelectedSectors([])
    setError('')
    setShowForm(true)
  }

  // Cerrar formulario
  const closeForm = () => {
    setShowForm(false)
    setVenue(null)
    setSelectedSectors([])
    setError('')
  }

  // Modificar campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target

    setForm(prev => ({
      ...prev,
      [name]: value
    }))
  }

  // Seleccionar o deseleccionar sector
  const handleSectorChange = (sector, checked) => {

    if (checked) {
      setSelectedSectors(prev => [
        ...prev,
        {
          sectorId: sector.id,
          price: ''
        }
      ])

    } else {
      setSelectedSectors(prev =>
        prev.filter(item => item.sectorId !== sector.id)
      )
    }
  }

  // Asignar precio a un sector
  const handleSectorPriceChange = (sectorId, price) => {

    setSelectedSectors(prev =>
      prev.map(sector =>
        sector.sectorId === sectorId
          ? { ...sector, price }
          : sector
      )
    )
  }

  // Crear evento
  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      setError('')

      if (!user?.id) {
        setError('No se encontró el ID del usuario.')
        return
      }

      if (!venue) {
        setError('Primero tenés que seleccionar un lugar.')
        return
      }

      if (selectedSectors.length === 0) {
        setError('Seleccioná al menos un sector.')
        return
      }

      const invalidPrice = selectedSectors.some(
        sector =>
          sector.price === '' ||
          !Number.isFinite(Number(sector.price)) ||
          Number(sector.price) <= 0
      )

      if (invalidPrice) {
        setError('Ingresá un precio válido para cada sector.')
        return
      }

      const eventData = {
        name: form.name,
        description: form.description,
        type: form.type,
        dateTime: form.dateTime,
        organizerId: user.id,
        participants: [user.id],
        venueId: venue.id,
        sectors: selectedSectors.map(sector => ({
          sectorId: sector.sectorId,
          price: Number(sector.price)
        }))
      }

      console.log('Evento a enviar:', eventData)

      setSubmitting(true)

      console.log('Fecha enviada:', eventData.dateTime)
      console.log('Hora actual navegador:', new Date().toString())
      console.log('Hora actual Argentina:', 
        new Date().toLocaleString('es-AR', {
          timeZone: 'America/Argentina/Buenos_Aires'
        })
      )

      await createEvent(eventData)

      await loadEvents()

      closeForm()

    } catch (error) {
      console.error('Error creando evento:', error)
      setError(error.message || 'No se pudo crear el evento.')
    } finally {
      setSubmitting(false)
    }
  }

  // Cancelar evento
  const handleCancel = async (id) => {

    const confirmed = window.confirm(
      '¿Estás seguro de que querés dar de baja este evento?'
    )

    if (!confirmed) return

    try {
      setError('')

      await cancelEvent(id)

      await loadEvents()

    } catch (error) {
      console.error('Error cancelando evento:', error)
      setError(error.message || 'No se pudo cancelar el evento.')
    }
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (!isAdmin) {
    return <Navigate to="/" replace />
  }

  return (
    <main className="management">

      {/* HEADER */}
      <section className="management__header">

        <div>
          <span className="management__tag">
            PANEL ADMINISTRATIVO
          </span>

          <h1>Administración</h1>

          <p>
            Gestioná los eventos y supervisá la actividad de TicketYa.
          </p>
        </div>

        <div className="management__header-actions">

          <Link
            to="/event"
            className="management__button management__button--secondary"
          >
            Ver eventos
          </Link>

          <button
            type="button"
            className="management__button"
            onClick={openCreateForm}
          >
            + Nuevo evento
          </button>

          <button
            type="button"
            className="management__button management__button--secondary"
            onClick={() => setShowVenueForm(true)}
          >
            + Nuevo lugar
          </button>

        </div>

      </section>

      {/* ERROR GENERAL */}
      {error && !showForm && (
        <p className="management__error">{error}</p>
      )}

      {/* FORMULARIO CREAR VENUE */}
      {showVenueForm && (
        <VenueForm
          onClose={() => setShowVenueForm(false)}
          onCreated={() => {
            loadVenues()
          }}
        />
      )}

      {/* ESTADÍSTICAS */}
      <section className="management__stats">

        <article className="management__stat">
          <span>Total de eventos</span>
          <strong>{events.length}</strong>
          <small>Eventos registrados</small>
        </article>

        <article className="management__stat">
          <span>Eventos publicados</span>
          <strong>{publishedEvents}</strong>
          <small>Disponibles para comprar</small>
        </article>

        <article className="management__stat">
          <span>Eventos pendientes</span>
          <strong>{draftEvents}</strong>
          <small>Esperando publicación</small>
        </article>

        <article className="management__stat">
          <span>Eventos cancelados</span>
          <strong>{cancelledEvents}</strong>
          <small>Cancelaciones registradas</small>
        </article>

      </section>

      {/* FORMULARIO */}
      {showForm && (

        <section className="management__form-container">

          <div className="management__form-header">

            <div>
              <h2>Nuevo evento</h2>

              <p>
                Completá los datos para registrar un evento.
              </p>
            </div>

            <button
              type="button"
              className="management__close"
              onClick={closeForm}
              aria-label="Cerrar formulario"
            >
              ×
            </button>

          </div>

          <form
            className="management__form"
            onSubmit={handleSubmit}
          >

            {/* NOMBRE */}
            <div className="management__form-field">

              <label htmlFor="name">
                Nombre del evento
              </label>

              <input
                id="name"
                name="name"
                type="text"
                value={form.name}
                onChange={handleChange}
                placeholder="Ej: Festival de Música"
                required
              />

            </div>

            {/* DESCRIPCIÓN */}
            <div className="management__form-field management__form-field--full">
              <label htmlFor="description">
                Descripción del evento
              </label>

              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Contá de qué se trata el evento, qué van a encontrar los asistentes..."
                rows={5}
                maxLength={1000}
                required
              />

              <small className="management__field-hint">
                Máximo 1000 caracteres.
              </small>
            </div>

            {/* TIPO */}
            <div className="management__form-field">

              <label htmlFor="type">
                Tipo de evento
              </label>

              <select
                id="type"
                name="type"
                value={form.type}
                onChange={handleChange}
                required
              >
                <option value="RECITAL">Recital</option>
                <option value="THEATER">Teatro</option>
                <option value="SPORTS">Deportes</option>
                <option value="CONFERENCE">Conferencia</option>
                <option value="WORKSHOP">Workshop</option>
                <option value="SEMINAR">Seminario</option>
                <option value="FESTIVAL">Festival</option>
              </select>

            </div>

            {/* FECHA */}
            <div className="management__form-field">

              <label htmlFor="dateTime">
                Fecha y hora
              </label>

              <input
                id="dateTime"
                name="dateTime"
                type="datetime-local"
                value={form.dateTime}
                min={getMinDateTime()}
                onChange={handleChange}
                required
              />

            </div>

            {/* VENUE */}
            {/* SELECCIONAR VENUE */}
            <div className="management__form-field">
            <label htmlFor="venue">Lugar del evento</label>

            <select
              id="venue"
              value={venue?.id || ''}
              onChange={(e) => {
                const selectedVenue = venues.find(
                  item => item.id === e.target.value
                )

                setVenue(selectedVenue || null)
                setSelectedSectors([])
                setError('')
              }}
              disabled={loadingVenues}
              required
            >
              <option value="">
                {loadingVenues
                  ? 'Cargando lugares...'
                  : 'Seleccioná un lugar'}
              </option>

              {venues.map(item => (
                <option key={item.id} value={item.id}>
                  {item.name} - {item.city}
                </option>
              ))}
            </select>

            {!loadingVenues && venues.length === 0 && (
              <p>No hay lugares registrados. Primero creá un Venue.</p>
            )}
          </div>

            {/* INFORMACIÓN DEL VENUE */}
            {venue && (

              <div className="management__form-field">

                <h3>{venue.name}</h3>

                <p>{venue.city}</p>

                <h4>Sectores disponibles</h4>

                {venue.sectors?.length > 0 ? (

                  venue.sectors.map(sector => {

                    const selected = selectedSectors.some(
                      item => item.sectorId === sector.id
                    )

                    const selectedSector = selectedSectors.find(
                      item => item.sectorId === sector.id
                    )

                    return (

                      <div
                        key={sector.id}
                        className="management__sector"
                      >

                        <label>
                          <input
                            type="checkbox"
                            checked={selected}
                            onChange={(e) =>
                              handleSectorChange(
                                sector,
                                e.target.checked
                              )
                            }
                          />

                          {sector.name} - Capacidad: {sector.capacity}
                        </label>

                        {selected && (

                          <input
                            type="number"
                            min="0.01"
                            step="0.01"
                            placeholder="Precio del sector"
                            value={selectedSector.price}
                            onChange={(e) =>
                              handleSectorPriceChange(
                                sector.id,
                                e.target.value
                              )
                            }
                            required
                          />

                        )}

                      </div>

                    )

                  })

                ) : (

                  <p>Este lugar no tiene sectores disponibles.</p>

                )}

              </div>

            )}

            {/* ERROR */}
            {error && (
              <p className="management__error">
                {error}
              </p>
            )}

            {/* ACCIONES */}
            <div className="management__form-actions">

              <button
                type="button"
                className="management__cancel"
                onClick={closeForm}
                disabled={submitting}
              >
                Cancelar
              </button>

              <button
                type="submit"
                className="management__button"
                disabled={submitting}
              >
                {submitting ? 'Creando...' : 'Crear evento'}
              </button>

            </div>

          </form>

        </section>

      )}

      {/* TABLA DE EVENTOS */}
      <section className="management__content">

        <div className="management__section-header">

          <div>
            <h2>Gestión de eventos</h2>

            <p>
              Consultá y administrá los eventos de la plataforma.
            </p>
          </div>

          <Link to="/event">
            Ver catálogo
          </Link>

        </div>

        <div className="management__table-container">

          <table className="management__table">

            <thead>
              <tr>
                <th>Evento</th>
                <th>Lugar</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>

              {loading ? (

                <tr>
                  <td colSpan="5" className="management__empty">
                    Cargando eventos...
                  </td>
                </tr>

              ) : events.length > 0 ? (

                events.map(event => (

                  <tr key={event.id}>
                    <td>{event.name}</td>

                    <td>{event.city || event.location || '-'}</td>

                    <td>{formatDate(event.dateTime || event.date)}</td>

                    <td>
                      <span
                        className={`management__status management__status--${event.status.toLowerCase()}`}
                      >
                        {event.statusLabel || event.status}
                      </span>
                    </td>

                    <td>
                      <div className="management__row-actions">
                        <Link
                          to={`/event/${event.id}`}
                          className="management__view"
                        >
                          Ver
                        </Link>

                        {event.status !== 'CANCELLED' &&
                          event.status !== 'FINISHED' && (
                            <button
                              type="button"
                              className="management__delete"
                              onClick={() => handleCancel(event.id)}
                            >
                              Dar de baja
                            </button>
                          )}
                      </div>
                    </td>
                  </tr>

                ))

              ) : (

                <tr>
                  <td colSpan="5" className="management__empty">
                    Todavía no hay eventos registrados.
                  </td>
                </tr>

              )}

            </tbody>

          </table>

        </div>

      </section>

      {/* ACCESOS RÁPIDOS */}
      <section className="management__quick-actions">

        <h2>Accesos rápidos</h2>

        <div className="management__actions">

          <button
            type="button"
            className="management__action"
            onClick={openCreateForm}
          >
            <span>Crear evento</span>
            <small>Registrar un nuevo evento</small>
          </button>

          <Link to="/profile" className="management__action">
            <span>Mi perfil</span>
            <small>Consultar información de cuenta</small>
          </Link>

        </div>

      </section>

    </main>
  )
}

export default Managment
