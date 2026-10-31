
import { useState } from 'react'
import { Link } from 'react-router-dom'
import '../styles/managment.css'

function Managment() {
  // Datos temporales hasta conectar el backend
  const [events, setEvents] = useState([
    {
      id: 1,
      name: 'Festival de Música',
      organizer: 'Producciones XYZ',
      date: '2026-11-15',
      status: 'PUBLICADO'
    },
    {
      id: 2,
      name: 'Noche de Teatro',
      organizer: 'Teatro Central',
      date: '2026-11-20',
      status: 'BORRADOR'
    },
    {
      id: 3,
      name: 'Conferencia Tech',
      organizer: 'Tech Events',
      date: '2026-11-25',
      status: 'CANCELADO'
    }
  ])

  const [showForm, setShowForm] = useState(false)
  const [editingEvent, setEditingEvent] = useState(null)

  const [form, setForm] = useState({
    name: '',
    organizer: '',
    date: '',
    status: 'BORRADOR'
  })

  const publishedEvents = events.filter(
    event => event.status === 'PUBLICADO'
  ).length

  const draftEvents = events.filter(
    event => event.status === 'BORRADOR'
  ).length

  const cancelledEvents = events.filter(
    event => event.status === 'CANCELADO'
  ).length

  const formatDate = (date) => {
    if (!date) return '-'
    return new Date(`${date}T00:00:00`).toLocaleDateString('es-AR')
  }

  const openCreateForm = () => {
    setEditingEvent(null)
    setForm({
      name: '',
      organizer: '',
      date: '',
      status: 'BORRADOR'
    })
    setShowForm(true)
  }

  const openEditForm = (event) => {
    setEditingEvent(event)
    setForm({
      name: event.name,
      organizer: event.organizer,
      date: event.date,
      status: event.status
    })
    setShowForm(true)
  }

  const closeForm = () => {
    setShowForm(false)
    setEditingEvent(null)
  }

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm({
      ...form,
      [name]: value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (editingEvent) {
      // Modificar evento existente
      setEvents(events.map(event =>
        event.id === editingEvent.id
          ? { ...event, ...form }
          : event
      ))
    } else {
      // Crear nuevo evento
      const newEvent = {
        id: Date.now(),
        ...form
      }

      setEvents([...events, newEvent])
    }

    closeForm()
  }

  const handleCancel = (id) => {
    const confirmed = window.confirm(
      '¿Estás seguro de que querés dar de baja este evento?'
    )

    if (!confirmed) return

    setEvents(events.map(event =>
      event.id === id
        ? { ...event, status: 'CANCELADO' }
        : event
    ))
  }

  const handlePublish = (id) => {
    setEvents(events.map(event =>
      event.id === id
        ? { ...event, status: 'PUBLICADO' }
        : event
    ))
  }

  return (
    <main className="management">
      <section className="management__header">
        <div>
          <span className="management__tag">PANEL ADMINISTRATIVO</span>
          <h1>Administración</h1>
          <p>Gestioná los eventos y supervisá la actividad de TicketYa.</p>
        </div>

        <div className="management__header-actions">
          <Link to="/eventos" className="management__button management__button--secondary">
            Ver eventos
          </Link>

          <button
            type="button"
            className="management__button"
            onClick={openCreateForm}
          >
            + Nuevo evento
          </button>
        </div>
      </section>

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

      {showForm && (
        <section className="management__form-container">
          <div className="management__form-header">
            <div>
              <h2>{editingEvent ? 'Modificar evento' : 'Nuevo evento'}</h2>
              <p>
                {editingEvent
                  ? 'Actualizá la información del evento.'
                  : 'Completá los datos para registrar un evento.'}
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

          <form className="management__form" onSubmit={handleSubmit}>
            <div className="management__form-field">
              <label htmlFor="name">Nombre del evento</label>
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

            <div className="management__form-field">
              <label htmlFor="organizer">Organizador</label>
              <input
                id="organizer"
                name="organizer"
                type="text"
                value={form.organizer}
                onChange={handleChange}
                placeholder="Nombre del organizador"
                required
              />
            </div>

            <div className="management__form-field">
              <label htmlFor="date">Fecha</label>
              <input
                id="date"
                name="date"
                type="date"
                value={form.date}
                onChange={handleChange}
                required
              />
            </div>

            <div className="management__form-field">
              <label htmlFor="status">Estado</label>
              <select
                id="status"
                name="status"
                value={form.status}
                onChange={handleChange}
              >
                <option value="BORRADOR">Borrador</option>
                <option value="PUBLICADO">Publicado</option>
              </select>
            </div>

            <div className="management__form-actions">
              <button
                type="button"
                className="management__cancel"
                onClick={closeForm}
              >
                Cancelar
              </button>

              <button type="submit" className="management__button">
                {editingEvent ? 'Guardar cambios' : 'Crear evento'}
              </button>
            </div>
          </form>
        </section>
      )}

      <section className="management__content">
        <div className="management__section-header">
          <div>
            <h2>Gestión de eventos</h2>
            <p>Consultá y administrá los eventos de la plataforma.</p>
          </div>

          <Link to="/eventos">Ver catálogo</Link>
        </div>

        <div className="management__table-container">
          <table className="management__table">
            <thead>
              <tr>
                <th>Evento</th>
                <th>Organizador</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {events.map(event => (
                <tr key={event.id}>
                  <td>{event.name}</td>
                  <td>{event.organizer}</td>
                  <td>{formatDate(event.date)}</td>
                  <td>
                    <span
                      className={`management__status management__status--${event.status.toLowerCase()}`}
                    >
                      {event.status}
                    </span>
                  </td>
                  <td>
                    <div className="management__row-actions">
                      <Link
                        to={`/eventos/${event.id}`}
                        className="management__view"
                      >
                        Ver
                      </Link>

                      <button
                        type="button"
                        className="management__edit"
                        onClick={() => openEditForm(event)}
                        disabled={event.status === 'CANCELADO'}
                      >
                        Modificar
                      </button>

                      {event.status === 'BORRADOR' && (
                        <button
                          type="button"
                          className="management__publish"
                          onClick={() => handlePublish(event.id)}
                        >
                          Publicar
                        </button>
                      )}

                      {event.status !== 'CANCELADO' && (
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
              ))}

              {events.length === 0 && (
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

          <Link to="/perfil" className="management__action">
            <span>Mi perfil</span>
            <small>Consultar información de cuenta</small>
          </Link>
        </div>
      </section>
    </main>
  )
}

export default Managment
