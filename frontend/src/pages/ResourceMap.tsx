import { useState } from 'react'
import { useQuery } from '@apollo/client'
import { GET_REGIONS, GET_RESOURCES } from '../graphql/queries'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { MapPin, Loader2, AlertCircle, Inbox } from 'lucide-react'

export default function ResourceMap() {
  const [selected, setSelected] = useState<string | number | null>(null)
  const { data: regionsData, loading: regionsLoading, error: regionsError } = useQuery(GET_REGIONS)
  const { data: resourcesData } = useQuery(GET_RESOURCES)

  const regionsList = regionsData?.regions || []
  const resourcesList = resourcesData?.resources || []

  if (regionsLoading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
        <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
        <span>Loading resource regions...</span>
      </div>
    )
  }

  if (regionsError) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
        <AlertCircle size={36} />
        <span>Failed to load regions: {regionsError.message}</span>
      </div>
    )
  }

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
        <MapPin size={24} color="#22c55e" />
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Resource Regions</h1>
      </div>

      {regionsList.length === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem', marginBottom: '1.5rem' }}>
          <Inbox size={32} />
          <span>No resource regions available</span>
        </div>
      ) : (
        <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
          {regionsList.map((r: any, idx: number) => {
            const res = {
              food: r.foodAvailability ?? 0,
              water: r.waterAvailability ?? 0,
              minerals: r.mineralAvailability ?? 0,
              energy: r.energyAvailability ?? 0,
              housing: r.housingAvailability ?? 0,
            }
            return (
              <div
                key={r.id || idx}
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
                {r.description && (
                  <div style={{ fontSize: '0.75rem', color: '#64748b', marginBottom: '0.5rem' }}>{r.description}</div>
                )}
                <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
                  {Object.entries(res).map(([k, v]) => (
                    <div key={k} style={{ marginBottom: '0.25rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ textTransform: 'capitalize' }}>{k}</span>
                        <span>{v}/100</span>
                      </div>
                      <div className="resource-bar">
                        <div className="resource-bar-fill" style={{ width: `${Math.min(100, Math.max(0, v as number))}%`, background: '#0ea5e9' }} />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}

      <div style={{ height: 400, borderRadius: '0.75rem', overflow: 'hidden', border: '1px solid #334155' }}>
        <MapContainer center={[-23, -46]} zoom={7} style={{ height: '100%', width: '100%' }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          {regionsList.map((r: any, idx: number) => {
            const pos: [number, number] = [-23.55 + (idx * 0.4), -46.63 + (idx * 0.6)]
            return (
              <Marker key={r.id || idx} position={pos}>
                <Popup>
                  <strong>{r.name}</strong><br />
                  {r.claimed ? 'Claimed' : 'Available'}
                </Popup>
              </Marker>
            )
          })}
        </MapContainer>
      </div>
    </div>
  )
}
