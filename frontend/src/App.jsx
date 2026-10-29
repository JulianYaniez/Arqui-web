import { BrowserRouter, Routes, Route } from 'react-router-dom'

import Navbar from './components/NavBar.jsx'
import Footer from './components/Footer.jsx'

import Home from './pages/Home.jsx'
import Events from './pages/Events.jsx'
import Register from './pages/Register.jsx'
import Login from './pages/Login.jsx'

import './App.css'

function App() {

  return (
    <>
      <BrowserRouter>
        <Navbar />

        <main className="app__main">

          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/eventos" element={<Events />} />
            <Route path="/inicio-sesion" element={<Login/>} />
            <Route path="/registro" element={<Register/>} />
          </Routes>
          
        </main>

        <Footer />
      </BrowserRouter>
    </>
  )
}

export default App
