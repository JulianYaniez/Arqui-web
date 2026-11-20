
import '../styles/venueForm.css'

function EventForm (){

  return (
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
  )

}

export default EventForm
