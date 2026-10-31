
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { QRCodeSVG } from 'qrcode.react'
import '../styles/myTickets.css'

function MyTickets() {
  // Datos temporales hasta conectar el backend
  const tickets = [
    {
      id: 1,
      event: 'Festival de Música',
      date: '15/11/2026',
      time: '20:00',
      location: 'Estadio Municipal',
      sector: 'Campo',
      quantity: 2,
      status: 'VENDIDA',
      qrCode: 'TICKETYA-TICKET-001'
    },
    {
      id: 2,
      event: 'Noche de Teatro',
      date: '20/11/2026',
      time: '21:00',
      location: 'Teatro Central',
      sector: 'Platea',
      quantity: 1,
      status: 'VENDIDA',
      qrCode: 'TICKETYA-TICKET-002'
    }
  ]

  const [selectedTicket, setSelectedTicket] = useState(null)

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
          <strong>
            {tickets.reduce((total, ticket) => total + ticket.quantity, 0)}
          </strong>
        </div>

        <div>
          <span>Eventos próximos</span>
          <strong>{tickets.length}</strong>
        </div>
      </section>

      <section className="my-tickets__content">
        <div className="my-tickets__section-header">
          <div>
            <h2>Mis compras</h2>
            <p>Estas son las entradas asociadas a tu cuenta.</p>
          </div>

          <Link to="/eventos">Explorar eventos</Link>
        </div>

        <div className="my-tickets__list">
          {tickets.length > 0 ? (
            tickets.map(ticket => (
              <article className="ticket-card" key={ticket.id}>
                <div className="ticket-card__info">
                  <div className="ticket-card__header">
                    <h3>{ticket.event}</h3>

                    <span className="ticket-card__status">
                      {ticket.status}
                    </span>
                  </div>

                  <div className="ticket-card__details">
                    <div>
                      <span>Fecha</span>
                      <strong>{ticket.date}</strong>
                    </div>

                    <div>
                      <span>Horario</span>
                      <strong>{ticket.time} hs</strong>
                    </div>

                    <div>
                      <span>Ubicación</span>
                      <strong>{ticket.location}</strong>
                    </div>

                    <div>
                      <span>Sector</span>
                      <strong>{ticket.sector}</strong>
                    </div>

                    <div>
                      <span>Cantidad</span>
                      <strong>{ticket.quantity}</strong>
                    </div>
                  </div>
                </div>

                <div className="ticket-card__actions">
                  <button
                    type="button"
                    className="ticket-card__qr"
                    onClick={() => setSelectedTicket(ticket)}
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

              <Link to="/eventos">Explorar eventos</Link>
            </div>
          )}
        </div>
      </section>

      {/* Modal del código QR */}
      {selectedTicket && (
        <div
          className="ticket-qr__overlay"
          onClick={() => setSelectedTicket(null)}
        >
          <section
            className="ticket-qr__modal"
            onClick={(e) => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            aria-labelledby="ticket-qr-title"
          >
            <button
              type="button"
              className="ticket-qr__close"
              onClick={() => setSelectedTicket(null)}
              aria-label="Cerrar"
            >
              ×
            </button>

            <span className="my-tickets__tag">TICKETYA</span>

            <h2 id="ticket-qr-title">Tu entrada</h2>
            <p className="ticket-qr__event">
              {selectedTicket.event}
            </p>

            <div className="ticket-qr__code">
              <QRCodeSVG
                value={selectedTicket.qrCode}
                size={220}
                level="H"
                includeMargin
              />
            </div>

            <div className="ticket-qr__details">
              <strong>{selectedTicket.sector}</strong>
              <span>{selectedTicket.date} · {selectedTicket.time} hs</span>
              <span>{selectedTicket.location}</span>
              <small>Código: {selectedTicket.qrCode}</small>
            </div>

            <p className="ticket-qr__notice">
              Presentá este código QR en el ingreso del evento.
            </p>

            <button
              type="button"
              className="ticket-qr__button"
              onClick={() => setSelectedTicket(null)}
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
