import React from 'react'
import { Home, Utensils, Droplets, Zap } from 'lucide-react'

export interface PopulationNeeds {
  housing?: number
  food?: number
  water?: number
  energy?: number
  [key: string]: number | undefined
}

interface NeedsProgressProps {
  needs?: PopulationNeeds
  title?: string
}

interface NeedItem {
  key: string
  label: string
  value: number
  icon: React.ReactNode
  color: string
}

export default function NeedsProgress({ needs = {}, title = 'Population Needs' }: NeedsProgressProps) {
  const housingVal = needs.housing ?? 85
  const foodVal = needs.food ?? 92
  const waterVal = needs.water ?? 78
  const energyVal = needs.energy ?? 88

  const items: NeedItem[] = [
    { key: 'housing', label: 'Housing', value: housingVal, icon: <Home size={16} color="#a78bfa" />, color: '#a78bfa' },
    { key: 'food', label: 'Food', value: foodVal, icon: <Utensils size={16} color="#22c55e" />, color: '#22c55e' },
    { key: 'water', label: 'Water', value: waterVal, icon: <Droplets size={16} color="#0ea5e9" />, color: '#0ea5e9' },
    { key: 'energy', label: 'Energy', value: energyVal, icon: <Zap size={16} color="#eab308" />, color: '#eab308' },
  ]

  const getStatus = (val: number) => {
    if (val >= 80) return { label: 'Optimal', color: '#22c55e', bg: 'rgba(34, 197, 94, 0.15)' }
    if (val >= 60) return { label: 'Satisfied', color: '#0ea5e9', bg: 'rgba(14, 165, 233, 0.15)' }
    if (val >= 40) return { label: 'Warning', color: '#eab308', bg: 'rgba(234, 179, 8, 0.15)' }
    return { label: 'Critical', color: '#ef4444', bg: 'rgba(239, 68, 68, 0.15)' }
  }

  return (
    <div style={{
      background: '#1e293b',
      border: '1px solid #334155',
      borderRadius: '0.75rem',
      padding: '1.25rem'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#f8fafc', margin: 0 }}>{title}</h3>
        <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Real-time Satisfaction</span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {items.map(item => {
          const clampedVal = Math.min(100, Math.max(0, item.value))
          const status = getStatus(clampedVal)

          return (
            <div key={item.key} style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.85rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#e2e8f0', fontWeight: 500 }}>
                  {item.icon}
                  <span>{item.label}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <span style={{
                    fontSize: '0.7rem',
                    padding: '0.1rem 0.4rem',
                    borderRadius: '0.25rem',
                    background: status.bg,
                    color: status.color,
                    fontWeight: 600
                  }}>
                    {status.label}
                  </span>
                  <span style={{ fontWeight: 600, color: '#f8fafc', minWidth: '2.5rem', textAlign: 'right' }}>
                    {clampedVal}%
                  </span>
                </div>
              </div>

              <div style={{
                height: '8px',
                width: '100%',
                background: '#0f172a',
                borderRadius: '4px',
                overflow: 'hidden',
                position: 'relative'
              }}>
                <div
                  style={{
                    height: '100%',
                    width: `${clampedVal}%`,
                    background: item.color,
                    borderRadius: '4px',
                    transition: 'width 0.5s ease-in-out'
                  }}
                />
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
