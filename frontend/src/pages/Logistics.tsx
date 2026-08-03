import { useQuery } from '@apollo/client'
import { GET_SHIPMENTS } from '../graphql/queries'
import { Handshake, Truck, Package, Loader2, AlertCircle, Inbox } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'

const statusColors: Record<string, string> = {
  IN_TRANSIT: '#0ea5e9',
  DELIVERED: '#22c55e',
  PENDING: '#eab308',
  CANCELLED: '#ef4444',
  FAILED: '#ef4444'
}

export default function Logistics() {
  const { data, loading, error } = useQuery(GET_SHIPMENTS)
  const shipments = data?.shipments || []

  if (loading) {
    return (
      <Layout icon={<Handshake size={24} color="#22c55e" />} title="Logistics" subtitle="Resource transportation and supply chains">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading logistics shipments...</span>
        </div>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout icon={<Handshake size={24} color="#22c55e" />} title="Logistics" subtitle="Resource transportation and supply chains">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Failed to load shipments: {error.message}</span>
        </div>
      </Layout>
    )
  }

  const inTransitCount = shipments.filter((s: any) => s.status === 'IN_TRANSIT').length
  const deliveredCount = shipments.filter((s: any) => s.status === 'DELIVERED').length
  const pendingCount = shipments.filter((s: any) => s.status === 'PENDING').length

  return (
    <Layout
      icon={<Handshake size={24} color="#22c55e" />}
      title="Logistics"
      subtitle="Resource transportation and supply chains"
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        {[
          { label: 'In Transit', value: inTransitCount, color: '#0ea5e9' },
          { label: 'Delivered', value: deliveredCount, color: '#22c55e' },
          { label: 'Pending', value: pendingCount, color: '#eab308' },
          { label: 'Total', value: shipments.length, color: '#94a3b8' },
        ].map(s => (
          <StatCard key={s.label} title="">
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.5rem', fontWeight: 700, color: s.color }}>{s.value}</div>
              <div style={{ fontSize: '0.85rem', color: '#64748b' }}>{s.label}</div>
            </div>
          </StatCard>
        ))}
      </div>

      {shipments.length === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem' }}>
          <Inbox size={32} />
          <span>No shipments found</span>
        </div>
      ) : (
        shipments.map((shipment: any) => (
          <div key={shipment.id} style={{
            background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem',
            padding: '1rem', marginBottom: '0.75rem',
            display: 'flex', justifyContent: 'space-between', alignItems: 'center'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div style={{
                width: 40, height: 40, borderRadius: '0.5rem',
                background: '#0f172a', display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                {shipment.status === 'IN_TRANSIT' ? <Truck size={20} color="#0ea5e9" /> : <Package size={20} color="#94a3b8" />}
              </div>
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>
                  {(shipment.resourceType || 'RESOURCE').toUpperCase()} — {shipment.quantity} units
                </div>
                <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
                  {shipment.originRegion || 'Origin'} → {shipment.destinationRegion || 'Destination'}
                </div>
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <StatusBadge status={shipment.status} activeColor={statusColors[shipment.status] || '#94a3b8'} />
              {shipment.createdAt && (
                <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '0.25rem' }}>
                  Created: {new Date(shipment.createdAt).toLocaleDateString()}
                </div>
              )}
            </div>
          </div>
        ))
      )}
    </Layout>
  )
}
