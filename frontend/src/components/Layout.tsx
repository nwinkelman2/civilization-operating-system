import { ReactNode } from 'react'

interface LayoutProps {
  icon: ReactNode
  title: string
  subtitle?: string
  actions?: ReactNode
  children: ReactNode
}

export default function Layout({ icon, title, subtitle, actions, children }: LayoutProps) {
  return (
    <div>
      <div style={{
        display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        marginBottom: '1.5rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          {icon}
          <div>
            <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{title}</h1>
            {subtitle && <div style={{ color: '#64748b', fontSize: '0.85rem' }}>{subtitle}</div>}
          </div>
        </div>
        {actions && <div style={{ display: 'flex', gap: '0.5rem' }}>{actions}</div>}
      </div>
      {children}
    </div>
  )
}
