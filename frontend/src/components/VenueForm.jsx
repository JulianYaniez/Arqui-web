
import { useState } from 'react'
import { createVenue } from '../services/venueService'
import '../styles/venueForm.css'

const emptySector = () => ({
  name: '',
  isNumbered: false,
  capacity: '',
  rows: '',
  seatsPerRow: ''
})

// Obtiene las coordenadas a partir de la dirección y ciudad
const geocodeAddress = async (address, city) => {
  const params = new URLSearchParams({
    q: `${address}, ${city}, Buenos Aires, Argentina`,
    format: 'jsonv2',
    limit: '1',
    countrycodes: 'ar',
    addressdetails: '1'
  })

  const url = `https://nominatim.openstreetmap.org/search?${params}`

  const response = await fetch(url, {
    headers: {
      'Accept': 'application/json',
      'Accept-Language': 'es'
    }
  })

  if (!response.ok) {
    const responseText = await response.text()
    console.error('Error Nominatim:', response.status, responseText)

    throw new Error(
      `Error al buscar la ubicación (${response.status}). Intentá nuevamente.`
    )
  }

  const results = await response.json()

  if (!results.length) {
    throw new Error(
      'No encontramos esa dirección. Verificá la ciudad y la dirección.'
    )
  }

  return {
    latitude: Number(results[0].lat),
    longitude: Number(results[0].lon)
  }
}

