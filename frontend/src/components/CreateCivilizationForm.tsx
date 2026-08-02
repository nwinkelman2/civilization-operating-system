import { useState } from 'react'
import { useMutation } from '@apollo/client'
import { CREATE_CIVILIZATION, GET_CIVILIZATIONS } from '../graphql/queries'

interface CreateCivilizationFormProps {
  onClose: () => void
}

export default function CreateCivilizationForm({ onClose }: CreateCivilizationFormProps) {
  const [name, setName] = useState('')
  const [scale, setScale] = useState('VILLAGE')
  const [region, setRegion] = useState('')

  const [create, { loading, error }] = useMutation(CREATE_CIVILIZATION, {
    refetchQueries: [{ query: GET_CIVILIZATIONS, variables: { page: 0, size: 10 } }]
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    await create({ variables: { name, scale, region } })
    onClose()
  }

  return (
    <div style={{
      position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.6)',
      display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100
    }}>
      <form onSubmit={handleSubmit} style={{
        background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem',
        padding: '2rem', width: 400
      }}>
        <h2 style={{ fontWeight: 600, marginBottom: '1.5rem' }}>Found New Civilization</h2>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.25rem' }}>Name</label>
          <input
            value={name}
            onChange={e => setName(e.target.value)}
            required
            style={{
              width: '100%', padding: '0.5rem 0.75rem', borderRadius: '0.5rem',
              background: '#0f172a', border: '1px solid #334155', color: '#e2e8f0',
              outline: 'none', fontSize: '0.9rem'
            }}
          />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.25rem' }}>Scale</label>
          <select
            value={scale}
            onChange={e => setScale(e.target.value)}
            style={{
              width: '100%', padding: '0.5rem 0.75rem', borderRadius: '0.5rem',
              background: '#0f172a', border: '1px solid #334155', color: '#e2e8f0',
              outline: 'none', fontSize: '0.9rem'
            }}
          >
            <option value="VILLAGE">Village</option>
            <option value="TOWN">Town</option>
            <option value="CITY">City</option>
            <option value="KINGDOM">Kingdom</option>
          </select>
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.25rem' }}>Region</label>
          <input
            value={region}
            onChange={e => setRegion(e.target.value)}
            style={{
              width: '100%', padding: '0.5rem 0.75rem', borderRadius: '0.5rem',
              background: '#0f172a', border: '1px solid #334155', color: '#e2e8f0',
              outline: 'none', fontSize: '0.9rem'
            }}
          />
        </div>

        {error && <div style={{ color: '#ef4444', fontSize: '0.85rem', marginBottom: '1rem' }}>{error.message}</div>}

        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          <button type="button" onClick={onClose} style={{
            padding: '0.5rem 1rem', borderRadius: '0.5rem', border: '1px solid #334155',
            background: 'transparent', color: '#94a3b8', cursor: 'pointer'
          }}>Cancel</button>
          <button type="submit" disabled={loading} style={{
            padding: '0.5rem 1.5rem', borderRadius: '0.5rem', border: 'none',
            background: '#0ea5e9', color: 'white', cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1
          }}>
            {loading ? 'Founding...' : 'Found Civilization'}
          </button>
        </div>
      </form>
    </div>
  )
}
