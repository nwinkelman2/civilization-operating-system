import { useState, useEffect } from 'react'
import { Warehouse, Plus, Loader2, AlertCircle, Inbox } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

export default function Production() {
  const [facilities, setFacilities] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    fetch('/api/v1/production/facilities')
      .then(res => {
        if (!res.ok) return fetch('/api/v1/facilities')
        return res
      })
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setFacilities(Array.isArray(data) ? data : (data.content || []))
        setError(null)
      })
      .catch(err => {
        setError(err.message || 'Failed to fetch production facilities')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <Layout icon={<Warehouse size={24} color="#22c55e" />} title="Production" subtitle="Facilities, output, and consumption">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading production facilities...</span>
        </div>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout icon={<Warehouse size={24} color="#22c55e" />} title="Production" subtitle="Facilities, output, and consumption">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Failed to load production facilities: {error}</span>
        </div>
      </Layout>
    )
  }

  const productionSummary: Record<string, number> = {}
  facilities.forEach(f => {
    const typeKey = (f.type || 'GENERAL').toLowerCase()
    const outVal = typeof f.currentOutput === 'number' ? f.currentOutput : parseFloat(f.currentOutput) || (f.output ?? 10)
    productionSummary[typeKey] = (productionSummary[typeKey] || 0) + outVal
  })

  return (
    <Layout
      icon={<Warehouse size={24} color="#22c55e" />}
      title="Production"
      subtitle="Facilities, output, and consumption"
      actions={
        <button style={{
          display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.4rem 0.8rem',
          borderRadius: '0.5rem', border: 'none', background: '#22c55e', color: 'white',
          cursor: 'pointer', fontSize: '0.85rem'
        }}>
          <Plus size={16} /> Build Facility
        </button>
      }
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Total Resource Output">
          {Object.keys(productionSummary).length === 0 ? (
            <div style={{ fontSize: '0.85rem', color: '#64748b' }}>No output statistics</div>
          ) : (
            Object.entries(productionSummary).map(([key, val]) => (
              <div key={key} style={{ marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                  <span style={{ textTransform: 'capitalize' }}>{key}</span>
                  <span style={{ color: '#22c55e' }}>+{val.toFixed(1)}/tick</span>
                </div>
              </div>
            ))
          )}
        </StatCard>

        <StatCard title="Facilities Overview">
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Total Facilities</span><span style={{ color: '#e2e8f0' }}>{facilities.length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Active</span><span style={{ color: '#22c55e' }}>{facilities.filter(f => f.status === 'ACTIVE').length}</span>
            </div>
          </div>
        </StatCard>
      </div>

      <h2 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem' }}>All Production Facilities</h2>

      {facilities.length === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem' }}>
          <Inbox size={32} />
          <span>No production facilities found</span>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
          {facilities.map((f: any) => {
            const eff = f.efficiency != null ? (f.efficiency <= 1 ? Math.round(f.efficiency * 100) : Math.round(f.efficiency)) : 100
            const outputText = f.currentOutput || `${f.output ?? 10}/tick`
            return (
              <div key={f.id} className="stat-card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                  <div style={{ fontWeight: 600 }}>{f.name}</div>
                  <span style={{
                    padding: '0.1rem 0.4rem', borderRadius: '0.25rem', fontSize: '0.7rem',
                    background: f.status === 'ACTIVE' ? '#065f46' : '#334155',
                    color: f.status === 'ACTIVE' ? '#22c55e' : '#94a3b8'
                  }}>{f.status || 'ACTIVE'}</span>
                </div>
                <div style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '0.5rem' }}>
                  Type: {f.type} · Region: {f.region || 'Global'}
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem' }}>
                  <span style={{ color: '#94a3b8' }}>Efficiency: {eff}%</span>
                  <span style={{ color: '#22c55e', fontWeight: 600 }}>+{outputText}</span>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </Layout>
  )
}
