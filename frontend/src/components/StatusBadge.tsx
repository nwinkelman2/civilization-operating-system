interface StatusBadgeProps {
  status: string
  activeColor?: string
}

export default function StatusBadge({ status, activeColor = '#22c55e' }: StatusBadgeProps) {
  const isActive = status === 'ACTIVE' || status === 'ACTIVE_EXPERIMENT'
  return (
    <span style={{
      padding: '0.15rem 0.5rem', borderRadius: '1rem', fontSize: '0.7rem',
      background: isActive ? '#065f46' : '#334155',
      color: isActive ? activeColor : '#94a3b8'
    }}>
      {status}
    </span>
  )
}
