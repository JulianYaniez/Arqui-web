
import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import '../styles/searchbar.css'

function SearchBar() {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()

  const [search, setSearch] = useState(
    searchParams.get('search') || ''
  )

  useEffect(() => {
    setSearch(searchParams.get('search') || '')
  }, [searchParams])

  const handleSubmit = (e) => {
    e.preventDefault()

    const query = search.trim()

    if (!query) return

    navigate(`/eventos?search=${encodeURIComponent(query)}`)
  }

  const handleClear = () => {
    setSearch('')
    setSearchParams({})
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

      {search && (
        <button
          type="button"
          onClick={handleClear}
          className="searchbar__clear"
          aria-label="Limpiar búsqueda"
        >
          ✕
        </button>
      )}

      <button type="submit">
        Buscar
      </button>
    </form>
  )
}

export default SearchBar
