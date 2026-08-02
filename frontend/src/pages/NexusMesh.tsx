import { useQuery } from '@apollo/client'
import { GET_NEXUS_NODES } from '../graphql/queries'
import { Network, Wifi, WifiOff } from 'lucide-react'

export default function NexusMesh() {
  const { data, loading } = useQuery(GET_NEXUS_NODES)

  if (loading) return <div style={{ color: '#64748b' }}>Loading Nexus mesh...</div>

  const nodes = data?.nexusNodes || []

  const activeNodes = nodes.filter((n: any) => n.status === 'ACTIVE').length
  const totalMessages = nodes.reduce((sum: number, n: any) => sum + (n.messageCount || 0), 0)

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
        <Network size={24} color="#a78bfa" />
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Nexus Neural Mesh</h1>
      </div>

      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <div className="stat-card" style={{ textAlign: 'center' }}>
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#a78bfa' }}>{nodes.length}</div>
          <div style={{ color: '#64748b', fontSize: '0.85rem' }}>Total Nodes</div>
        </div>
        <div className="stat-card" style={{ textAlign: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
            {activeNodes > 0 ? <Wifi color="#22c55e" /> : <WifiOff color="#ef4444" />}
            <span style={{ fontSize: '2rem', fontWeight: 700, color: activeNodes > 0 ? '#22c55e' : '#ef4444' }}>{activeNodes}</span>
          </div>
          <div style={{ color: '#64748b', fontSize: '0.85rem' }}>Active Nodes</div>
        </div>
        <div className="stat-card" style={{ textAlign: 'center' }}>
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#0ea5e9' }}>{totalMessages}</div>
          <div style={{ color: '#64748b', fontSize: '0.85rem' }}>Messages Exchanged</div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
        {nodes.map((node: any) => (
          <div key={node.id} className="stat-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span style={{ fontWeight: 600 }}>{node.name}</span>
              <span style={{
                padding: '0.15rem 0.5rem', borderRadius: '1rem', fontSize: '0.7rem',
                background: node.status === 'ACTIVE' ? '#065f46' : '#334155',
                color: node.status === 'ACTIVE' ? '#22c55e' : '#94a3b8'
              }}>{node.status}</span>
            </div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
              <div>Type: {node.type}</div>
              {node.region && <div>Region: {node.region}</div>}
              <div>Messages: {node.messageCount || 0}</div>
            </div>
          </div>
        ))}
        {nodes.length === 0 && (
          <div style={{ color: '#64748b', gridColumn: '1 / -1', textAlign: 'center', padding: '3rem' }}>
            No nexus nodes deployed. Found a civilization to create a primary node.
          </div>
        )}
      </div>
    </div>
  )
}
