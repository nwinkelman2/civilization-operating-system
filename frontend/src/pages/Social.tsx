import { Heart, Users, MessageCircle, TrendingUp } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import DataTable from '../components/DataTable'
import ResourceChart from '../components/ResourceChart'

const mockCitizens = [
  { id: 1, name: 'Elena', role: 'Farmer', happiness: 78, productivity: 0.85, skill: 'Agriculture' },
  { id: 2, name: 'Marcus', role: 'Engineer', happiness: 65, productivity: 0.72, skill: 'Engineering' },
  { id: 3, name: 'Sofia', role: 'Merchant', happiness: 90, productivity: 0.95, skill: 'Trade' },
  { id: 4, name: 'Kael', role: 'Scholar', happiness: 82, productivity: 0.88, skill: 'Research' },
  { id: 5, name: 'Nadia', role: 'Builder', happiness: 70, productivity: 0.78, skill: 'Construction' },
]

const happinessByRole = [
  { name: 'Farmer', happiness: 78 },
  { name: 'Engineer', happiness: 65 },
  { name: 'Merchant', happiness: 90 },
  { name: 'Scholar', happiness: 82 },
  { name: 'Builder', happiness: 70 },
]

export default function Social() {
  const avgHappiness = mockCitizens.reduce((s, c) => s + c.happiness, 0) / mockCitizens.length

  return (
    <Layout
      icon={<Heart size={24} color="#ef4444" />}
      title="Social"
      subtitle="Citizens, happiness, and skills"
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Overview">
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Population</span><span style={{ color: '#e2e8f0' }}>{mockCitizens.length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Avg Happiness</span><span style={{ color: '#ef4444' }}>{avgHappiness.toFixed(1)}%</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Avg Productivity</span><span style={{ color: '#22c55e' }}>{(mockCitizens.reduce((s, c) => s + c.productivity, 0) / mockCitizens.length * 100).toFixed(0)}%</span>
            </div>
          </div>
        </StatCard>

        <StatCard title="Happiness by Role">
          <ResourceChart
            data={happinessByRole}
            dataKeys={[{ key: 'happiness', color: '#ef4444' }]}
          />
        </StatCard>
      </div>

      <StatCard title="Citizens">
        <DataTable
          columns={[
            { key: 'name', label: 'Name' },
            { key: 'role', label: 'Role' },
            { key: 'happiness', label: 'Happiness', render: (c: any) => (
              <span style={{ color: c.happiness >= 75 ? '#22c55e' : c.happiness >= 50 ? '#eab308' : '#ef4444' }}>
                {c.happiness}%
              </span>
            )},
            { key: 'productivity', label: 'Productivity', render: (c: any) => `${Math.round(c.productivity * 100)}%` },
            { key: 'skill', label: 'Skill' },
          ]}
          data={mockCitizens}
        />
      </StatCard>
    </Layout>
  )
}
