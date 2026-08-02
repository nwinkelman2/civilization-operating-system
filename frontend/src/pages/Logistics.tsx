import { Handshake, Truck, Package } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'

const mockShipments = [
  { id: 'S001', origin: 'Central Farm', dest: 'City Center', resource: 'food', amount: 30, status: 'IN_TRANSIT', eta: '2m' },
  { id: 'S002', origin: 'Iron Mine', dest: 'Workshop', resource: 'minerals', amount: 15, status: 'DELIVERED', eta: '-' },
  { id: 'S003', origin: 'Solar Array', dest: 'Residential Block A', resource: 'energy', amount: 10, status: 'PENDING', eta: '5m' },
  { id: 'S004', origin: 'Water Pump', dest: 'Central Farm', resource: 'water', amount: 20, status: 'IN_TRANSIT', eta: '1m' },
]

const statusColors: Record<string, string> = {
  IN_TRANSIT: '#0ea5e9', DELIVERED: '#22c55e', PENDING: '#eab308'
}

export default function Logistics() {
  return (
    <Layout
      icon={<Handshake size={24} color="#22c55e" />}
      title="Logistics"
      subtitle="Resource transportation and supply chains"
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        {[
          { label: 'In Transit', value: mockShipments.filter(s => s.status === 'IN_TRANSIT').length, color: '#0ea5e9' },
          { label: 'Delivered', value: mockShipments.filter(s => s.status === 'DELIVERED').length, color: '#22c55e' },
          { label: 'Pending', value: mockShipments.filter(s => s.status === 'PENDING').length, color: '#eab308' },
          { label: 'Total', value: mockShipments.length, color: '#94a3b8' },
        ].map(s => (
          <StatCard key={s.label} title="">
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.5rem', fontWeight: 700, color: s.color }}>{s.value}</div>
              <div style={{ fontSize: '0.85rem', color: '#64748b' }}>{s.label}</div>
            </div>
          </StatCard>
        ))}
      </div>

      {mockShipments.map(shipment => (
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
              <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{shipment.resource.toUpperCase()} — {shipment.amount} units</div>
              <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
                {shipment.origin} → {shipment.dest}
              </div>
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <StatusBadge status={shipment.status} activeColor={statusColors[shipment.status] || '#94a3b8'} />
            {shipment.eta !== '-' && (
              <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '0.25rem' }}>ETA: {shipment.eta}</div>
            )}
          </div>
        </div>
      ))}
    </Layout>
  )
}
