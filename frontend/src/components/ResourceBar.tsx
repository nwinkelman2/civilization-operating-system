interface ResourceBarProps {
  label: string
  value: number
  max?: number
  color?: string
}

const defaultColors: Record<string, string> = {
  food: '#22c55e', water: '#0ea5e9', minerals: '#f59e0b',
  energy: '#eab308', housing: '#a78bfa',
}

export default function ResourceBar({ label, value, max = 200, color }: ResourceBarProps) {
  const barColor = color || defaultColors[label] || '#0ea5e9'

  return (
    <div style={{ marginBottom: '0.75rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
        <span style={{ textTransform: 'capitalize' }}>{label}</span>
        <span>{value.toFixed(1)}</span>
      </div>
      <div className="resource-bar">
        <div className="resource-bar-fill" style={{
          width: `${Math.min(100, (value / max) * 100)}%`,
          background: barColor
        }} />
      </div>
    </div>
  )
}
