import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import ResourceBar from '../components/ResourceBar'

describe('ResourceBar', () => {
  it('renders label and value', () => {
    render(<ResourceBar label="food" value={50} />)
    expect(screen.getByText('food')).toBeInTheDocument()
    expect(screen.getByText('50.0')).toBeInTheDocument()
  })

  it('caps width at 100% when value exceeds max', () => {
    render(<ResourceBar label="energy" value={999} max={200} />)
    const fill = document.querySelector('.resource-bar-fill') as HTMLElement
    expect(fill.style.width).toBe('100%')
  })

  it('uses custom color', () => {
    render(<ResourceBar label="test" value={50} color="#ff0000" />)
    const fill = document.querySelector('.resource-bar-fill') as HTMLElement
    expect(fill.style.background).toBe('rgb(255, 0, 0)')
  })

  it('uses default color mapping for known resource types', () => {
    render(<ResourceBar label="water" value={30} />)
    const fill = document.querySelector('.resource-bar-fill') as HTMLElement
    expect(fill.style.background).toBe('rgb(14, 165, 233)')
  })
})
