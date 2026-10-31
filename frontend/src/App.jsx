import { BrowserRouter, Routes, Route } from 'react-router-dom'

import Navbar from './components/NavBar.jsx'
import Footer from './components/Footer.jsx'
import EventCard from './components/EventCard.jsx'
import SearchBar from './components/SearchBar.jsx'


import Home from './pages/Home.jsx'
import Events from './pages/Events.jsx'
import EventDetails from './pages/EventDetails.jsx'
import Register from './pages/Register.jsx'
import Login from './pages/Login.jsx'
import Managment from './pages/Managment.jsx'
import MyTickets from './pages/MyTickets.jsx'
import Profile from './pages/Profile.jsx'




import './App.css'

function App() {

  return (
    <>
      <BrowserRouter>
        <Navbar />

        <main className="app__main">


      <Routes>
        {/* Página principal */}
        <Route path="/" element={<Home />} />

        {/* Eventos */}
        <Route path="/eventos" element={<Events />} />
        <Route path="/eventos/:id" element={<EventDetails />} />

        {/* Autenticación */}
        <Route path="/inicio-sesion" element={<Login />} />
        <Route path="/registro" element={<Register />} />

        {/* Perfil del usuario */}
        <Route path="/perfil" element={<Profile />} />

        {/* Mis entradas */}
        <Route path="/mis-entradas" element={<MyTickets />} />

        {/* Administración */}
        <Route path="/administracion" element={<Management />} />

        {/* Página no encontrada */}
        <Route path="*" element={<h2>404 - Página no encontrada</h2>} />
      </Routes>

          
        </main>

        <Footer />
      </BrowserRouter>
    </>
  )
}

export default App
