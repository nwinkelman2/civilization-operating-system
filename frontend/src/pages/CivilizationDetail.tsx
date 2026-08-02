import { useParams } from 'react-router-dom'
import { useQuery } from '@apollo/client'
import { GET_CIVILIZATION } from '../graphql/queries'
import { BarChart3, Users, Award, GitBranch } from 'lucide-react'

const resourceColors: Record<string, string> = {
  food: '#22c55e', water: '#0ea5e9', minerals: '#f59e0b',
  energy: '#eab308', housing: '#a78bfa'
}

export default function CivilizationDetail() {
  const { id } = useParams()
  const { data, loading } = useQuery(GET_CIVILIZATION, { variables: { id } })

  if (loading) return <div style={{ color: '#64748b' }}>Loading civilization...</div>

  const civ = data?.civilization
  if (!civ) return <div style={{ color: '#ef4444' }}>Civilization not found</div>

  const resources = civ.resources || {}
  const maxResource = 200

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{civ.name}</h1>
        <div style={{ display: 'flex', gap: '1rem', color: '#64748b', fontSize: '0.85rem', marginTop: '0.25rem' }}>
          <span>{civ.scale} Scale</span>
          <span>·</span>
          <span style={{ textTransform: 'capitalize' }}>{civ.status?.toLowerCase()}</span>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <BarChart3 size={18} color="#0ea5e9" />
            <span style={{ fontWeight: 600 }}>Resources</span>
          </div>
          {Object.entries(resources).map(([key, val]) => (
            <div key={key} style={{ marginBottom: '0.75rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                <span style={{ textTransform: 'capitalize' }}>{key}</span>
                <span>{(val as number).toFixed(0)}</span>
              </div>
              <div className="resource-bar">
                <div className="resource-bar-fill" style={{
                  width: `${Math.min(100, ((val as number) / maxResource) * 100)}%`,
                  background: resourceColors[key] || '#0ea5e9'
                }} />
              </div>
            </div>
          ))}
        </div>

        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <Award size={18} color="#f59e0b" />
            <span style={{ fontWeight: 600 }}>Metrics</span>
          </div>
          <div style={{ fontSize: '0.9rem', marginBottom: '0.75rem' }}>
            <span style={{ color: '#94a3b8' }}>Population: </span>
            <span style={{ fontWeight: 600 }}>{civ.population?.toLocaleString() || 0}</span>
          </div>
          <div style={{ fontSize: '0.9rem' }}>
            <span style={{ color: '#94a3b8' }}>Reputation: </span>
            <span style={{ fontWeight: 600, color: '#22c55e' }}>{civ.reputationScore?.toFixed(1) || 0}</span>
          </div>
        </div>

        {civ.nexusNodes?.length > 0 && (
          <div className="stat-card">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <GitBranch size={18} color="#a78bfa" />
              <span style={{ fontWeight: 600 }}>Nexus Nodes ({civ.nexusNodes.length})</span>
            </div>
            {civ.nexusNodes.map((node: any) => (
              <div key={node.id} style={{
                display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0',
                borderBottom: '1px solid #334155', fontSize: '0.85rem'
              }}>
                <span>{node.name}</span>
                <span style={{
                  padding: '0.1rem 0.4rem', borderRadius: '0.25rem', fontSize: '0.75rem',
                  background: node.status === 'ACTIVE' ? '#065f46' : '#334155',
                  color: node.status === 'ACTIVE' ? '#22c55e' : '#94a3b8'
                }}>{node.status}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
