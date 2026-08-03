import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import NeedsProgress from '../components/NeedsProgress'

describe('NeedsProgress', () => {
  it('renders title and default needs bars', () => {
    render(<NeedsProgress />)
    expect(screen.getByText('Population Needs')).toBeInTheDocument()
    expect(screen.getByText('Housing')).toBeInTheDocument()
    expect(screen.getByText('Food')).toBeInTheDocument()
    expect(screen.getByText('Water')).toBeInTheDocument()
    expect(screen.getByText('Energy')).toBeInTheDocument()
  })

  it('renders custom percentage indicators correctly', () => {
    const customNeeds = { housing: 95, food: 45, water: 70, energy: 30 }
    render(<NeedsProgress needs={customNeeds} title="Custom Needs" />)
    expect(screen.getByText('Custom Needs')).toBeInTheDocument()
    expect(screen.getByText('95%')).toBeInTheDocument()
    expect(screen.getByText('45%')).toBeInTheDocument()
    expect(screen.getByText('70%')).toBeInTheDocument()
    expect(screen.getByText('30%')).toBeInTheDocument()
  })
})
