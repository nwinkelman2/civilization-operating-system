import { lazy, Suspense } from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import { BarChart3, Globe, Network, Trees, Warehouse, Handshake, ArrowLeftRight, ScrollText, Heart, Beaker, Gamepad2, Trophy } from 'lucide-react'
import Dashboard from './pages/Dashboard'

const CivilizationDetail = lazy(() => import('./pages/CivilizationDetail'))
const VoxtexMesh = lazy(() => import('./pages/VoxtexMesh'))
const ResourceMap = lazy(() => import('./pages/ResourceMap'))
const Trade = lazy(() => import('./pages/Trade'))
const Constitution = lazy(() => import('./pages/Constitution'))
const TechTree = lazy(() => import('./pages/TechTree'))
const Production = lazy(() => import('./pages/Production'))
const Logistics = lazy(() => import('./pages/Logistics'))
const Leaderboard = lazy(() => import('./pages/Leaderboard'))
const Simulation = lazy(() => import('./pages/Simulation'))
const Social = lazy(() => import('./pages/Social'))

const PageLoader = () => (
  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '60vh', color: '#64748b' }}>
    <div style={{ textAlign: 'center' }}>
      <div style={{ width: 32, height: 32, border: '3px solid #334155', borderTopColor: '#0ea5e9', borderRadius: '50%', animation: 'spin 0.8s linear infinite', margin: '0 auto 0.75rem' }} />
      <div style={{ fontSize: '0.85rem' }}>Loading...</div>
    </div>
  </div>
)

const navItems = [
  { path: '/', label: 'Dashboard', icon: BarChart3 },
  { path: '/civilizations', label: 'Civilizations', icon: Globe },
  { path: '/voxtex', label: 'Voxtex Mesh', icon: Network },
  { path: '/resources', label: 'Resources', icon: Trees },
  { path: '/production', label: 'Production', icon: Warehouse },
  { path: '/logistics', label: 'Logistics', icon: Handshake },
  { path: '/trade', label: 'Trade', icon: ArrowLeftRight },
  { path: '/constitution', label: 'Governance', icon: ScrollText },
  { path: '/social', label: 'Social', icon: Heart },
  { path: '/tech-tree', label: 'Technology', icon: Beaker },
  { path: '/simulation', label: 'Simulation', icon: Gamepad2 },
  { path: '/leaderboard', label: 'Leaderboard', icon: Trophy },
]

export default function App() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <nav style={{ width: 240, background: '#1e293b', borderRight: '1px solid #334155', padding: '1rem', overflowY: 'auto' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '2rem', padding: '0.5rem' }}>
          <Globe size={24} color="#0ea5e9" />
          <span style={{ fontWeight: 700, fontSize: '1.1rem' }}>Civ:OS</span>
        </div>
        {navItems.map(item => (
          <Link
            key={item.path}
            to={item.path}
            style={{
              display: 'flex', alignItems: 'center', gap: '0.75rem',
              padding: '0.6rem 0.75rem', borderRadius: '0.5rem',
              color: '#94a3b8', textDecoration: 'none', fontSize: '0.9rem',
              marginBottom: '0.25rem', transition: 'all 0.2s'
            }}
            onMouseEnter={e => { e.currentTarget.style.background = '#334155'; e.currentTarget.style.color = '#e2e8f0' }}
            onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = '#94a3b8' }}
          >
            <item.icon size={18} />
            {item.label}
          </Link>
        ))}
      </nav>
      <main style={{ flex: 1, padding: '1.5rem', overflowY: 'auto' }}>
        <Suspense fallback={<PageLoader />}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/civilizations" element={<Dashboard />} />
            <Route path="/civilization/:id" element={<CivilizationDetail />} />
            <Route path="/voxtex" element={<VoxtexMesh />} />
            <Route path="/resources" element={<ResourceMap />} />
            <Route path="/trade" element={<Trade />} />
            <Route path="/constitution" element={<Constitution />} />
            <Route path="/tech-tree" element={<TechTree />} />
            <Route path="/production" element={<Production />} />
            <Route path="/logistics" element={<Logistics />} />
            <Route path="/leaderboard" element={<Leaderboard />} />
            <Route path="/simulation" element={<Simulation />} />
            <Route path="/social" element={<Social />} />
            <Route path="*" element={<Dashboard />} />
          </Routes>
        </Suspense>
      </main>
    </div>
  )
}
