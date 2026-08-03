import React, { Suspense, lazy, useState, useEffect } from 'react'
import { Routes, Route, useLocation, Navigate } from 'react-router-dom'
import { Globe, Menu } from 'lucide-react'
import Dashboard from './pages/Dashboard'
import Sidebar from './components/Sidebar'

const CivilizationDetail = lazy(() => import('./pages/CivilizationDetail'))
const NexusMesh = lazy(() => import('./pages/NexusMesh'))
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

export default function App() {
  const [mobileNavOpen, setMobileNavOpen] = useState(false)
  const location = useLocation()

  // Close the mobile drawer whenever the route changes (e.g. after tapping a nav link)
  useEffect(() => {
    setMobileNavOpen(false)
  }, [location.pathname])

  // Lock body scroll while the mobile drawer is open
  useEffect(() => {
    document.body.style.overflow = mobileNavOpen ? 'hidden' : ''
    return () => { document.body.style.overflow = '' }
  }, [mobileNavOpen])

  // Close the drawer on Escape for keyboard users
  useEffect(() => {
    if (!mobileNavOpen) return
    const onKeyDown = (e: KeyboardEvent) => { if (e.key === 'Escape') setMobileNavOpen(false) }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [mobileNavOpen])

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <header className="mobile-header">
        <button
          onClick={() => setMobileNavOpen(true)}
          aria-label="Open navigation menu"
          aria-expanded={mobileNavOpen}
          aria-controls="mobile-nav"
          style={{
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            width: 44, height: 44, background: 'transparent', border: 'none',
            color: '#e2e8f0', cursor: 'pointer'
          }}
        >
          <Menu size={22} />
        </button>
        <Globe size={22} color="#0ea5e9" />
        <span style={{ fontWeight: 700, fontSize: '1.05rem' }}>Civ:OS</span>
      </header>

      <Sidebar isOpen={mobileNavOpen} onClose={() => setMobileNavOpen(false)} />

      <main
        className="md:ml-[240px] px-6 pb-6 pt-20 md:pt-6"
        style={{ flex: 1, overflowY: 'auto', overflowX: 'hidden', minWidth: 0 }}
      >
        <Suspense fallback={<PageLoader />}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/civilizations" element={<Dashboard />} />
            <Route path="/civilization/:id" element={<CivilizationDetail />} />
            <Route path="/civilizations/:id" element={<CivilizationDetail />} />
            <Route path="/nexus" element={<NexusMesh />} />
            <Route path="/voxtex" element={<Navigate to="/nexus" replace />} />
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
