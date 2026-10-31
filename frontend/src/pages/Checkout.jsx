
import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import '../styles/checkout.css'

function Checkout() {
  const { id } = useParams()
  const navigate = useNavigate()

  // Datos temporales hasta conectar el catalog-service
  const event = {
    id,
    name: 'Festival de Música',
    date: '15 de noviembre de 2026',
    time: '20:00 hs',
    location: 'Estadio Municipal',
    address: 'Av. Principal 123, Tandil',
    image: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=1200'
  }

  const sectors = [
    { id: 1, name: 'Campo', price: 25000, available: 150 },
    { id: 2, name: 'Platea', price: 35000, available: 80 },
    { id: 3, name: 'VIP', price: 50000, available: 20 }
  ]

  const [selectedSector, setSelectedSector] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [buyer, setBuyer] = useState({
    name: '',
    surname: '',
    email: ''
  })
  const [purchaseCompleted, setPurchaseCompleted] = useState(false)

  const sector = sectors.find(
    item => item.id === Number(selectedSector)
  )

  const total = sector ? sector.price * quantity : 0

  const handleSectorChange = (e) => {
    setSelectedSector(e.target.value)
    setQuantity(1)
  }

  const handleBuyerChange = (e) => {
    setBuyer({
      ...buyer,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (!sector) return

    // Compra simulada hasta conectar el payment-service
    setPurchaseCompleted(true)
  }

  if (purchaseCompleted) {
    return (
      <main className="checkout">
        <section className="checkout__success">
          <div className="checkout__success-icon">✓</div>
          <h1>¡Compra realizada!</h1>
          <p>
            Tu compra de {quantity} entrada(s) para {event.name} se realizó correctamente.
          </p>
          <p className="checkout__success-email">
            Enviaremos los detalles a {buyer.email}.
          </p>

          <Link to="/mis-entradas" className="checkout__button">
            Ver mis entradas
          </Link>
        </section>
      </main>
    )
  }

  return (
    <main className="checkout">
      <Link
        to={`/eventos/${id}`}
        className="checkout__back"
      >
        ← Volver al evento
      </Link>

      <header className="checkout__header">
        <h1>Finalizar compra</h1>
        <p>Completá tus datos para asegurar tus entradas.</p>
      </header>

      <div className="checkout__layout">
        <form className="checkout__form" onSubmit={handleSubmit}>
          <section className="checkout__section">
            <h2>1. Seleccioná tus entradas</h2>

            <div className="checkout__field">
              <label htmlFor="sector">Sector</label>
              <select
                id="sector"
                value={selectedSector}
                onChange={handleSectorChange}
                required
              >
                <option value="">Seleccioná un sector</option>

                {sectors.map(item => (
                  <option
                    key={item.id}
                    value={item.id}
                    disabled={item.available === 0}
                  >
                    {item.name} - ${item.price.toLocaleString('es-AR')}
                    {' '}({item.available} disponibles)
                  </option>
                ))}
              </select>
            </div>

            <div className="checkout__field">
              <label htmlFor="quantity">Cantidad de entradas</label>
              <input
                id="quantity"
                type="number"
                min="1"
                max={sector?.available || 1}
                value={quantity}
                disabled={!sector}
                onChange={(e) => {
                  const value = Number(e.target.value)
                  if (value >= 1 && value <= sector.available) {
                    setQuantity(value)
                  }
                }}
                required
              />
            </div>
          </section>

          <section className="checkout__section">
            <h2>2. Datos del comprador</h2>

            <div className="checkout__field">
              <label htmlFor="name">Nombre</label>
              <input
                id="name"
                name="name"
                type="text"
                placeholder="Tu nombre"
                value={buyer.name}
                onChange={handleBuyerChange}
                required
              />
            </div>

            <div className="checkout__field">
              <label htmlFor="surname">Apellido</label>
              <input
                id="surname"
                name="surname"
                type="text"
                placeholder="Tu apellido"
                value={buyer.surname}
                onChange={handleBuyerChange}
                required
              />
            </div>

            <div className="checkout__field">
              <label htmlFor="email">Correo electrónico</label>
              <input
                id="email"
                name="email"
                type="email"
                placeholder="ejemplo@correo.com"
                value={buyer.email}
                onChange={handleBuyerChange}
                required
              />
            </div>
          </section>

          <button
            type="submit"
            className="checkout__button"
            disabled={!sector}
          >
            Confirmar compra
          </button>

          <small className="checkout__notice">
            Esta compra es una simulación. El pago se integrará posteriormente.
          </small>
        </form>

        <aside className="checkout__summary">
          <h2>Resumen de compra</h2>

          <img
            src={event.image}
            alt={event.name}
            className="checkout__image"
          />

          <h3>{event.name}</h3>

          <div className="checkout__event-info">
            <span>📅 {event.date}</span>
            <span>🕒 {event.time}</span>
            <span>📍 {event.location}</span>
          </div>

          <div className="checkout__divider" />

          {sector ? (
            <>
              <div className="checkout__summary-row">
                <span>{sector.name}</span>
                <strong>${sector.price.toLocaleString('es-AR')}</strong>
              </div>

              <div className="checkout__summary-row">
                <span>Cantidad</span>
                <strong>{quantity}</strong>
              </div>

              <div className="checkout__divider" />

              <div className="checkout__total">
                <span>Total</span>
                <strong>${total.toLocaleString('es-AR')}</strong>
              </div>
            </>
          ) : (
            <p className="checkout__empty">
              Seleccioná un sector para calcular el total.
            </p>
          )}
        </aside>
      </div>
    </main>
  )
}

export default Checkout
