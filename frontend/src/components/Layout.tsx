import { ReactNode, useState, useEffect } from 'react'
import { Menu } from 'lucide-react'
import Sidebar from './Sidebar'

interface LayoutProps {
  icon?: ReactNode
  title?: string
  subtitle?: string
  actions?: ReactNode
  children: ReactNode
  showSidebar?: boolean
}

export default function Layout({ icon, title, subtitle, actions, children, showSidebar = false }: LayoutProps) {
  const [mobileNavOpen, setMobileNavOpen] = useState(false)

  useEffect(() => {
    if (!mobileNavOpen) return
    document.body.style.overflow = 'hidden'
    return () => { document.body.style.overflow = '' }
  }, [mobileNavOpen])

  useEffect(() => {
    if (!mobileNavOpen) return
    const onKeyDown = (e: KeyboardEvent) => { if (e.key === 'Escape') setMobileNavOpen(false) }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [mobileNavOpen])

  return (
    <div>
      {showSidebar && <Sidebar isOpen={mobileNavOpen} onClose={() => setMobileNavOpen(false)} />}
      <div style={{
        display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        marginBottom: '1.5rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          {showSidebar && (
            <button
              className="hamburger-btn md:hidden"
              onClick={() => setMobileNavOpen(prev => !prev)}
              aria-label="Open navigation menu"
              aria-expanded={mobileNavOpen}
              aria-controls="mobile-nav"
              style={{
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                width: 44, height: 44, background: 'transparent', border: 'none',
                color: '#e2e8f0', cursor: 'pointer', padding: 0
              }}
            >
              <Menu size={22} />
            </button>
          )}
          {icon}
          {(title || subtitle) && (
            <div>
              {title && <h1 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0 }}>{title}</h1>}
              {subtitle && <div style={{ color: '#64748b', fontSize: '0.85rem' }}>{subtitle}</div>}
            </div>
          )}
        </div>
        {actions && <div style={{ display: 'flex', gap: '0.5rem' }}>{actions}</div>}
      </div>
      {children}
    </div>
  )
}

