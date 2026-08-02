import { useState } from 'react'
import { ArrowLeftRight, Plus } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'
import DataTable from '../components/DataTable'

const mockAgreements = [
  { id: '1', partner: 'Civ Alpha', type: 'RESOURCE', resource: 'food', amount: 50, status: 'ACTIVE', updatedAt: '2026-07-29' },
  { id: '2', partner: 'Civ Beta', type: 'MILITARY', status: 'PENDING', updatedAt: '2026-07-28' },
  { id: '3', partner: 'Civ Gamma', type: 'TECHNOLOGY', tech: 'Irrigation', status: 'ACTIVE_EXPERIMENT', updatedAt: '2026-07-27' },
]

export default function Trade() {
  const [filter, setFilter] = useState('ALL')

  const filtered = filter === 'ALL' ? mockAgreements : mockAgreements.filter(a => a.status === filter)

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
              <span>Active</span><span style={{ color: '#22c55e' }}>{mockAgreements.filter(a => a.status === 'ACTIVE' || a.status === 'ACTIVE_EXPERIMENT').length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Pending</span><span style={{ color: '#eab308' }}>{mockAgreements.filter(a => a.status === 'PENDING').length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Total</span><span>{mockAgreements.length}</span>
            </div>
          </div>
        </StatCard>
      </div>

      <DataTable
        columns={[
          { key: 'partner', label: 'Partner' },
          { key: 'type', label: 'Type', render: (a: any) => <span style={{ textTransform: 'capitalize' }}>{a.type.toLowerCase()}</span> },
          { key: 'resource', label: 'Resource', render: (a: any) => a.resource ? <span style={{ textTransform: 'capitalize' }}>{a.resource}</span> : '-' },
          { key: 'amount', label: 'Amount', render: (a: any) => a.amount ?? '-' },
          { key: 'status', label: 'Status', render: (a: any) => <StatusBadge status={a.status} /> },
          { key: 'updatedAt', label: 'Updated' },
        ]}
        data={filtered}
        emptyMessage="No trade agreements found"
      />
    </Layout>
  )
}
