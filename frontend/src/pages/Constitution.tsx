import { useState } from 'react'
import { ScrollText, Plus, Pencil } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

const mockRules = [
  { id: 1, category: 'RESOURCE', name: 'Resource Cap', description: 'Maximum resource storage per tier', enabled: true },
  { id: 2, category: 'SOCIAL', name: 'Population Growth', description: 'Population growth rate modifier', enabled: true },
  { id: 3, category: 'MILITARY', name: 'War Declaration', description: 'Requires council approval for war', enabled: false },
  { id: 4, category: 'DIPLOMACY', name: 'Trade Embargo', description: 'Auto-embargo on hostile civilizations', enabled: true },
  { id: 5, category: 'ECONOMY', name: 'Market Tax', description: 'Base tax rate on marketplace trades', enabled: true },
]

const mockCommittees = [
  { id: 1, name: 'High Council', members: 7, focus: 'Strategic decisions, war, diplomacy' },
  { id: 2, name: 'Economic Board', members: 5, focus: 'Trade, taxation, resource allocation' },
  { id: 3, name: 'Research Institute', members: 4, focus: 'Technology, education, innovation' },
]

export default function Constitution() {
  const [activeTab, setActiveTab] = useState<'rules' | 'committees'>('rules')

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
                <span>Enabled: {mockRules.filter(r => r.enabled).length}</span>
                <span>Total: {mockRules.length}</span>
              </div>
            </StatCard>
          </div>

          {mockRules.map(rule => (
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
                  }}>{rule.category}</span>
                  <span style={{ fontWeight: 600 }}>{rule.name}</span>
                </div>
                <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{rule.description}</div>
              </div>
              <label style={{ position: 'relative', display: 'inline-block', width: 40, height: 22, cursor: 'pointer' }}>
                <input type="checkbox" checked={rule.enabled} readOnly style={{ opacity: 0, width: 0, height: 0 }} />
                <span style={{
                  position: 'absolute', inset: 0, borderRadius: 22, transition: '0.3s',
                  background: rule.enabled ? '#22c55e' : '#475569'
                }}>
                  <span style={{
                    position: 'absolute', height: 18, width: 18, borderRadius: '50%',
                    background: 'white', top: 2, transition: '0.3s',
                    left: rule.enabled ? 20 : 2
                  }} />
                </span>
              </label>
            </div>
          ))}
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
