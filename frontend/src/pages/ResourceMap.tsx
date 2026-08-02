import { useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { MapPin } from 'lucide-react'

const mockRegions = [
  { id: 1, name: 'Fertile Valley', position: [-23.55, -46.63], resources: { food: 85, water: 90 }, claimed: false },
  { id: 2, name: 'Granite Highlands', position: [-22.5, -45.0], resources: { minerals: 80, energy: 70 }, claimed: false },
  { id: 3, name: 'Coastal Delta', position: [-23.0, -44.0], resources: { water: 95, housing: 50 }, claimed: true },
]

export default function ResourceMap() {
  const [selected, setSelected] = useState<number | null>(null)

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
        <MapPin size={24} color="#22c55e" />
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Resource Regions</h1>
      </div>

      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        {mockRegions.map(r => (
          <div
            key={r.id}
            className="stat-card"
            style={{ cursor: 'pointer', borderColor: selected === r.id ? '#0ea5e9' : undefined }}
            onClick={() => setSelected(r.id)}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span style={{ fontWeight: 600 }}>{r.name}</span>
              <span style={{
                padding: '0.1rem 0.4rem', borderRadius: '0.25rem', fontSize: '0.7rem',
                background: r.claimed ? '#334155' : '#065f46',
                color: r.claimed ? '#94a3b8' : '#22c55e'
              }}>{r.claimed ? 'CLAIMED' : 'AVAILABLE'}</span>
            </div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
              {Object.entries(r.resources).map(([k, v]) => (
                <div key={k} style={{ marginBottom: '0.25rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ textTransform: 'capitalize' }}>{k}</span>
                    <span>{v}/100</span>
                  </div>
                  <div className="resource-bar">
                    <div className="resource-bar-fill" style={{ width: `${v}%`, background: '#0ea5e9' }} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      <div style={{ height: 400, borderRadius: '0.75rem', overflow: 'hidden', border: '1px solid #334155' }}>
        <MapContainer center={[-23, -46]} zoom={7} style={{ height: '100%', width: '100%' }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          {mockRegions.map(r => (
            <Marker key={r.id} position={r.position as [number, number]}>
              <Popup>
                <strong>{r.name}</strong><br />
                {r.claimed ? 'Claimed' : 'Available'}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
  )
}
