
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

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    setError('')

    if (!form.name || !form.email || !form.subject || !form.message) {
      setError('Completá todos los campos.')
      return
    }

    // Acá se enviaría el mensaje al backend.

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

      <section className="contact__header">
        <div>
          <span className="contact__tag">CENTRO DE AYUDA</span>
          <h1>Contacto</h1>
          <p>
            ¿Tenés alguna consulta o problema?
            Estamos acá para ayudarte.
          </p>
        </div>
      </section>

      <section className="contact__content">

        <div className="contact__form-card">

          <div className="contact__form-header">
            <h2>Envianos tu consulta</h2>
            <p>
              Completá el formulario y nuestro equipo
              se pondrá en contacto con vos.
            </p>
          </div>

          {submitted ? (

            <div className="contact__success">
              <div className="contact__success-icon">✓</div>
              <h3>¡Mensaje enviado!</h3>
              <p>
                Gracias por contactarte con TicketYa.
                Recibimos tu consulta y pronto nos
                pondremos en contacto con vos.
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

              <div className="contact__field">
                <label htmlFor="name">Nombre completo</label>
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

              <div className="contact__field">
                <label htmlFor="email">Correo electrónico</label>
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

              <div className="contact__field">
                <label htmlFor="subject">Asunto</label>
                <select
                  id="subject"
                  name="subject"
                  value={form.subject}
                  onChange={handleChange}
                  required
                >
                  <option value="">Seleccioná un asunto</option>
                  <option value="PURCHASE">Problemas con una compra</option>
                  <option value="EVENT">Consulta sobre un evento</option>
                  <option value="ACCOUNT">Problemas con mi cuenta</option>
                  <option value="REFUND">Reembolsos</option>
                  <option value="OTHER">Otro</option>
                </select>
              </div>

              <div className="contact__field">
                <label htmlFor="message">Mensaje</label>
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
                <small>{form.message.length}/1000 caracteres</small>
              </div>

              {error && (
                <p className="contact__error">{error}</p>
              )}

              <button
                type="submit"
                className="contact__button"
              >
                Enviar mensaje
              </button>

            </form>

          )}

        </div>

        <aside className="contact__sidebar">

          <div className="contact__info-card">
            <div className="contact__info-icon">✉</div>
            <h3>Correo electrónico</h3>
            <p>Escribinos y te responderemos a la brevedad.</p>
            <span>soporte@ticketya.com</span>
          </div>

          <div className="contact__info-card">
            <div className="contact__info-icon">?</div>
            <h3>Atención al cliente</h3>
            <p>
              Estamos disponibles para ayudarte con
              tus compras, eventos y consultas.
            </p>
          </div>

          <div className="contact__help-card">
            <h3>¿Necesitás ayuda?</h3>
            <p>
              Nuestro equipo está comprometido con
              brindarte la mejor experiencia en TicketYa.
            </p>
          </div>

        </aside>

      </section>

    </main>
  )
}

export default Contact
