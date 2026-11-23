
import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { apiFetch } from '../services/api'
import '../styles/myTickets.css'

const API_URL = '/api/ticketing/reservation'

function MyTickets() {
  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  const [tickets, setTickets] = useState([])
  const [selectedTicket, setSelectedTicket] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user) return

    const loadTickets = async () => {
      try {
        setLoading(true)
        setError('')

        const data = await apiFetch(`${API_URL}/my-tickets`)

        const reservations = Array.isArray(data) ? data : []

        const ticketList = reservations.flatMap(reservation =>
          (reservation.tickets || []).map(ticket => ({
            ...ticket,
            reservationId: reservation.reservationId,
            eventId: reservation.eventId,
            reservationStatus: reservation.status
          }))
        )

        setTickets(ticketList)
      } catch (error) {
        console.error('Error cargando entradas:', error)
        setError('No se pudieron cargar tus entradas.')
      } finally {
        setLoading(false)
      }
    }

    loadTickets()
  }, [])

  const handleShowQr = async (ticket) => {
    try {
      const response = await apiFetch(
        `${API_URL}/${ticket.reservationId}/ticket/${ticket.id}/qr`
      )

      setSelectedTicket({
        ...ticket,
        qrImage: response.qrImage
      })
    } catch (error) {
      console.error('Error obteniendo QR:', error)
      setError('No se pudo obtener el código QR.')
    }
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  const totalTickets = tickets.length

  const handleCloseQr = () => {
    setSelectedTicket(null)
  }

  return (
    <main className="my-tickets">

      <section className="my-tickets__header">
        <span className="my-tickets__tag">MI CUENTA</span>
        <h1>Mis entradas</h1>
        <p>
          Consultá tus compras y preparate para disfrutar tus próximos eventos.
        </p>
      </section>

      <section className="my-tickets__summary">
        <div>
          <span>Entradas adquiridas</span>
          <strong>{totalTickets}</strong>
        </div>

        <div>
          <span>Reservas realizadas</span>
          <strong>
            {new Set(tickets.map(ticket => ticket.reservationId)).size}
          </strong>
        </div>
      </section>

      <section className="my-tickets__content">

        <div className="my-tickets__section-header">
          <div>
            <h2>Mis compras</h2>
            <p>Estas son las entradas asociadas a tu cuenta.</p>
          </div>

          <Link to="/event">Explorar eventos</Link>
        </div>

        <div className="my-tickets__list">

          {loading ? (
            <p>Cargando tus entradas...</p>
          ) : error ? (
            <p>{error}</p>
          ) : tickets.length > 0 ? (
            tickets.map(ticket => (
              <article className="ticket-card" key={ticket.id}>

                <div className="ticket-card__info">

                  <div className="ticket-card__header">
                    <h3>Evento: {ticket.eventId}</h3>

                    <span className="ticket-card__status">
                      {ticket.status}
                    </span>
                  </div>

                  <div className="ticket-card__details">

                    <div>
                      <span>Sector</span>
                      <strong>{ticket.sectorId}</strong>
                    </div>

                    {ticket.isNumbered && (
                      <>
                        <div>
                          <span>Fila</span>
                          <strong>{ticket.rowNumber}</strong>
                        </div>

                        <div>
                          <span>Asiento</span>
                          <strong>{ticket.seatNumber}</strong>
                        </div>
                      </>
                    )}

                    <div>
                      <span>Tipo</span>
                      <strong>
                        {ticket.isNumbered ? 'Numerada' : 'Sin numerar'}
                      </strong>
                    </div>

                    <div>
                      <span>ID de entrada</span>
                      <strong>{ticket.id}</strong>
                    </div>

                  </div>
                </div>

                <div className="ticket-card__actions">
                  <button
                    type="button"
                    className="ticket-card__qr"
                    onClick={() => handleShowQr(ticket)}
                  >
                    Ver código QR
                  </button>
                </div>

              </article>
            ))
          ) : (
            <div className="my-tickets__empty">
              <h3>Todavía no tenés entradas</h3>
              <p>
                Cuando compres una entrada, aparecerá en esta sección.
              </p>

              <Link to="/event">Explorar eventos</Link>
            </div>
          )}

        </div>
      </section>

      {selectedTicket && (
        <div
          className="ticket-qr__overlay"
          onClick={handleCloseQr}
        >
          <section
            className="ticket-qr__modal"
            onClick={e => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            aria-labelledby="ticket-qr-title"
          >
            <button
              type="button"
              className="ticket-qr__close"
              onClick={handleCloseQr}
              aria-label="Cerrar"
            >
              ×
            </button>

            <span className="my-tickets__tag">TICKETYA</span>

            <h2 id="ticket-qr-title">Tu entrada</h2>

            <p className="ticket-qr__event">
              Evento: {selectedTicket.eventId}
            </p>

            <div className="ticket-qr__code">
              <img
                src={
                  selectedTicket.qrImage.startsWith('data:')
                    ? selectedTicket.qrImage
                    : `data:image/png;base64,${selectedTicket.qrImage}`
                }
                alt="Código QR de la entrada"
                width="220"
                height="220"
              />
            </div>

            <div className="ticket-qr__details">
              <strong>Sector: {selectedTicket.sectorId}</strong>

              {selectedTicket.isNumbered && (
                <span>
                  Fila {selectedTicket.rowNumber} ·
                  Asiento {selectedTicket.seatNumber}
                </span>
              )}

              <small>Código: {selectedTicket.id}</small>
            </div>

            <p className="ticket-qr__notice">
              Presentá este código QR en el ingreso del evento.
            </p>

            <button
              type="button"
              className="ticket-qr__button"
              onClick={handleCloseQr}
            >
              Cerrar
            </button>
          </section>
        </div>
      )}

    </main>
  )
}

export default MyTickets
