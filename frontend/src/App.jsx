import { BrowserRouter, Routes, Route } from 'react-router-dom'

import Navbar from './components/NavBar.jsx'
import Footer from './components/Footer.jsx'


import Home from './pages/Home.jsx'
import Events from './pages/Events.jsx'
import EventDetails from './pages/EventDetails.jsx'
import Register from './pages/Register.jsx'
import Login from './pages/Login.jsx'
import Managment from './pages/Managment.jsx'
import MyTickets from './pages/MyTickets.jsx'
import Profile from './pages/Profile.jsx'




import './App.css'
import Checkout from './pages/Checkout.jsx'

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
        <Route path="/venue" element={<Events />} />
        <Route path="/venue/:id" element={<EventDetails />} />

        {/* Autenticación */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Perfil del usuario */}
        <Route path="/profile" element={<Profile />} />

        {/* Mis entradas */}
        <Route path="/my-tickets" element={<MyTickets />} />

        {/* Administración */}
        <Route path="/managment" element={<Managment />} />

        <Route path="/venue/:id/buy" element={<Checkout />} ></Route>

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
