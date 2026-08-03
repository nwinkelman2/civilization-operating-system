import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import {
  Globe, Users, Shield, MapPin, Calendar, Activity,
  TrendingUp, ArrowLeft, Loader2, Clock, GitBranch
} from 'lucide-react'
import {
  ResponsiveContainer, LineChart, Line, XAxis, YAxis,
  Tooltip, CartesianGrid, Legend
} from 'recharts'
import Layout from '../components/Layout'
import StatusBadge from '../components/StatusBadge'
import StatCard from '../components/StatCard'
import NeedsProgress from '../components/NeedsProgress'

// Historical trend data for Population and Resources
const MOCK_HISTORICAL_DATA = [
  { cycle: 'Cycle 1', population: 1000, food: 200, water: 250, energy: 180, minerals: 150 },
  { cycle: 'Cycle 2', population: 1250, food: 240, water: 270, energy: 210, minerals: 180 },
  { cycle: 'Cycle 3', population: 1600, food: 290, water: 310, energy: 260, minerals: 220 },
  { cycle: 'Cycle 4', population: 2100, food: 350, water: 380, energy: 320, minerals: 290 },
  { cycle: 'Cycle 5', population: 2800, food: 420, water: 450, energy: 400, minerals: 350 },
  { cycle: 'Cycle 6', population: 3600, food: 510, water: 520, energy: 480, minerals: 410 },
  { cycle: 'Cycle 7', population: 4500, food: 600, water: 590, energy: 570, minerals: 490 },
]

// Timeline events
const MOCK_TIMELINE_EVENTS = [
  { id: 1, cycle: 'Cycle 1', title: 'Civilization Established', description: 'Founding charter signed and initial settlement initialized in Sector Alpha.', type: 'milestone' },
  { id: 2, cycle: 'Cycle 3', title: 'Bio-hydroponics Mastery', description: 'Agricultural tech unlocked; food production increased by 40%.', type: 'tech' },
  { id: 3, cycle: 'Cycle 4', title: 'Inter-Civilization Trade Accord', description: 'Signed bilateral resource pact with Solaria Federation.', type: 'diplomacy' },
  { id: 4, cycle: 'Cycle 6', title: 'Nexus Mesh Synchronization', description: 'Linked 3 primary nodes into high-bandwidth quantum mesh network.', type: 'infrastructure' },
]

