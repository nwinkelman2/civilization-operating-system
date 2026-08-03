import { useState, useEffect } from 'react'
import { ScrollText, Plus, Pencil, Loader2, AlertCircle, Inbox } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

const mockCommittees = [
  { id: 1, name: 'High Council', members: 7, focus: 'Strategic decisions, war, diplomacy' },
  { id: 2, name: 'Economic Board', members: 5, focus: 'Trade, taxation, resource allocation' },
  { id: 3, name: 'Research Institute', members: 4, focus: 'Technology, education, innovation' },
]

export default function Constitution() {
  const [activeTab, setActiveTab] = useState<'rules' | 'committees'>('rules')
  const [rules, setRules] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    fetch('/api/v1/rules')
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setRules(Array.isArray(data) ? data : (data.content || []))
        setError(null)
      })
      .catch(err => {
        setError(err.message || 'Failed to fetch rules')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading && activeTab === 'rules') {
    return (
      <Layout icon={<ScrollText size={24} color="#f59e0b" />} title="Governance" subtitle="Constitution, rules, and committees">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading constitutional rules...</span>
        </div>
      </Layout>
    )
  }

  if (error && activeTab === 'rules') {
    return (
      <Layout icon={<ScrollText size={24} color="#f59e0b" />} title="Governance" subtitle="Constitution, rules, and committees">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Failed to load constitutional rules: {error}</span>
        </div>
      </Layout>
    )
  }

  const enabledCount = rules.filter(r => r.status === 'ACTIVE' || r.status === 'PASSED' || r.enabled === true).length

  return (
    <Layout
      icon={<ScrollText size={24} color="#f59e0b" />}
      title="Governance"
      subtitle="Constitution, rules, and committees"
    >
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
        {['rules', 'committees'].map(tab => (
          <button key={tab} onClick={() => setActiveTab(tab as any)} style={{
            padding: '0.4rem 1rem', borderRadius: '0.5rem', border: '1px solid #334155',
            background: activeTab === tab ? '#0ea5e9' : 'transparent',
            color: activeTab === tab ? 'white' : '#94a3b8', cursor: 'pointer', fontSize: '0.85rem',
            textTransform: 'capitalize'
          }}>{tab}</button>
        ))}
      </div>

      {activeTab === 'rules' && (
        <>
          <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
            <StatCard
              title="Governing Rules"
              icon={<ScrollText size={18} color="#f59e0b" />}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: '#64748b' }}>
                <span>Active / Passed: {enabledCount}</span>
                <span>Total: {rules.length}</span>
              </div>
            </StatCard>
          </div>

          {rules.length === 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem' }}>
              <Inbox size={32} />
              <span>No constitutional rules found</span>
            </div>
          ) : (
            rules.map(rule => {
              const isActive = rule.status === 'ACTIVE' || rule.status === 'PASSED' || rule.enabled === true
              return (
                <div key={rule.id} style={{
                  background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem',
                  padding: '1rem', marginBottom: '0.75rem',
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center'
                }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                      <span style={{
                        padding: '0.1rem 0.4rem', borderRadius: '0.25rem', fontSize: '0.7rem',
                        background: '#334155', color: '#0ea5e9'
                      }}>{rule.sector || rule.category || 'GENERAL'}</span>
                      <span style={{ fontWeight: 600 }}>{rule.title || rule.name}</span>
                    </div>
                    <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{rule.description}</div>
                  </div>
                  <label style={{ position: 'relative', display: 'inline-block', width: 40, height: 22, cursor: 'pointer' }}>
                    <input type="checkbox" checked={isActive} readOnly style={{ opacity: 0, width: 0, height: 0 }} />
                    <span style={{
                      position: 'absolute', inset: 0, borderRadius: 22, transition: '0.3s',
                      background: isActive ? '#22c55e' : '#475569'
                    }}>
                      <span style={{
                        position: 'absolute', height: 18, width: 18, borderRadius: '50%',
                        background: 'white', top: 2, transition: '0.3s',
                        left: isActive ? 20 : 2
                      }} />
                    </span>
                  </label>
                </div>
              )
            })
          )}
        </>
      )}

      {activeTab === 'committees' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1rem' }}>
          {mockCommittees.map(c => (
            <StatCard key={c.id} title={c.name}>
              <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
                <div style={{ marginBottom: '0.5rem' }}>Members: <strong style={{ color: '#e2e8f0' }}>{c.members}</strong></div>
                <div>Focus: {c.focus}</div>
              </div>
            </StatCard>
          ))}
        </div>
      )}
    </Layout>
  )
}
