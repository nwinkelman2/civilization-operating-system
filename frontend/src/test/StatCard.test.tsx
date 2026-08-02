import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import StatCard from '../components/StatCard'

describe('StatCard', () => {
  it('renders title and children', () => {
    render(<StatCard title="Resources"><div>Content</div></StatCard>)
    expect(screen.getByText('Resources')).toBeInTheDocument()
    expect(screen.getByText('Content')).toBeInTheDocument()
  })

  it('renders without title', () => {
    render(<StatCard title=""><div>Content</div></StatCard>)
    expect(screen.getByText('Content')).toBeInTheDocument()
  })

  it('renders icon when provided', () => {
    render(<StatCard title="Test" icon={<span>ICON</span>}><div>Content</div></StatCard>)
    expect(screen.getByText('ICON')).toBeInTheDocument()
  })
})
