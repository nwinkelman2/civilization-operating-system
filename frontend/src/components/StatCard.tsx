import type { ReactNode } from 'react'

interface StatCardProps {
  icon?: ReactNode
  title: string
  children: ReactNode
  className?: string
  onClick?: () => void
}

export default function StatCard({ icon, title, children, className, onClick }: StatCardProps) {
  return (
    <div
      className={`stat-card ${className || ''}`}
      style={{ cursor: onClick ? 'pointer' : undefined }}
      onClick={onClick}
    >
      {title && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
          {icon}
          <span style={{ fontWeight: 600 }}>{title}</span>
        </div>
      )}
      {children}
    </div>
  )
}
