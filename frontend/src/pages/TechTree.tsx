import { useState } from 'react'
import { Beaker, Lock, CheckCircle2 } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

const techTreeData = [
  {
    era: 'Ancient', color: '#a78bfa', techs: [
      { id: 1, name: 'Agriculture', description: 'Unlock farming and food surplus', cost: 50, researched: true },
      { id: 2, name: 'Writing', description: 'Enable records and basic diplomacy', cost: 30, researched: true },
      { id: 3, name: 'Masonry', description: 'Improved building construction', cost: 40, researched: false },
    ]
  },
  {
    era: 'Classical', color: '#60a5fa', techs: [
      { id: 4, name: 'Iron Working', description: 'Stronger military units', cost: 80, researched: false },
      { id: 5, name: 'Philosophy', description: 'Boost to cultural output', cost: 60, researched: false },
    ]
  },
  {
    era: 'Medieval', color: '#f59e0b', techs: [
      { id: 6, name: 'Feudalism', description: 'Specialized economic roles', cost: 120, researched: false },
    ]
  },
]

export default function TechTree() {
  const [selectedTech, setSelectedTech] = useState<number | null>(null)
  const allResearched = techTreeData.flatMap(e => e.techs).filter(t => t.researched).length
  const total = techTreeData.flatMap(e => e.techs).length

  return (
    <Layout
      icon={<Beaker size={24} color="#60a5fa" />}
      title="Technology Tree"
      subtitle={`${allResearched}/${total} technologies researched`}
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Research Progress">
          <div className="resource-bar" style={{ height: 12, marginTop: '0.5rem' }}>
            <div className="resource-bar-fill" style={{
              width: `${(allResearched / total) * 100}%`,
              background: 'linear-gradient(90deg, #60a5fa, #a78bfa)'
            }} />
          </div>
        </StatCard>
      </div>

      {techTreeData.map(era => (
        <div key={era.era} style={{ marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '1.1rem', fontWeight: 600, color: era.color, marginBottom: '1rem' }}>
            {era.era} Era
          </h2>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '1rem' }}>
            {era.techs.map(tech => (
              <div
                key={tech.id}
                className="stat-card"
                style={{
                  cursor: 'pointer',
                  borderColor: selectedTech === tech.id ? era.color : undefined,
                  opacity: tech.researched ? 1 : 0.7
                }}
                onClick={() => setSelectedTech(tech.id)}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                  {tech.researched ? (
                    <CheckCircle2 size={18} color="#22c55e" />
                  ) : (
                    <Lock size={18} color="#64748b" />
                  )}
                  <span style={{ fontWeight: 600 }}>{tech.name}</span>
                </div>
                <div style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '0.5rem' }}>
                  {tech.description}
                </div>
                <div style={{
                  fontSize: '0.8rem', color: tech.researched ? '#22c55e' : '#eab308'
                }}>
                  {tech.researched ? 'Researched' : `${tech.cost} research points`}
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </Layout>
  )
}
