import { useState, useEffect } from 'react'
import { Link, useLocation } from 'react-router-dom'
import {
  BarChart3, Globe, Network, Trees, Warehouse, Handshake, ArrowLeftRight, ScrollText, Heart, Beaker, Gamepad2, Trophy, X
} from 'lucide-react'

export interface SidebarProps {
  isOpen?: boolean
  onClose?: () => void
}

export const navItems = [
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

export default function Sidebar({ isOpen = false, onClose }: SidebarProps) {
  const location = useLocation()
  const [isMobileViewport, setIsMobileViewport] = useState(() =>
    typeof window !== 'undefined' && typeof window.matchMedia === 'function'
      ? window.matchMedia('(max-width: 768px)').matches
      : false
  )

  useEffect(() => {
    if (typeof window.matchMedia !== 'function') return
    const mql = window.matchMedia('(max-width: 768px)')
    const onChange = (e: MediaQueryListEvent) => setIsMobileViewport(e.matches)
    mql.addEventListener('change', onChange)
    return () => mql.removeEventListener('change', onChange)
  }, [])

  const navOffscreen = isMobileViewport && !isOpen

  return (
    <>
      {isOpen && (
        <div
          className="sidebar-backdrop md:hidden"
          onClick={onClose}
          aria-hidden="true"
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.5)',
            backdropFilter: 'blur(4px)',
            WebkitBackdropFilter: 'blur(4px)',
            zIndex: 40
          }}
        />
      )}

      <nav
        id="mobile-nav"
        className={`sidebar-drawer ${isOpen ? 'translate-x-0 open' : '-translate-x-full'} md:translate-x-0`}
        inert={navOffscreen ? true : undefined}
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          bottom: 0,
          width: 240,
          maxWidth: '85vw',
          background: '#1e293b',
          borderRight: '1px solid #334155',
          padding: '1rem',
          overflowY: 'auto',
          zIndex: 50,
          transition: 'transform 0.2s ease-in-out'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '2rem', padding: '0.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Globe size={24} color="#0ea5e9" />
            <span style={{ fontWeight: 700, fontSize: '1.1rem' }}>Civ:OS</span>
          </div>
          <button
            className="mobile-close-btn md:hidden"
            onClick={onClose}
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

        {navItems.map(item => {
          const isActive = location.pathname === item.path
          return (
            <Link
              key={item.path}
              to={item.path}
              onClick={onClose}
              style={{
                display: 'flex', alignItems: 'center', gap: '0.75rem',
                padding: '0.6rem 0.75rem', minHeight: 44, borderRadius: '0.5rem',
                color: isActive ? '#0ea5e9' : '#94a3b8',
                background: isActive ? 'rgba(14, 165, 233, 0.1)' : 'transparent',
                textDecoration: 'none', fontSize: '0.9rem',
                marginBottom: '0.25rem', transition: 'all 0.2s'
              }}
              onMouseEnter={e => {
                if (!isActive) {
                  e.currentTarget.style.background = '#334155'
                  e.currentTarget.style.color = '#e2e8f0'
                }
              }}
              onMouseLeave={e => {
                if (!isActive) {
                  e.currentTarget.style.background = 'transparent'
                  e.currentTarget.style.color = '#94a3b8'
                }
              }}
            >
              <item.icon size={18} />
              {item.label}
            </Link>
          )
        })}
      </nav>
    </>
  )
}
