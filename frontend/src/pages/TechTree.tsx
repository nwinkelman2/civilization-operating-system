import { useState, useEffect } from 'react'
import { Beaker, Lock, CheckCircle2, Loader2, AlertCircle, Inbox } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

export default function TechTree() {
  const [technologies, setTechnologies] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedTech, setSelectedTech] = useState<number | string | null>(null)

  useEffect(() => {
    setLoading(true)
    fetch('/api/v1/technologies')
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setTechnologies(Array.isArray(data) ? data : (data.content || []))
        setError(null)
      })
      .catch(err => {
        setError(err.message || 'Failed to fetch technologies')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <Layout icon={<Beaker size={24} color="#60a5fa" />} title="Technology Tree" subtitle="Research and technological advancement">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading technology tree...</span>
        </div>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout icon={<Beaker size={24} color="#60a5fa" />} title="Technology Tree" subtitle="Research and technological advancement">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Failed to load technologies: {error}</span>
        </div>
      </Layout>
    )
  }

  const allResearched = technologies.filter(t => t.status === 'COMPLETED' || t.researched === true).length
  const total = technologies.length

  // Group technologies by tier/era
  const eraMap: Record<string, any[]> = {}
  technologies.forEach(t => {
    const tier = t.tier || 1
    const eraName = tier === 1 ? 'Ancient' : tier === 2 ? 'Classical' : tier === 3 ? 'Medieval' : `Tier ${tier}`
    if (!eraMap[eraName]) eraMap[eraName] = []
    eraMap[eraName].push(t)
  })

  const eraColors: Record<string, string> = {
    Ancient: '#a78bfa',
    Classical: '#60a5fa',
    Medieval: '#f59e0b',
  }

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
              width: `${total > 0 ? (allResearched / total) * 100 : 0}%`,
              background: 'linear-gradient(90deg, #60a5fa, #a78bfa)'
            }} />
          </div>
        </StatCard>
      </div>

      {total === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem' }}>
          <Inbox size={32} />
          <span>No technologies available</span>
        </div>
      ) : (
        Object.entries(eraMap).map(([era, techs]) => {
          const color = eraColors[era] || '#38bdf8'
          return (
            <div key={era} style={{ marginBottom: '2rem' }}>
              <h2 style={{ fontSize: '1.1rem', fontWeight: 600, color, marginBottom: '1rem' }}>
                {era} Era
              </h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '1rem' }}>
                {techs.map(tech => {
                  const isResearched = tech.status === 'COMPLETED' || tech.researched === true
                  return (
                    <div
                      key={tech.id}
                      className="stat-card"
                      style={{
                        cursor: 'pointer',
                        borderColor: selectedTech === tech.id ? color : undefined,
                        opacity: isResearched ? 1 : 0.7
                      }}
                      onClick={() => setSelectedTech(tech.id)}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                        {isResearched ? (
                          <CheckCircle2 size={18} color="#22c55e" />
                        ) : (
                          <Lock size={18} color="#64748b" />
                        )}
                        <span style={{ fontWeight: 600 }}>{tech.name}</span>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '0.5rem' }}>
                        {tech.description || 'No description available'}
                      </div>
                      <div style={{ fontSize: '0.8rem', color: isResearched ? '#22c55e' : '#eab308' }}>
                        {isResearched ? 'Researched' : `${tech.researchCost || tech.cost || 50} research points`}
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )
        })
      )}
    </Layout>
  )
}
