import { useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@apollo/client'
import { GET_CIVILIZATIONS, GET_LEADERBOARD, GET_SIMULATION_STATUS } from '../graphql/queries'
import { BarChart, Activity, Users, Trophy, Plus, Wifi, WifiOff } from 'lucide-react'
import CreateCivilizationForm from '../components/CreateCivilizationForm'
import { useRealtimeUpdates } from '../hooks/useRealtimeUpdates'

export default function Dashboard() {
  const [showForm, setShowForm] = useState(false)
  const [wsConnected, setWsConnected] = useState(false)
  const { data: civData, refetch: refetchCivs } = useQuery(GET_CIVILIZATIONS, { variables: { page: 0, size: 5 } })
  const { data: leaderData, refetch: refetchLeader } = useQuery(GET_LEADERBOARD)
  const { data: simData, refetch: refetchSim } = useQuery(GET_SIMULATION_STATUS)

  const onResourceTick = useCallback(() => { refetchCivs(); refetchLeader() }, [refetchCivs, refetchLeader])
  const onSimulationTick = useCallback(() => { refetchSim() }, [refetchSim])

  useRealtimeUpdates({
    onResourceTick,
    onSimulationTick,
    onCivilizationEvent: useCallback(() => { refetchCivs(); refetchLeader() }, [refetchCivs, refetchLeader]),
    onStatusChange: setWsConnected,
  })

  const civs = civData?.civilizations?.content || []
  const leaderboard = leaderData?.leaderboard || []
  const sim = simData?.simulationStatus

  const totalResources = civs.reduce((acc: any, civ: any) => {
    if (civ.resources) {
      acc.food = (acc.food || 0) + (civ.resources.food || 0)
      acc.water = (acc.water || 0) + (civ.resources.water || 0)
      acc.energy = (acc.energy || 0) + (civ.resources.energy || 0)
    }
    return acc
  }, {})

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.75rem', color: wsConnected ? '#22c55e' : '#ef4444' }}>
            {wsConnected ? <Wifi size={14} /> : <WifiOff size={14} />}
            {wsConnected ? 'Live' : 'Offline'}
          </div>
          <button onClick={() => setShowForm(true)} style={{
            display: 'flex', alignItems: 'center', gap: '0.4rem',
            padding: '0.5rem 1rem', borderRadius: '0.5rem', border: 'none',
            background: '#0ea5e9', color: 'white', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 500
          }}>
            <Plus size={18} /> Found Civilization
          </button>
        </div>
      </div>

      {showForm && <CreateCivilizationForm onClose={() => { setShowForm(false); refetchCivs() }} />}

      <div className="dashboard-grid">
        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <BarChart size={20} color="#0ea5e9" />
            <span style={{ fontWeight: 600 }}>Global Resources</span>
          </div>
          {['food', 'water', 'energy'].map(r => (
            <div key={r} style={{ marginBottom: '0.75rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem', fontSize: '0.85rem' }}>
                <span style={{ textTransform: 'capitalize' }}>{r}</span>
                <span className="text-sky-400 font-bold">{(totalResources[r] || 0).toFixed(1)}</span>
              </div>
              <div className="resource-bar">
                <div className="resource-bar-fill" style={{
                  width: `${Math.min(100, (totalResources[r] || 0) / 10)}%`,
                  background: r === 'food' ? '#22c55e' : r === 'water' ? '#0ea5e9' : '#eab308'
                }} />
              </div>
            </div>
          ))}
        </div>

        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <Activity size={20} color="#a78bfa" />
            <span style={{ fontWeight: 600 }}>Simulation Engine</span>
          </div>
          {sim ? (
            <>
              <div style={{ fontSize: '0.85rem', marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Engine: </span>
                <span>{sim.engineName}</span>
              </div>
              <div style={{ fontSize: '0.85rem', marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Active Rules: </span>
                <span>{sim.activeRules}</span>
              </div>
              <div style={{ fontSize: '0.85rem', marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Ticks: </span>
                <span>{sim.tickCount}</span>
              </div>
              <div style={{ fontSize: '0.8rem', color: '#64748b', fontStyle: 'italic', marginTop: '0.5rem' }}>
                {sim.lastDecision}
              </div>
            </>
          ) : (
            <div style={{ color: '#64748b' }}>Loading simulation data...</div>
          )}
        </div>

        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <Trophy size={20} color="#f59e0b" />
            <span style={{ fontWeight: 600 }}>Leaderboard</span>
          </div>
          {leaderboard.slice(0, 5).map((entry: any, i: number) => (
            <div key={entry.civilizationId} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '0.4rem 0', borderBottom: i < 4 ? '1px solid #334155' : 'none'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ color: '#64748b', fontWeight: 700, width: 20 }}>#{entry.rank}</span>
                <span>{entry.name}</span>
              </div>
              <span style={{ color: '#22c55e' }}>{entry.reputationScore?.toFixed(0)}</span>
            </div>
          ))}
          {leaderboard.length === 0 && (
            <div style={{ color: '#64748b' }}>No civilizations yet</div>
          )}
        </div>

        <div className="stat-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <Users size={20} color="#22c55e" />
            <span style={{ fontWeight: 600 }}>Civilizations</span>
          </div>
          {civs.map((civ: any) => (
            <div key={civ.id} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '0.5rem 0', borderBottom: '1px solid #334155', fontSize: '0.9rem'
            }}>
              <div>
                <Link to={`/civilization/${civ.id}`} style={{ fontWeight: 500, color: '#e2e8f0', textDecoration: 'none' }}
                  onMouseEnter={e => e.currentTarget.style.color = '#0ea5e9'}
                  onMouseLeave={e => e.currentTarget.style.color = '#e2e8f0'}
                >{civ.name}</Link>
                <div style={{ fontSize: '0.75rem', color: '#64748b' }}>{civ.scale} · {civ.region}</div>
              </div>
              <span style={{
                padding: '0.15rem 0.5rem', borderRadius: '1rem', fontSize: '0.75rem',
                background: civ.status === 'ACTIVE' ? '#065f46' : '#334155',
                color: civ.status === 'ACTIVE' ? '#22c55e' : '#94a3b8'
              }}>
                {civ.status}
              </span>
            </div>
          ))}
          {civs.length === 0 && (
            <div style={{ color: '#64748b' }}>Found your first civilization to get started</div>
          )}
        </div>
      </div>
    </div>
  )
}
