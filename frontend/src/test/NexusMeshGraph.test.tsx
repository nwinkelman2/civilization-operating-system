import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import NexusMeshGraph, { NexusNode } from '../components/NexusMeshGraph'

describe('NexusMeshGraph', () => {
  const sampleNodes: NexusNode[] = [
    { id: 'node-1', name: 'Alpha Node', type: 'HUB', status: 'ACTIVE', latencyMs: 15, signalStrength: 90 },
    { id: 'node-2', name: 'Beta Node', type: 'RELAY', status: 'BOOTING', latencyMs: 40, signalStrength: 60 },
  ]

  it('renders interactive topology heading', () => {
    render(<NexusMeshGraph nodes={sampleNodes} />)
    expect(screen.getByText(/Interactive Nexus Mesh Topology/i)).toBeInTheDocument()
  })

  it('renders node names and status badges', () => {
    render(<NexusMeshGraph nodes={sampleNodes} />)
    expect(screen.getAllByText('Alpha Node')[0]).toBeInTheDocument()
    expect(screen.getAllByText('Beta Node')[0]).toBeInTheDocument()
  })

  it('displays latency and node details in selection panel', () => {
    render(<NexusMeshGraph nodes={sampleNodes} selectedNodeId="node-1" />)
    expect(screen.getAllByText('Alpha Node').length).toBeGreaterThan(0)
    expect(screen.getByText(/Latency:/i)).toBeInTheDocument()
    expect(screen.getByText('15 ms')).toBeInTheDocument()
  })
})
