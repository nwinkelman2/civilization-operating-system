import { Warehouse, Plus } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import ResourceBar from '../components/ResourceBar'

const mockFacilities = [
  { id: 1, name: 'Central Farm', type: 'FOOD', output: 12, efficiency: 0.85, status: 'ACTIVE' },
  { id: 2, name: 'Water Pump Station', type: 'WATER', output: 20, efficiency: 0.92, status: 'ACTIVE' },
  { id: 3, name: 'Iron Mine', type: 'MINERALS', output: 8, efficiency: 0.7, status: 'ACTIVE' },
  { id: 4, name: 'Solar Array', type: 'ENERGY', output: 15, efficiency: 0.88, status: 'ACTIVE' },
  { id: 5, name: 'Residential Block A', type: 'HOUSING', output: 25, efficiency: 0.9, status: 'ACTIVE' },
]

const mockProduction = { food: 12, water: 20, minerals: 8, energy: 15, housing: 25 }
const mockConsumption = { food: 8, water: 10, minerals: 5, energy: 12, housing: 20 }

export default function Production() {
  return (
    <Layout
      icon={<Warehouse size={24} color="#22c55e" />}
      title="Production"
      subtitle="Facilities, output, and consumption"
      actions={
        <button style={{
          display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.4rem 0.8rem',
          borderRadius: '0.5rem', border: 'none', background: '#22c55e', color: 'white',
          cursor: 'pointer', fontSize: '0.85rem'
        }}>
          <Plus size={16} /> Build Facility
        </button>
      }
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Net Resources">
          {Object.keys(mockProduction).map(key => {
            const net = (mockProduction as any)[key] - (mockConsumption as any)[key]
            return (
              <div key={key} style={{ marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                  <span style={{ textTransform: 'capitalize' }}>{key}</span>
                  <span style={{ color: net >= 0 ? '#22c55e' : '#ef4444' }}>
                    {net >= 0 ? '+' : ''}{net.toFixed(1)}/tick
                  </span>
                </div>
                <div style={{ display: 'flex', gap: '0.5rem', fontSize: '0.75rem', color: '#64748b' }}>
                  <span>Prod: {(mockProduction as any)[key]}</span>
                  <span>Con: {(mockConsumption as any)[key]}</span>
                </div>
              </div>
            )
          })}
        </StatCard>

        <StatCard title="Facilities">
          {mockFacilities.map(f => (
            <div key={f.id} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '0.4rem 0', borderBottom: '1px solid #334155', fontSize: '0.85rem'
            }}>
              <div>
                <div style={{ fontWeight: 500 }}>{f.name}</div>
                <div style={{ fontSize: '0.75rem', color: '#64748b' }}>{f.type} · {Math.round(f.efficiency * 100)}% eff</div>
              </div>
              <span style={{ color: '#22c55e' }}>+{f.output}/tick</span>
            </div>
          ))}
        </StatCard>
      </div>
    </Layout>
  )
}
