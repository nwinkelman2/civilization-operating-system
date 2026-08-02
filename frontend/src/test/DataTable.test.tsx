import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import DataTable from '../components/DataTable'

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'role', label: 'Role' },
]

const data = [
  { id: 1, name: 'Alice', role: 'Engineer' },
  { id: 2, name: 'Bob', role: 'Farmer' },
]

describe('DataTable', () => {
  it('renders headers and data rows', () => {
    render(<DataTable columns={columns} data={data} />)
    expect(screen.getByText('Name')).toBeInTheDocument()
    expect(screen.getByText('Role')).toBeInTheDocument()
    expect(screen.getByText('Alice')).toBeInTheDocument()
    expect(screen.getByText('Bob')).toBeInTheDocument()
  })

  it('shows loading state', () => {
    render(<DataTable columns={columns} data={[]} loading />)
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })

  it('shows empty message when no data', () => {
    render(<DataTable columns={columns} data={[]} emptyMessage="No items" />)
    expect(screen.getByText('No items')).toBeInTheDocument()
  })

  it('renders custom cell renderers', () => {
    const colsWithRender = [
      { key: 'name', label: 'Name', render: (item: any) => <strong>{item.name}</strong> },
    ]
    render(<DataTable columns={colsWithRender} data={data} />)
    const strong = screen.getByText('Alice')
    expect(strong.tagName).toBe('STRONG')
  })
})
