import React from 'react'
import { Leaf, Wind, Droplets, Factory, AlertTriangle, CheckCircle2, ShieldAlert } from 'lucide-react'

export interface BiosphereData {
  aqi?: number
  phLevel?: number
  soilFertility?: number
  industrialDrift?: number
}

interface BiosphereGaugesProps {
  metrics?: BiosphereData
}

interface GaugeConfig {
  id: string
  title: string
  subtitle: string
  value: number
  unit: string
  formattedValue: string
  icon: React.ReactNode
  min: number
  max: number
  percentage: number
  statusText: string
  color: string
  badgeBg: string
  thresholds: { green: string; yellow: string; red: string }
}

export const BiosphereGauges: React.FC<BiosphereGaugesProps> = ({ metrics = {} }) => {
  const aqi = metrics.aqi ?? 42
  const phLevel = metrics.phLevel ?? 7.2
  const soilFertility = metrics.soilFertility ?? 84
  const industrialDrift = metrics.industrialDrift ?? 15

  // AQI Evaluation (0 - 300)
  const getAqiConfig = (val: number): GaugeConfig => {
    const percentage = Math.min(100, Math.max(0, (val / 150) * 100))
    let color = '#22c55e'
    let statusText = 'Optimal (Good)'
    let badgeBg = 'rgba(34, 197, 94, 0.15)'

    if (val > 100) {
      color = '#ef4444'
      statusText = 'Unhealthy'
      badgeBg = 'rgba(239, 68, 68, 0.15)'
    } else if (val > 50) {
      color = '#f59e0b'
      statusText = 'Moderate Risk'
      badgeBg = 'rgba(245, 158, 11, 0.15)'
    }

    return {
      id: 'aqi',
      title: 'Air Quality Index (AQI)',
      subtitle: 'Atmospheric particulate and purity',
      value: val,
      unit: 'AQI',
      formattedValue: `${val}`,
      icon: <Wind size={20} color={color} />,
      min: 0,
      max: 150,
      percentage,
      statusText,
      color,
      badgeBg,
      thresholds: { green: '0-50 Good', yellow: '51-100 Fair', red: '>100 Poor' },
    }
  }

  // pH Evaluation (0 - 14)
  const getPhConfig = (val: number): GaugeConfig => {
    const percentage = Math.min(100, Math.max(0, (val / 14) * 100))
    let color = '#22c55e'
    let statusText = 'Optimal Balance'
    let badgeBg = 'rgba(34, 197, 94, 0.15)'

    if (val < 5.5 || val > 8.5) {
      color = '#ef4444'
      statusText = 'Critical Shift'
      badgeBg = 'rgba(239, 68, 68, 0.15)'
    } else if (val < 6.5 || val > 7.5) {
      color = '#f59e0b'
      statusText = 'Mild Imbalance'
      badgeBg = 'rgba(245, 158, 11, 0.15)'
    }

    return {
      id: 'ph',
      title: 'Hydro & Soil pH Level',
      subtitle: 'Acidity / Alkalinity ratio',
      value: val,
      unit: 'pH',
      formattedValue: `${val.toFixed(1)}`,
      icon: <Droplets size={20} color={color} />,
      min: 0,
      max: 14,
      percentage,
      statusText,
      color,
      badgeBg,
      thresholds: { green: '6.5 - 7.5 Neutral', yellow: '5.5-6.4 / 7.6-8.5', red: '<5.5 or >8.5 Extreme' },
    }
  }

  // Soil Fertility Evaluation (0 - 100%)
  const getSoilConfig = (val: number): GaugeConfig => {
    const percentage = Math.min(100, Math.max(0, val))
    let color = '#22c55e'
    let statusText = 'High Fertility'
    let badgeBg = 'rgba(34, 197, 94, 0.15)'

    if (val < 40) {
      color = '#ef4444'
      statusText = 'Severely Degraded'
      badgeBg = 'rgba(239, 68, 68, 0.15)'
    } else if (val < 70) {
      color = '#f59e0b'
      statusText = 'Moderate Depletion'
      badgeBg = 'rgba(245, 158, 11, 0.15)'
    }

    return {
      id: 'soil',
      title: 'Soil Fertility Index',
      subtitle: 'Nutrient & organic matter content',
      value: val,
      unit: '%',
      formattedValue: `${val}%`,
      icon: <Leaf size={20} color={color} />,
      min: 0,
      max: 100,
      percentage,
      statusText,
      color,
      badgeBg,
      thresholds: { green: '≥70% Rich', yellow: '40-69% Depleting', red: '<40% Low' },
    }
  }

  // Industrial Drift Evaluation (0 - 100%)
  const getDriftConfig = (val: number): GaugeConfig => {
    const percentage = Math.min(100, Math.max(0, val))
    let color = '#22c55e'
    let statusText = 'Nominal Drift'
    let badgeBg = 'rgba(34, 197, 94, 0.15)'

    if (val > 50) {
      color = '#ef4444'
      statusText = 'Critical Footprint'
      badgeBg = 'rgba(239, 68, 68, 0.15)'
    } else if (val > 20) {
      color = '#f59e0b'
      statusText = 'Elevated Impact'
      badgeBg = 'rgba(245, 158, 11, 0.15)'
    }

    return {
      id: 'drift',
      title: 'Biosphere Industrial Drift',
      subtitle: 'Technosphere environmental footprint',
      value: val,
      unit: '%',
      formattedValue: `${val}%`,
      icon: <Factory size={20} color={color} />,
      min: 0,
      max: 100,
      percentage,
      statusText,
      color,
      badgeBg,
      thresholds: { green: '≤20% Stable', yellow: '21-50% Elevated', red: '>50% Critical' },
    }
  }

  const gauges = [
    getAqiConfig(aqi),
    getPhConfig(phLevel),
    getSoilConfig(soilFertility),
    getDriftConfig(industrialDrift),
  ]

  // Render SVG Radial Gauge
  const renderRadialGauge = (gauge: GaugeConfig) => {
    const size = 110
    const strokeWidth = 10
    const center = size / 2
    const radius = center - strokeWidth
    const circumference = 2 * Math.PI * radius
    // Semi-circle gauge (180 degree arc)
    const arcLength = circumference * 0.75
    const strokeDashoffset = arcLength - (arcLength * gauge.percentage) / 100

    return (
      <div style={{ position: 'relative', width: size, height: size, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <svg width={size} height={size} style={{ transform: 'rotate(135deg)' }}>
          {/* Background Track Arc */}
          <circle
            cx={center}
            cy={center}
            r={radius}
            fill="none"
            stroke="#334155"
            strokeWidth={strokeWidth}
            strokeDasharray={`${arcLength} ${circumference}`}
            strokeLinecap="round"
          />
          {/* Active Value Arc */}
          <circle
            cx={center}
            cy={center}
            r={radius}
            fill="none"
            stroke={gauge.color}
            strokeWidth={strokeWidth}
            strokeDasharray={`${arcLength} ${circumference}`}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            style={{ transition: 'stroke-dashoffset 0.8s ease, stroke 0.3s ease' }}
          />
        </svg>
        {/* Center Display Value */}
        <div
          style={{
            position: 'absolute',
            textAlign: 'center',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <span style={{ fontSize: '1.25rem', fontWeight: 700, color: '#f8fafc' }}>{gauge.formattedValue}</span>
          <span style={{ fontSize: '0.65rem', color: '#94a3b8', textTransform: 'uppercase' }}>{gauge.unit}</span>
        </div>
      </div>
    )
  }

  return (
    <div style={{ marginBottom: '1.5rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h2 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#f8fafc', display: 'flex', alignItems: 'center', gap: '0.5rem', margin: 0 }}>
          <Leaf size={20} color="#22c55e" />
          Biosphere Ecosystem Metrics
        </h2>
        <div style={{ fontSize: '0.75rem', color: '#94a3b8', display: 'flex', gap: '0.75rem' }}>
          <span style={{ color: '#22c55e' }}>● Green (Optimal)</span>
          <span style={{ color: '#f59e0b' }}>● Yellow (Warning)</span>
          <span style={{ color: '#ef4444' }}>● Red (Critical)</span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1rem' }}>
        {gauges.map((gauge) => (
          <div
            key={gauge.id}
            className="stat-card"
            style={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              background: '#1e293b',
              border: '1px solid #334155',
              borderRadius: '0.75rem',
              padding: '1.25rem',
            }}
          >
            {/* Header with Title & Icon */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.9rem', color: '#f8fafc' }}>{gauge.title}</div>
                <div style={{ fontSize: '0.7rem', color: '#64748b' }}>{gauge.subtitle}</div>
              </div>
              <div style={{ padding: '0.35rem', borderRadius: '0.5rem', background: 'rgba(15, 23, 42, 0.6)' }}>
                {gauge.icon}
              </div>
            </div>

            {/* Radial Gauge Center */}
            <div style={{ display: 'flex', justifyContent: 'center', margin: '0.5rem 0' }}>
              {renderRadialGauge(gauge)}
            </div>

            {/* Status Pill Badge */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0.4rem 0.65rem',
                borderRadius: '0.5rem',
                background: gauge.badgeBg,
                border: `1px solid ${gauge.color}40`,
                fontSize: '0.75rem',
                marginTop: '0.5rem',
              }}
            >
              <span style={{ fontWeight: 600, color: gauge.color }}>{gauge.statusText}</span>
              {gauge.color === '#22c55e' ? (
                <CheckCircle2 size={14} color={gauge.color} />
              ) : gauge.color === '#f59e0b' ? (
                <AlertTriangle size={14} color={gauge.color} />
              ) : (
                <ShieldAlert size={14} color={gauge.color} />
              )}
            </div>

            {/* Threshold Legend */}
            <div style={{ fontSize: '0.65rem', color: '#64748b', textAlign: 'center', marginTop: '0.5rem' }}>
              Limits: <span style={{ color: '#22c55e' }}>{gauge.thresholds.green}</span> |{' '}
              <span style={{ color: '#f59e0b' }}>{gauge.thresholds.yellow}</span> |{' '}
              <span style={{ color: '#ef4444' }}>{gauge.thresholds.red}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default BiosphereGauges
