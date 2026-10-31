
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import '../styles/searchbar.css'

function SearchBar() {
  const [search, setSearch] = useState('')
  const navigate = useNavigate()

  const handleSubmit = (e) => {
    e.preventDefault()

    const query = search.trim()

    if (!query) return

    navigate(`/eventos?search=${encodeURIComponent(query)}`)
  }

  return (
    <form className="searchbar" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Buscar eventos..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        aria-label="Buscar eventos"
      />

      <button type="submit">
        Buscar
      </button>
    </form>
  )
}

export default SearchBar
