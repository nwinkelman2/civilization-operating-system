import { useState, useEffect } from 'react'
import { useQuery } from '@apollo/client'
import { Gamepad2, Play, Pause, RotateCcw, FastForward } from 'lucide-react'
import { GET_SIMULATION_STATUS } from '../graphql/queries'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import StatusBadge from '../components/StatusBadge'
import BiosphereGauges from '../components/BiosphereGauges'

export default function Simulation() {
  const { data, loading } = useQuery(GET_SIMULATION_STATUS)
  const [isRunning, setIsRunning] = useState<boolean>(true)
  const [localTickCount, setLocalTickCount] = useState<number>(0)
  const [lastStepTime, setLastStepTime] = useState<string>('Just now')

  const sim = data?.simulationStatus

  useEffect(() => {
    if (sim?.tickCount !== undefined) {
      setLocalTickCount(sim.tickCount)
    }
  }, [sim])

  // Simulation tick interval effect when running
  useEffect(() => {
    if (!isRunning) return
    const interval = setInterval(() => {
      setLocalTickCount((prev) => prev + 1)
      setLastStepTime(new Date().toLocaleTimeString())
    }, 4000)

    return () => clearInterval(interval)
  }, [isRunning])

  const handleStepTick = () => {
    setLocalTickCount((prev) => prev + 1)
    setLastStepTime(new Date().toLocaleTimeString())
  }

  const toggleSimulation = () => {
    setIsRunning((prev) => !prev)
  }

  return (
    <Layout
      icon={<Gamepad2 size={24} color="#a78bfa" />}
      title="Simulation Engine"
      subtitle="Cortex AI decision-making, biosphere loop, and tick control"
      actions={
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {isRunning ? (
            <button
              onClick={toggleSimulation}
              style={{
                padding: '0.4rem 0.8rem',
                borderRadius: '0.5rem',
                border: '1px solid #f59e0b',
                background: 'rgba(245, 158, 11, 0.15)',
                color: '#f59e0b',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '0.3rem',
                fontSize: '0.85rem',
                fontWeight: 600,
              }}
            >
              <Pause size={16} /> Pause
            </button>
          ) : (
            <button
              onClick={toggleSimulation}
              style={{
                padding: '0.4rem 0.8rem',
                borderRadius: '0.5rem',
                border: '1px solid #22c55e',
                background: 'rgba(34, 197, 94, 0.15)',
                color: '#22c55e',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '0.3rem',
                fontSize: '0.85rem',
                fontWeight: 600,
              }}
            >
              <Play size={16} /> Play
            </button>
          )}
          <button
            onClick={handleStepTick}
            style={{
              padding: '0.4rem 0.8rem',
              borderRadius: '0.5rem',
              border: 'none',
              background: '#0ea5e9',
              color: 'white',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.3rem',
              fontSize: '0.85rem',
              fontWeight: 600,
            }}
          >
            <RotateCcw size={16} /> Step Tick
          </button>
        </div>
      }
    >
      {/* Biosphere Gauge Metrics Section */}
      <BiosphereGauges />

      {loading ? (
        <div style={{ color: '#64748b', textAlign: 'center', padding: '3rem' }}>Loading simulation status...</div>
      ) : (
        <div className="dashboard-grid">
          <StatCard title="Engine Status">
            <div style={{ fontSize: '0.85rem' }}>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Engine: </span>
                <span style={{ fontWeight: 600, color: '#f8fafc' }}>{sim?.engineName || 'CortexEngine'}</span>
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Execution State: </span>
                <StatusBadge status={isRunning ? 'ACTIVE' : 'IDLE'} />
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Ticks Elapsed: </span>
                <span style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0ea5e9' }}>{localTickCount}</span>
              </div>
              <div style={{ marginBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Last Tick Execution: </span>
                <span style={{ color: '#cbd5e1' }}>{lastStepTime}</span>
              </div>
              <div>
                <span style={{ color: '#94a3b8' }}>Last AI Decision: </span>
                <span style={{ color: '#a78bfa' }}>{sim?.lastDecision || 'Rebalanced hydro distribution'}</span>
              </div>
            </div>
          </StatCard>

          <StatCard title={`Active Rules (${sim?.activeRules || 4})`}>
            {sim?.monitoredCategories?.map((cat: string) => (
              <div
                key={cat}
                style={{
                  padding: '0.4rem 0',
                  borderBottom: '1px solid #334155',
                  fontSize: '0.85rem',
                  color: '#cbd5e1',
                }}
              >
                {cat}
              </div>
            ))}
            {(!sim?.monitoredCategories || sim.monitoredCategories.length === 0) && (
              <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
                <div style={{ padding: '0.3rem 0', borderBottom: '1px solid #334155' }}>Environmental Safeguards</div>
                <div style={{ padding: '0.3rem 0', borderBottom: '1px solid #334155' }}>Resource Distribution Law</div>
                <div style={{ padding: '0.3rem 0', borderBottom: '1px solid #334155' }}>Biosphere Equilibrium Thresholds</div>
                <div style={{ padding: '0.3rem 0' }}>Nexus Inter-Node Synchronization</div>
              </div>
            )}
          </StatCard>

          <StatCard title="Simulation Loop Controls">
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <button
                onClick={toggleSimulation}
                style={{
                  padding: '0.65rem 1rem',
                  borderRadius: '0.5rem',
                  border: isRunning ? '1px solid #f59e0b' : '1px solid #22c55e',
                  background: '#0f172a',
                  color: '#e2e8f0',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '0.5rem',
                  fontWeight: 600,
                  fontSize: '0.85rem',
                }}
              >
                {isRunning ? (
                  <>
                    <Pause size={18} color="#f59e0b" /> Pause Game Loop
                  </>
                ) : (
                  <>
                    <Play size={18} color="#22c55e" /> Resume Game Loop
                  </>
                )}
              </button>
              <button
                onClick={handleStepTick}
                style={{
                  padding: '0.65rem 1rem',
                  borderRadius: '0.5rem',
                  border: '1px solid #0ea5e9',
                  background: '#0f172a',
                  color: '#e2e8f0',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '0.5rem',
                  fontWeight: 600,
                  fontSize: '0.85rem',
                }}
              >
                <FastForward size={18} color="#0ea5e9" /> Force Step Single Tick
              </button>
            </div>
          </StatCard>
        </div>
      )}
    </Layout>
  )
}
