import { useQuery } from '@apollo/client'
import { Trophy, Medal, Award } from 'lucide-react'
import { GET_LEADERBOARD } from '../graphql/queries'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'

const rankIcons = [<Trophy size={20} color="#f59e0b" />, <Medal size={20} color="#94a3b8" />, <Award size={20} color="#cd7f32" />]

export default function Leaderboard() {
  const { data, loading } = useQuery(GET_LEADERBOARD)

  const entries = data?.leaderboard || []

  const topScore = entries.length > 0 ? Math.max(...entries.map((e: any) => e.reputationScore || 0)) : 1

  return (
    <Layout
      icon={<Trophy size={24} color="#f59e0b" />}
      title="Leaderboard"
      subtitle="Civilization rankings by reputation score"
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Stats">
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Civilizations</span><span>{entries.length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Top Score</span><span style={{ color: '#f59e0b' }}>{topScore.toFixed(0)}</span>
            </div>
          </div>
        </StatCard>
      </div>

      {loading ? (
        <div style={{ color: '#64748b', textAlign: 'center', padding: '3rem' }}>Loading...</div>
      ) : entries.length === 0 ? (
        <div style={{ color: '#64748b', textAlign: 'center', padding: '3rem' }}>No civilizations ranked yet</div>
      ) : (
        entries.map((entry: any, i: number) => (
          <div key={entry.civilizationId} style={{
            background: '#1e293b', border: '1px solid #334155', borderRadius: '0.75rem',
            padding: '1rem', marginBottom: '0.75rem',
            display: 'flex', alignItems: 'center', gap: '1rem'
          }}>
            <div style={{
              width: 44, height: 44, borderRadius: '50%',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              background: i < 3 ? '#0f172a' : 'transparent',
              fontSize: i < 3 ? undefined : '1.1rem',
              fontWeight: 700, color: '#64748b'
            }}>
              {i < 3 ? rankIcons[i] : `#${entry.rank}`}
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 600 }}>{entry.name}</div>
              <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
                Population: {entry.population?.toLocaleString() || 0}
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="resource-bar" style={{ width: 120 }}>
                <div className="resource-bar-fill" style={{
                  width: `${(entry.reputationScore / topScore) * 100}%`,
                  background: '#f59e0b'
                }} />
              </div>
              <div style={{ fontSize: '0.85rem', color: '#f59e0b', fontWeight: 600, marginTop: '0.2rem' }}>
                {entry.reputationScore?.toFixed(0)}
              </div>
            </div>
          </div>
        ))
      )}
    </Layout>
  )
}
