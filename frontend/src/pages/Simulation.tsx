import { useQuery } from '@apollo/client'
import { Gamepad2, Play, Pause, RotateCcw } from 'lucide-react'
import { GET_SIMULATION_STATUS } from '../graphql/queries'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'

export default function Simulation() {
  const { data, loading } = useQuery(GET_SIMULATION_STATUS)

  const sim = data?.simulationStatus

  return (
    <Layout
      icon={<Gamepad2 size={24} color="#a78bfa" />}
      title="Simulation Engine"
      subtitle="Cortex AI decision-making and game loop"
      actions={
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button style={{
            padding: '0.4rem 0.8rem', borderRadius: '0.5rem', border: '1px solid #334155',
            background: 'transparent', color: '#94a3b8', cursor: 'pointer',
            display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem'
          }}>
            <Pause size={16} /> Pause
          </button>
          <button style={{
            padding: '0.4rem 0.8rem', borderRadius: '0.5rem', border: 'none',
            background: '#22c55e', color: 'white', cursor: 'pointer',
            display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem'
          }}>
            <RotateCcw size={16} /> Tick
          </button>
        </div>
      }
    >
      {loading ? (
        <div style={{ color: '#64748b', textAlign: 'center', padding: '3rem' }}>Loading simulation status...</div>
      ) : sim ? (
        <div className="dashboard-grid">
          <StatCard title="Engine Status">
            <div style={{ fontSize: '0.85rem' }}>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Engine: </span>
                <span>{sim.engineName}</span>
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Status: </span>
                <StatusBadge status={sim.engineName === 'CortexEngine' ? 'ACTIVE' : 'PENDING'} />
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Ticks Elapsed: </span>
                <span>{sim.tickCount}</span>
              </div>
              <div>
                <span style={{ color: '#94a3b8' }}>Last Decision: </span>
                <span>{sim.lastDecision}</span>
              </div>
            </div>
          </StatCard>

          <StatCard title="Active Rules ({sim.activeRules})">
            {sim.monitoredCategories?.map((cat: string) => (
              <div key={cat} style={{
                padding: '0.4rem 0', borderBottom: '1px solid #334155', fontSize: '0.85rem'
              }}>
                {cat}
              </div>
            ))}
            {(!sim.monitoredCategories || sim.monitoredCategories.length === 0) && (
              <div style={{ color: '#64748b', fontSize: '0.85rem' }}>No monitoring categories</div>
            )}
          </StatCard>

          <StatCard title="Controls">
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <button style={{
                padding: '0.5rem', borderRadius: '0.5rem', border: '1px solid #334155',
                background: '#0f172a', color: '#e2e8f0', cursor: 'pointer',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem'
              }}>
                <Play size={16} color="#22c55e" /> Resume Simulation
              </button>
              <button style={{
                padding: '0.5rem', borderRadius: '0.5rem', border: '1px solid #334155',
                background: '#0f172a', color: '#e2e8f0', cursor: 'pointer',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem'
              }}>
                <RotateCcw size={16} color="#0ea5e9" /> Force Tick
              </button>
            </div>
          </StatCard>
        </div>
      ) : (
        <div style={{ color: '#64748b', textAlign: 'center', padding: '3rem' }}>Simulation engine not available</div>
      )}
    </Layout>
  )
}