function VenueForm({ onClose, onCreated }) {
  const [form, setForm] = useState({
    name: '',
    address: '',
    city: ''
  })

  const [sectors, setSectors] = useState([emptySector()])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSectorChange = (index, field, value) => {
    setSectors(prev =>
      prev.map((sector, i) =>
        i === index
          ? { ...sector, [field]: value }
          : sector
      )
    )
  }

  const addSector = () => {
    setSectors(prev => [...prev, emptySector()])
  }

  const removeSector = (index) => {
    setSectors(prev => prev.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (sectors.length === 0) {
      setError('El lugar debe tener al menos un sector.')
      return
    }

    const invalidSector = sectors.some(sector => {
      if (!sector.name.trim()) return true

      if (sector.isNumbered) {
        return (
          !sector.rows ||
          !sector.seatsPerRow ||
          Number(sector.rows) <= 0 ||
          Number(sector.seatsPerRow) <= 0
        )
      }

      return (
        !sector.capacity ||
        Number(sector.capacity) <= 0
      )
    })

    if (invalidSector) {
      setError('Revisá los datos de los sectores.')
      return
    }

    try {
      setLoading(true)

      // 1. Obtener coordenadas automáticamente
      const location = await geocodeAddress(
        form.address.trim(),
        form.city.trim()
      )

      // 2. Preparar información del lugar
      const venueData = {
        name: form.name.trim(),
        address: form.address.trim(),
        city: form.city.trim(),

        location: {
          latitude: location.latitude,
          longitude: location.longitude
        },

        sectors: sectors.map(sector => ({
          name: sector.name.trim(),
          isNumbered: sector.isNumbered,
          capacity: sector.isNumbered
            ? null
            : Number(sector.capacity),
          rows: sector.isNumbered
            ? Number(sector.rows)
            : null,
          seatsPerRow: sector.isNumbered
            ? Number(sector.seatsPerRow)
            : null
        }))
      }

      console.log('Venue a enviar:', venueData)

      // 3. Crear lugar en el backend
      await createVenue(venueData)

      onCreated?.()
      onClose()

    } catch (error) {
      console.error('Error creando Venue:', error)
      setError(error.message || 'No se pudo crear el lugar.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="venue-form-container">

      <div className="venue-form-header">
        <div>
          <span className="venue-form-tag">
            ADMINISTRACIÓN
          </span>

          <h2>Nuevo lugar</h2>

          <p>Registrá un establecimiento y sus sectores.</p>
        </div>

        <button
          type="button"
          className="venue-form-close"
          onClick={onClose}
          disabled={loading}
        >
          ×
        </button>
      </div>

      <form onSubmit={handleSubmit} className="venue-form">

        <h3>Información del lugar</h3>

        <div className="venue-form-grid">

          <div className="venue-form-field">
            <label>Nombre del lugar</label>

            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Ej: Estadio Municipal"
              required
            />
          </div>

          <div className="venue-form-field">
            <label>Ciudad</label>

            <input
              name="city"
              value={form.city}
              onChange={handleChange}
              placeholder="Ej: Tandil"
              required
            />
          </div>

          <div className="venue-form-field venue-form-full">
            <label>Dirección</label>

            <input
              name="address"
              value={form.address}
              onChange={handleChange}
              placeholder="Ej: Av. Rivadavia 1000"
              required
            />
          </div>

        </div>

        <div className="venue-form-location-info">
          <span>📍</span>

          <div>
            <strong>Ubicación automática</strong>
            <p>
              Las coordenadas geográficas se calcularán
              automáticamente utilizando la ciudad y dirección
              ingresadas.
            </p>
          </div>
        </div>

        <div className="venue-form-sectors-header">
          <div>
            <h3>Sectores</h3>
            <p>Configurá los espacios disponibles del lugar.</p>
          </div>

          <button
            type="button"
            className="venue-form-add"
            onClick={addSector}
          >
            + Agregar sector
          </button>
        </div>

        {sectors.map((sector, index) => (
          <article className="venue-sector-card" key={index}>

            <div className="venue-sector-header">
              <h4>Sector {index + 1}</h4>

              {sectors.length > 1 && (
                <button
                  type="button"
                  className="venue-sector-remove"
                  onClick={() => removeSector(index)}
                >
                  Eliminar
                </button>
              )}
            </div>

            <div className="venue-form-field">
              <label>Nombre del sector</label>

              <input
                value={sector.name}
                onChange={e =>
                  handleSectorChange(index, 'name', e.target.value)
                }
                placeholder="Ej: Platea, Campo, VIP"
                required
              />
            </div>

            <div className="venue-sector-type">
              <label>
                <input
                  type="checkbox"
                  checked={sector.isNumbered}
                  onChange={e => {
                    const numbered = e.target.checked

                    setSectors(prev =>
                      prev.map((item, i) =>
                        i === index
                          ? {
                              ...item,
                              isNumbered: numbered,
                              capacity: '',
                              rows: '',
                              seatsPerRow: ''
                            }
                          : item
                      )
                    )
                  }}
                />

                Este sector tiene asientos numerados
              </label>
            </div>

            {sector.isNumbered ? (

              <div className="venue-form-grid">

                <div className="venue-form-field">
                  <label>Cantidad de filas</label>

                  <input
                    type="number"
                    min="1"
                    value={sector.rows}
                    onChange={e =>
                      handleSectorChange(index, 'rows', e.target.value)
                    }
                    required
                  />
                </div>

                <div className="venue-form-field">
                  <label>Asientos por fila</label>

                  <input
                    type="number"
                    min="1"
                    value={sector.seatsPerRow}
                    onChange={e =>
                      handleSectorChange(
                        index,
                        'seatsPerRow',
                        e.target.value
                      )
                    }
                    required
                  />
                </div>

                <p className="venue-sector-capacity">
                  Capacidad calculada: {
                    Number(sector.rows || 0) *
                    Number(sector.seatsPerRow || 0)
                  } personas
                </p>

              </div>

            ) : (

              <div className="venue-form-field">
                <label>Capacidad máxima</label>

                <input
                  type="number"
                  min="1"
                  value={sector.capacity}
                  onChange={e =>
                    handleSectorChange(index, 'capacity', e.target.value)
                  }
                  placeholder="Ej: 500"
                  required
                />
              </div>

            )}

          </article>
        ))}

        {error && (
          <p className="venue-form-error">{error}</p>
        )}

        <div className="venue-form-actions">

          <button
            type="button"
            className="venue-form-cancel"
            onClick={onClose}
            disabled={loading}
          >
            Cancelar
          </button>

          <button
            type="submit"
            className="venue-form-submit"
            disabled={loading}
          >
            {loading ? 'Buscando ubicación y guardando...' : 'Crear lugar'}
          </button>

        </div>

      </form>
    </section>
  )
}

export default VenueForm