export default function CivilizationDetail() {
  const { id = '1' } = useParams()
  const [civData, setCivData] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    fetch(`/api/v1/civilizations/${id}`)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setCivData(data)
        setError(null)
      })
      .catch(() => {
        // Fallback demo data if backend endpoint is unavailable
        setCivData({
          id,
          name: `Civilization #${id}`,
          scale: 'Kardashev Type I',
          status: 'ACTIVE',
          population: 4500,
          reputationScore: 94.2,
          homeRegion: {
            name: 'Terran Sector Alpha-9',
            biome: 'Temperate / Maritime',
            surfaceArea: '1,250,000 km²',
            coordinates: '37.7749° N, 122.4194° W',
            habitability: '98%'
          },
          needs: {
            housing: 88,
            food: 94,
            water: 82,
            energy: 90
          },
          resources: {
            food: 600,
            water: 590,
            energy: 570,
            minerals: 490
          }
        })
        setError(null)
      })
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return (
      <Layout icon={<Globe size={24} color="#0ea5e9" />} title="Civilization Details" subtitle={`Viewing civilization #${id}`}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading civilization details...</span>
        </div>
      </Layout>
    )
  }

  const civ = civData || {}
  const homeRegion = civ.homeRegion || {
    name: 'Terran Sector Alpha-9',
    biome: 'Temperate / Maritime',
    surfaceArea: '1,250,000 km²',
    coordinates: '37.7749° N, 122.4194° W',
    habitability: '98%'
  }

  return (
    <Layout
      icon={<Globe size={24} color="#0ea5e9" />}
      title={civ.name || `Civilization #${id}`}
      subtitle={`${civ.scale || 'Type I'} · Territory & Resource Hub`}
      actions={
        <Link
          to="/"
          style={{
            display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.4rem 0.8rem',
            borderRadius: '0.5rem', border: '1px solid #334155', background: 'transparent',
            color: '#cbd5e1', textDecoration: 'none', fontSize: '0.85rem'
          }}
        >
          <ArrowLeft size={16} /> Back to Overview
        </Link>
      }
    >
      {/* Header status bar */}
      <div style={{
        background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem',
        padding: '1.25rem', marginBottom: '1.5rem', display: 'flex',
        flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '1rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{
            width: 52, height: 52, borderRadius: '0.75rem', background: 'rgba(14, 165, 233, 0.15)',
            border: '1px solid rgba(14, 165, 233, 0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center'
          }}>
            <Globe size={28} color="#0ea5e9" />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.25rem' }}>
              <h2 style={{ fontSize: '1.35rem', fontWeight: 700, margin: 0, color: '#f8fafc' }}>
                {civ.name || `Civilization #${id}`}
              </h2>
              <StatusBadge status={civ.status || 'ACTIVE'} />
            </div>
            <div style={{ fontSize: '0.85rem', color: '#94a3b8', display: 'flex', gap: '1rem' }}>
              <span>Scale: <strong style={{ color: '#e2e8f0' }}>{civ.scale || 'Kardashev I'}</strong></span>
              <span>·</span>
              <span>ID: <strong style={{ color: '#e2e8f0' }}>#{id}</strong></span>
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '1.5rem' }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Population</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#f8fafc', marginTop: '0.2rem' }}>
              {civ.population?.toLocaleString() || '4,500'}
            </div>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Reputation</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#22c55e', marginTop: '0.2rem' }}>
              {civ.reputationScore ? civ.reputationScore.toFixed(1) : '94.2'}
            </div>
          </div>
        </div>
      </div>

      {/* Main Grid: Needs Progress & Territory Info */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem', marginBottom: '1.5rem' }}>
        <NeedsProgress needs={civ.needs} />

        {/* Territory & Home Region Info */}
        <StatCard title="Territory & Home Region" icon={<MapPin size={18} color="#0ea5e9" />}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.85rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid #334155' }}>
              <span style={{ color: '#94a3b8' }}>Region Name</span>
              <span style={{ fontWeight: 600, color: '#f8fafc' }}>{homeRegion.name}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid #334155' }}>
              <span style={{ color: '#94a3b8' }}>Primary Biome</span>
              <span style={{ fontWeight: 500, color: '#e2e8f0' }}>{homeRegion.biome}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid #334155' }}>
              <span style={{ color: '#94a3b8' }}>Surface Area</span>
              <span style={{ fontWeight: 500, color: '#e2e8f0' }}>{homeRegion.surfaceArea}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid #334155' }}>
              <span style={{ color: '#94a3b8' }}>Coordinates</span>
              <span style={{ fontWeight: 500, color: '#0ea5e9' }}>{homeRegion.coordinates}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94a3b8' }}>Habitability Index</span>
              <span style={{ fontWeight: 700, color: '#22c55e' }}>{homeRegion.habitability}</span>
            </div>
          </div>
        </StatCard>
      </div>

      {/* Historical Trend Charts */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '1.5rem', marginBottom: '1.5rem' }}>
        {/* Population Growth Line Chart */}
        <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem', padding: '1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <TrendingUp size={18} color="#0ea5e9" />
            <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#f8fafc', margin: 0 }}>
              Population Growth Trend
            </h3>
          </div>
          <div style={{ width: '100%', height: 260 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={MOCK_HISTORICAL_DATA}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="cycle" stroke="#94a3b8" fontSize={12} />
                <YAxis stroke="#94a3b8" fontSize={12} />
                <Tooltip
                  contentStyle={{ background: '#0f172a', border: '1px solid #334155', borderRadius: '0.5rem', color: '#f8fafc' }}
                />
                <Line type="monotone" dataKey="population" stroke="#0ea5e9" strokeWidth={3} dot={{ r: 4 }} name="Population" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Resource Historical Trend Chart */}
        <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem', padding: '1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
            <Activity size={18} color="#22c55e" />
            <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#f8fafc', margin: 0 }}>
              Resource Reserves Trend
            </h3>
          </div>
          <div style={{ width: '100%', height: 260 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={MOCK_HISTORICAL_DATA}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="cycle" stroke="#94a3b8" fontSize={12} />
                <YAxis stroke="#94a3b8" fontSize={12} />
                <Tooltip
                  contentStyle={{ background: '#0f172a', border: '1px solid #334155', borderRadius: '0.5rem', color: '#f8fafc' }}
                />
                <Legend wrapperStyle={{ fontSize: '12px', paddingTop: '10px' }} />
                <Line type="monotone" dataKey="food" stroke="#22c55e" strokeWidth={2} name="Food" />
                <Line type="monotone" dataKey="water" stroke="#0ea5e9" strokeWidth={2} name="Water" />
                <Line type="monotone" dataKey="energy" stroke="#eab308" strokeWidth={2} name="Energy" />
                <Line type="monotone" dataKey="minerals" stroke="#f59e0b" strokeWidth={2} name="Minerals" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Historical Events Timeline */}
      <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem', padding: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.25rem' }}>
          <Clock size={18} color="#a78bfa" />
          <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#f8fafc', margin: 0 }}>
            Historical Events Timeline
          </h3>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', position: 'relative', paddingLeft: '1.5rem' }}>
          {/* Vertical timeline rule */}
          <div style={{
            position: 'absolute', left: '7px', top: '8px', bottom: '8px', width: '2px', background: '#334155'
          }} />

          {MOCK_TIMELINE_EVENTS.map(event => (
            <div key={event.id} style={{ position: 'relative' }}>
              {/* Dot icon */}
              <div style={{
                position: 'absolute', left: '-1.5rem', top: '4px', width: '12px', height: '12px',
                borderRadius: '50%', background: '#a78bfa', border: '2px solid #1e293b'
              }} />

              <div style={{ background: '#0f172a', border: '1px solid #334155', borderRadius: '0.5rem', padding: '0.85rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
                  <span style={{ fontSize: '0.9rem', fontWeight: 600, color: '#f8fafc' }}>{event.title}</span>
                  <span style={{ fontSize: '0.75rem', padding: '0.1rem 0.4rem', borderRadius: '0.25rem', background: '#334155', color: '#a78bfa' }}>
                    {event.cycle}
                  </span>
                </div>
                <p style={{ fontSize: '0.8rem', color: '#94a3b8', margin: 0 }}>{event.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  )
}
