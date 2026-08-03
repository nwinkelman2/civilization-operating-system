import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import BiosphereGauges from '../components/BiosphereGauges'

describe('BiosphereGauges', () => {
  it('renders default biosphere gauge metrics', () => {
    render(<BiosphereGauges />)
    expect(screen.getByText('Biosphere Ecosystem Metrics')).toBeInTheDocument()
    expect(screen.getByText('Air Quality Index (AQI)')).toBeInTheDocument()
    expect(screen.getByText('Hydro & Soil pH Level')).toBeInTheDocument()
    expect(screen.getByText('Soil Fertility Index')).toBeInTheDocument()
    expect(screen.getByText('Biosphere Industrial Drift')).toBeInTheDocument()
  })

  it('renders custom metric values', () => {
    render(
      <BiosphereGauges
        metrics={{
          aqi: 25,
          phLevel: 7.0,
          soilFertility: 90,
          industrialDrift: 10,
        }}
      />
    )
    expect(screen.getByText('25')).toBeInTheDocument()
    expect(screen.getByText('7.0')).toBeInTheDocument()
    expect(screen.getByText('90%')).toBeInTheDocument()
    expect(screen.getByText('10%')).toBeInTheDocument()
  })

  it('evaluates threshold statuses correctly', () => {
    render(
      <BiosphereGauges
        metrics={{
          aqi: 120, // Poor (>100)
          phLevel: 7.2,
          soilFertility: 80,
          industrialDrift: 15,
        }}
      />
    )
    expect(screen.getByText('Unhealthy')).toBeInTheDocument()
  })
})
