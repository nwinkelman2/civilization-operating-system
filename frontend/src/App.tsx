import React, { Suspense, lazy, useState, useEffect } from 'react'
import { Routes, Route, Link, useLocation, Navigate } from 'react-router-dom'
import { BarChart3, Globe, Network, Trees, Warehouse, Handshake, ArrowLeftRight, ScrollText, Heart, Beaker, Gamepad2, Trophy, Menu, X } from 'lucide-react'
import Dashboard from './pages/Dashboard'

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

const navItems = [
  { path: '/', label: 'Dashboard', icon: BarChart3 },
  { path: '/civilizations', label: 'Civilizations', icon: Globe },
  { path: '/nexus', label: 'Nexus Mesh', icon: Network },
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
  const [mobileNavOpen, setMobileNavOpen] = useState(false)
  const location = useLocation()

  // Track whether we're below the md breakpoint so we know when the drawer
  // is actually off-screen (vs. always-visible on desktop) for a11y purposes
  const [isMobileViewport, setIsMobileViewport] = useState(() =>
    typeof window !== 'undefined' && typeof window.matchMedia === 'function'
      ? window.matchMedia('(max-width: 767px)').matches
      : false
  )
  useEffect(() => {
    if (typeof window.matchMedia !== 'function') return
    const mql = window.matchMedia('(max-width: 767px)')
    const onChange = (e: MediaQueryListEvent) => setIsMobileViewport(e.matches)
    mql.addEventListener('change', onChange)
    return () => mql.removeEventListener('change', onChange)
  }, [])

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

  // True only when the drawer is actually off-screen (mobile viewport + closed).
  // On desktop the nav is always visible, so it must never be inert/hidden there.
  const navOffscreen = isMobileViewport && !mobileNavOpen

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Mobile top bar: shown < 768px, hosts the hamburger toggle */}
      <header
        className="md:hidden"
        style={{
          position: 'fixed', top: 0, left: 0, right: 0, height: 56, zIndex: 40,
          display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0 0.75rem',
          background: '#1e293b', borderBottom: '1px solid #334155'
        }}
      >
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

      {/* Backdrop for the mobile slide-out nav */}
      {mobileNavOpen && (
        <div
          className="md:hidden"
          onClick={() => setMobileNavOpen(false)}
          aria-hidden="true"
          style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 40 }}
        />
      )}

      <nav
        id="mobile-nav"
        className={`${mobileNavOpen ? 'translate-x-0' : '-translate-x-full'} md:translate-x-0`}
        inert={navOffscreen}
        style={{
          position: 'fixed', top: 0, left: 0, bottom: 0,
          width: 240, maxWidth: '85vw', background: '#1e293b', borderRight: '1px solid #334155',
          padding: '1rem', overflowY: 'auto', zIndex: 50,
          transition: 'transform 0.2s ease-in-out'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '2rem', padding: '0.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Globe size={24} color="#0ea5e9" />
            <span style={{ fontWeight: 700, fontSize: '1.1rem' }}>Civ:OS</span>
          </div>
          <button
            className="md:hidden"
            onClick={() => setMobileNavOpen(false)}
            aria-label="Close navigation menu"
            style={{
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              width: 44, height: 44, background: 'transparent', border: 'none',
              color: '#94a3b8', cursor: 'pointer'
            }}
          >
            <X size={20} />
          </button>
        </div>
        {navItems.map(item => (
          <Link
            key={item.path}
            to={item.path}
            style={{
              display: 'flex', alignItems: 'center', gap: '0.75rem',
              padding: '0.6rem 0.75rem', minHeight: 44, borderRadius: '0.5rem',
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

      <main
        className="md:ml-[240px] px-6 pb-6 pt-20 md:pt-6"
        style={{ flex: 1, overflowY: 'auto', overflowX: 'hidden', minWidth: 0 }}
      >
        <Suspense fallback={<PageLoader />}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/civilizations" element={<Dashboard />} />
            <Route path="/civilization/:id" element={<CivilizationDetail />} />
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
