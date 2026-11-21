import { useState } from 'react'

import '../styles/contact.css'

function Contact() {

  const [form, setForm] = useState({
    name: '',
    email: '',
    subject: '',
    message: ''
  })

  const [submitted, setSubmitted] = useState(false)
  const [error, setError] = useState('')

  // Modificar campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target

    setForm(prev => ({
      ...prev,
      [name]: value
    }))
  }

  // Enviar formulario
  const handleSubmit = (e) => {
    e.preventDefault()

    setError('')

    if (!form.name || !form.email || !form.subject || !form.message) {
      setError('Completá todos los campos.')
      return
    }

    // Acá se enviaría el mensaje al backend

    setSubmitted(true)

    setForm({
      name: '',
      email: '',
      subject: '',
      message: ''
    })
  }

  return (
    <main className="contact">

      {/* HEADER */}

      <section className="contact__header">

        <div>
          <span className="contact__tag">
            CONTACTO
          </span>

          <h1>¿Necesitás ayuda?</h1>

          <p>
            Estamos acá para ayudarte. Dejanos tu consulta
            y nos pondremos en contacto con vos.
          </p>
        </div>

      </section>


      {/* FORMULARIO */}

      <section className="contact__container">

        <div className="contact__form-header">

          <h2>Contactanos</h2>

          <p>
            Completá el formulario y envianos tu consulta.
          </p>

        </div>


        {submitted ? (

          <div className="contact__success">

            <h3>¡Mensaje enviado!</h3>

            <p>
              Gracias por contactarte con TicketYa.
              Recibimos tu mensaje y pronto nos pondremos
              en contacto con vos.
            </p>

            <button
              type="button"
              className="contact__button"
              onClick={() => setSubmitted(false)}
            >
              Enviar otro mensaje
            </button>

          </div>

        ) : (

          <form
            className="contact__form"
            onSubmit={handleSubmit}
          >

            {/* NOMBRE */}

            <div className="contact__field">

              <label htmlFor="name">
                Nombre completo
              </label>

              <input
                id="name"
                name="name"
                type="text"
                value={form.name}
                onChange={handleChange}
                placeholder="Ingresá tu nombre"
                required
              />

            </div>


            {/* EMAIL */}

            <div className="contact__field">

              <label htmlFor="email">
                Email
              </label>

              <input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                placeholder="ejemplo@email.com"
                required
              />

            </div>


            {/* ASUNTO */}

            <div className="contact__field">

              <label htmlFor="subject">
                Asunto
              </label>

              <select
                id="subject"
                name="subject"
                value={form.subject}
                onChange={handleChange}
                required
              >

                <option value="">
                  Seleccioná un asunto
                </option>

                <option value="PURCHASE">
                  Problemas con una compra
                </option>

                <option value="EVENT">
                  Consulta sobre un evento
                </option>

                <option value="ACCOUNT">
                  Problemas con mi cuenta
                </option>

                <option value="REFUND">
                  Reembolsos
                </option>

                <option value="OTHER">
                  Otro
                </option>

              </select>

            </div>


            {/* MENSAJE */}

            <div className="contact__field contact__field--full">

              <label htmlFor="message">
                Mensaje
              </label>

              <textarea
                id="message"
                name="message"
                value={form.message}
                onChange={handleChange}
                placeholder="Contanos en qué podemos ayudarte..."
                rows={6}
                maxLength={1000}
                required
              />

              <small>
                Máximo 1000 caracteres.
              </small>

            </div>


            {/* ERROR */}

            {error && (
              <p className="contact__error">
                {error}
              </p>
            )}


            {/* BOTÓN */}

            <div className="contact__form-actions">

              <button
                type="submit"
                className="contact__button"
              >
                Enviar mensaje
              </button>

            </div>

          </form>

        )}

      </section>


      {/* INFORMACIÓN DE CONTACTO */}

      <section className="contact__info">

        <h2>También podés contactarnos</h2>

        <div className="contact__info-cards">

          <article className="contact__info-card">

            <h3>Email</h3>

            <p>
              soporte@ticketya.com
            </p>

          </article>

          <article className="contact__info-card">

            <h3>Atención al cliente</h3>

            <p>
              Estamos para ayudarte con tus consultas.
            </p>

          </article>

        </div>

      </section>

    </main>
  )
}

export default Contact