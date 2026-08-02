import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import Layout from '../components/Layout'

describe('Layout', () => {
  it('renders title and children', () => {
    render(<Layout icon={<span>ICON</span>} title="Test Page">Content</Layout>)
    expect(screen.getByText('Test Page')).toBeInTheDocument()
    expect(screen.getByText('Content')).toBeInTheDocument()
  })

  it('renders subtitle when provided', () => {
    render(<Layout icon={<span>ICON</span>} title="Page" subtitle="Description">Content</Layout>)
    expect(screen.getByText('Description')).toBeInTheDocument()
  })

  it('renders action buttons when provided', () => {
    render(<Layout icon={<span>ICON</span>} title="Page" actions={<button>Action</button>}>Content</Layout>)
    expect(screen.getByText('Action')).toBeInTheDocument()
  })
})
