import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import StatusBadge from '../components/StatusBadge'

describe('StatusBadge', () => {
  it('renders ACTIVE status with green styling', () => {
    render(<StatusBadge status="ACTIVE" />)
    const badge = screen.getByText('ACTIVE')
    expect(badge).toBeInTheDocument()
    expect(badge).toHaveStyle({ color: '#22c55e' })
  })

  it('renders PENDING status with neutral styling', () => {
    render(<StatusBadge status="PENDING" />)
    const badge = screen.getByText('PENDING')
    expect(badge).toBeInTheDocument()
    expect(badge).toHaveStyle({ color: '#94a3b8' })
  })

  it('renders custom active color', () => {
    render(<StatusBadge status="ACTIVE" activeColor="#0ea5e9" />)
    const badge = screen.getByText('ACTIVE')
    expect(badge).toHaveStyle({ color: '#0ea5e9' })
  })

  it('accepts ACTIVE_EXPERIMENT as active', () => {
    render(<StatusBadge status="ACTIVE_EXPERIMENT" />)
    const badge = screen.getByText('ACTIVE_EXPERIMENT')
    expect(badge).toHaveStyle({ color: '#22c55e' })
  })
})
