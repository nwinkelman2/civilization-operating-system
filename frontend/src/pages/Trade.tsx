import { useState, useEffect } from 'react'
import { ArrowLeftRight, Plus, Loader2, AlertCircle } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'
import DataTable from '../components/DataTable'

export default function Trade() {
  const [agreements, setAgreements] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    setLoading(true)
    fetch('/api/v1/trade/agreements')
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setAgreements(Array.isArray(data) ? data : (data.content || []))
        setError(null)
      })
      .catch(err => {
        setError(err.message || 'Failed to fetch trade agreements')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <Layout icon={<ArrowLeftRight size={24} color="#0ea5e9" />} title="Trade Agreements" subtitle="Inter-civilization trade and diplomacy">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading trade agreements...</span>
        </div>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout icon={<ArrowLeftRight size={24} color="#0ea5e9" />} title="Trade Agreements" subtitle="Inter-civilization trade and diplomacy">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Error loading trade agreements: {error}</span>
        </div>
      </Layout>
    )
  }

  const filtered = filter === 'ALL' ? agreements : agreements.filter(a => a.status === filter)
  const activeCount = agreements.filter(a => a.status === 'ACTIVE' || a.status === 'ACTIVE_EXPERIMENT' || a.status === 'ACCEPTED').length
  const pendingCount = agreements.filter(a => a.status === 'PENDING' || a.status === 'PROPOSED').length

  return (
    <Layout
      icon={<ArrowLeftRight size={24} color="#0ea5e9" />}
      title="Trade Agreements"
      subtitle="Inter-civilization trade and diplomacy"
      actions={
        <button style={{
          display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.4rem 0.8rem',
          borderRadius: '0.5rem', border: 'none', background: '#0ea5e9', color: 'white',
          cursor: 'pointer', fontSize: '0.85rem'
        }}>
          <Plus size={16} /> New Agreement
        </button>
      }
    >
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
        {['ALL', 'ACTIVE', 'PENDING', 'COMPLETED'].map(f => (
          <button key={f} onClick={() => setFilter(f)} style={{
            padding: '0.3rem 0.8rem', borderRadius: '0.5rem', border: '1px solid #334155',
            background: filter === f ? '#0ea5e9' : 'transparent',
            color: filter === f ? 'white' : '#94a3b8', cursor: 'pointer', fontSize: '0.8rem'
          }}>{f === 'ALL' ? 'All' : f.charAt(0) + f.slice(1).toLowerCase()}</button>
        ))}
      </div>

      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Overview">
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Active</span><span style={{ color: '#22c55e' }}>{activeCount}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Pending</span><span style={{ color: '#eab308' }}>{pendingCount}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Total</span><span>{agreements.length}</span>
            </div>
          </div>
        </StatCard>
      </div>

      <DataTable
        columns={[
          { key: 'fromCivilizationId', label: 'From Civ', render: (a: any) => a.fromCivilizationId ? `Civ #${a.fromCivilizationId}` : (a.partner || '-') },
          { key: 'toCivilizationId', label: 'To Civ', render: (a: any) => a.toCivilizationId ? `Civ #${a.toCivilizationId}` : '-' },
          { key: 'resourceType', label: 'Resource', render: (a: any) => a.resourceType ? <span style={{ textTransform: 'capitalize' }}>{a.resourceType}</span> : (a.resource || '-') },
          { key: 'quantity', label: 'Amount', render: (a: any) => a.quantity ?? a.amount ?? '-' },
          { key: 'status', label: 'Status', render: (a: any) => <StatusBadge status={a.status} /> },
          { key: 'createdAt', label: 'Created', render: (a: any) => a.createdAt ? new Date(a.createdAt).toLocaleDateString() : (a.updatedAt || '-') },
        ]}
        data={filtered}
        emptyMessage="No trade agreements found"
      />
    </Layout>
  )
}
