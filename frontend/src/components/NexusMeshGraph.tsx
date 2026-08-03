import React, { useState } from 'react'
import { Wifi, Activity, Cpu, Server, Radio, ShieldCheck } from 'lucide-react'

export interface NexusNode {
  id: string
  name: string
  type: string
  status: 'ACTIVE' | 'BOOTING' | 'IDLE' | 'OFFLINE' | string
  region?: string
  lastActiveAt?: string
  messageCount?: number
  latencyMs?: number
  signalStrength?: number // 0-100
}

interface NexusMeshGraphProps {
  nodes?: NexusNode[]
  onNodeSelect?: (node: NexusNode) => void
  selectedNodeId?: string
}

const DEFAULT_NODES: NexusNode[] = [
  { id: 'node-core', name: 'Core Nexus Prime', type: 'CENTRAL_HUB', status: 'ACTIVE', region: 'Global-Alpha', messageCount: 1420, latencyMs: 8, signalStrength: 98 },
  { id: 'node-1', name: 'Alpha Orbital Relay', type: 'RELAY', status: 'ACTIVE', region: 'North America', messageCount: 840, latencyMs: 14, signalStrength: 92 },
  { id: 'node-2', name: 'Biosphere Sentinel', type: 'MONITOR', status: 'ACTIVE', region: 'Amazonia', messageCount: 620, latencyMs: 22, signalStrength: 85 },
  { id: 'node-3', name: 'Cortex Neural Node', type: 'COMPUTE', status: 'BOOTING', region: 'Europa Sector', messageCount: 150, latencyMs: 45, signalStrength: 68 },
  { id: 'node-4', name: 'Pacific Grid Node', type: 'STORAGE', status: 'IDLE', region: 'Pacific Basin', messageCount: 310, latencyMs: 38, signalStrength: 74 },
  { id: 'node-5', name: 'Sahara Solar Edge', type: 'EDGE', status: 'OFFLINE', region: 'Sahara Region', messageCount: 0, latencyMs: 180, signalStrength: 25 },
]

export const NexusMeshGraph: React.FC<NexusMeshGraphProps> = ({
  nodes = [],
  onNodeSelect,
  selectedNodeId: propSelectedNodeId,
}) => {
  const displayNodes = nodes.length > 0 ? nodes : DEFAULT_NODES
  const [internalSelectedId, setInternalSelectedId] = useState<string | null>(null)
  const [hoveredNodeId, setHoveredNodeId] = useState<string | null>(null)

  const activeSelectedId = propSelectedNodeId ?? internalSelectedId ?? displayNodes[0]?.id

  // Dimensions for SVG canvas
  const width = 800
  const height = 420
  const centerX = width / 2
  const centerY = height / 2
  const radius = 150

  // Calculate layout coordinates around center
  const positionedNodes = displayNodes.map((node, index) => {
    if (index === 0 && displayNodes.length > 1 && node.type === 'CENTRAL_HUB') {
      return { ...node, x: centerX, y: centerY }
    }
    const totalOuter = displayNodes[0]?.type === 'CENTRAL_HUB' ? displayNodes.length - 1 : displayNodes.length
    const outerIndex = displayNodes[0]?.type === 'CENTRAL_HUB' ? index - 1 : index
    const angle = (2 * Math.PI * outerIndex) / Math.max(1, totalOuter) - Math.PI / 2

    // Derive deterministic latency/signal if missing
    const latencyMs = node.latencyMs ?? ((index + 1) * 12 + 5)
    const signalStrength = node.signalStrength ?? Math.max(30, 100 - index * 12)

    return {
      ...node,
      latencyMs,
      signalStrength,
      x: centerX + radius * Math.cos(angle),
      y: centerY + radius * Math.sin(angle),
    }
  })

  const selectedNode = positionedNodes.find((n) => n.id === activeSelectedId) || positionedNodes[0]

  const handleNodeClick = (node: NexusNode) => {
    setInternalSelectedId(node.id)
    if (onNodeSelect) {
      onNodeSelect(node)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'ACTIVE':
        return '#22c55e'
      case 'BOOTING':
        return '#f59e0b'
      case 'IDLE':
        return '#0ea5e9'
      case 'OFFLINE':
        return '#ef4444'
      default:
        return '#64748b'
    }
  }

  const renderSignalBars = (strength: number = 80, x: number, y: number) => {
    const bars = [1, 2, 3, 4]
    const filledCount = Math.ceil((strength / 100) * 4)

    return (
      <g transform={`translate(${x}, ${y})`}>
        {bars.map((bar, i) => (
          <rect
            key={i}
            x={i * 4}
            y={12 - (i + 1) * 3}
            width="2.5"
            height={(i + 1) * 3}
            rx="1"
            fill={i < filledCount ? '#22c55e' : '#334155'}
          />
        ))}
      </g>
    )
  }

  return (
    <div
      className="stat-card"
      style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        border: '1px solid #334155',
        borderRadius: '0.75rem',
        padding: '1.25rem',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Radio size={20} color="#a78bfa" />
          <h2 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#f8fafc', margin: 0 }}>
            Interactive Nexus Mesh Topology
          </h2>
        </div>
        <div style={{ display: 'flex', gap: '1rem', fontSize: '0.75rem', color: '#94a3b8' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#22c55e' }} /> ACTIVE
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#f59e0b' }} /> BOOTING
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#0ea5e9' }} /> IDLE
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#ef4444' }} /> OFFLINE
          </span>
        </div>
      </div>

      <div style={{ position: 'relative', width: '100%', display: 'flex', justifyContent: 'center' }}>
        <svg
          viewBox={`0 0 ${width} ${height}`}
          style={{ width: '100%', maxHeight: '420px', height: 'auto', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '0.5rem' }}
        >
          <defs>
            {/* Background Grid Pattern */}
            <pattern id="mesh-grid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(51, 65, 85, 0.3)" strokeWidth="1" />
            </pattern>
            {/* Radial Glow Gradient */}
            <radialGradient id="center-glow" cx="50%" cy="50%" r="50%">
              <stop offset="0%" stopColor="#a78bfa" stopOpacity="0.25" />
              <stop offset="100%" stopColor="#a78bfa" stopOpacity="0" />
            </radialGradient>
          </defs>

          {/* Background Grid */}
          <rect width={width} height={height} fill="url(#mesh-grid)" />

          {/* Central Glow Effect */}
          <circle cx={centerX} cy={centerY} r={radius * 1.2} fill="url(#center-glow)" />

          {/* Connection Lines between nodes */}
          <g className="mesh-lines">
            {positionedNodes.map((node, i) => {
              const isCenterNode = node.x === centerX && node.y === centerY
              if (isCenterNode) return null

              const isActive = node.status === 'ACTIVE'
              const isSelected = node.id === activeSelectedId || hoveredNodeId === node.id

              return (
                <g key={`line-group-${node.id}`}>
                  {/* Base Line */}
                  <line
                    x1={centerX}
                    y1={centerY}
                    x2={node.x}
                    y2={node.y}
                    stroke={isSelected ? '#a78bfa' : isActive ? '#3b82f6' : '#334155'}
                    strokeWidth={isSelected ? 2.5 : 1.5}
                    strokeOpacity={isSelected ? 0.9 : 0.4}
                  />

                  {/* Animated Data Particle Flow Line */}
                  {isActive && (
                    <line
                      x1={centerX}
                      y1={centerY}
                      x2={node.x}
                      y2={node.y}
                      stroke={getStatusColor(node.status)}
                      strokeWidth={2}
                      className="mesh-dash-animated"
                      strokeOpacity={0.8}
                    />
                  )}

                  {/* Inter-node Ring Connections */}
                  {i > 0 && positionedNodes[i - 1] && positionedNodes[i - 1].x !== centerX && (
                    <line
                      x1={positionedNodes[i - 1].x}
                      y1={positionedNodes[i - 1].y}
                      x2={node.x}
                      y2={node.y}
                      stroke="#334155"
                      strokeWidth={1}
                      strokeDasharray="3 3"
                      strokeOpacity={0.3}
                    />
                  )}
                </g>
              )
            })}
          </g>

          {/* Nodes Rendering */}
          {positionedNodes.map((node) => {
            const isSelected = node.id === activeSelectedId
            const isHovered = node.id === hoveredNodeId
            const statusColor = getStatusColor(node.status)

            return (
              <g
                key={node.id}
                transform={`translate(${node.x}, ${node.y})`}
                style={{ cursor: 'pointer' }}
                onClick={() => handleNodeClick(node)}
                onMouseEnter={() => setHoveredNodeId(node.id)}
                onMouseLeave={() => setHoveredNodeId(null)}
              >
                {/* CSS Pulse Ring on hover or selection */}
                {(isSelected || isHovered) && (
                  <circle
                    cx={0}
                    cy={0}
                    r={22}
                    fill="none"
                    stroke={statusColor}
                    strokeWidth={2}
                    className="mesh-pulse-ring"
                  />
                )}

                {/* Main Node Outer Ring */}
                <circle
                  cx={0}
                  cy={0}
                  r={isSelected ? 20 : 16}
                  fill="#1e293b"
                  stroke={isSelected ? '#a78bfa' : statusColor}
                  strokeWidth={isSelected ? 3 : 2}
                  style={{
                    filter: isSelected ? 'drop-shadow(0 0 8px rgba(167, 139, 250, 0.8))' : 'none',
                    transition: 'all 0.2s ease',
                  }}
                />

                {/* Inner Core Fill */}
                <circle cx={0} cy={0} r={6} fill={statusColor} />

                {/* Signal Strength Bars (top right of node) */}
                {renderSignalBars(node.signalStrength, 12, -22)}

                {/* Latency Badge (top left / above node) */}
                <g transform="translate(0, -26)">
                  <rect
                    x={-24}
                    y={-10}
                    width={48}
                    height={14}
                    rx={7}
                    fill="#0f172a"
                    stroke="#334155"
                    strokeWidth={1}
                  />
                  <text
                    x={0}
                    y={0}
                    fill="#94a3b8"
                    fontSize="9"
                    fontWeight="600"
                    textAnchor="middle"
                  >
                    {node.latencyMs}ms
                  </text>
                </g>

                {/* Node Name Label */}
                <text
                  x={0}
                  y={32}
                  fill={isSelected ? '#f8fafc' : '#cbd5e1'}
                  fontSize="11"
                  fontWeight={isSelected ? '700' : '500'}
                  textAnchor="middle"
                >
                  {node.name}
                </text>

                {/* Status Badge below name */}
                <g transform="translate(0, 40)">
                  <rect
                    x={-28}
                    y={0}
                    width={56}
                    height={13}
                    rx={6.5}
                    fill={node.status === 'ACTIVE' ? 'rgba(34, 197, 94, 0.2)' : 'rgba(51, 65, 85, 0.5)'}
                    stroke={statusColor}
                    strokeWidth={0.8}
                  />
                  <text
                    x={0}
                    y={9}
                    fill={statusColor}
                    fontSize="8"
                    fontWeight="700"
                    textAnchor="middle"
                  >
                    {node.status}
                  </text>
                </g>
              </g>
            )
          })}
        </svg>
      </div>

      {/* Selected Node Inspector Detail Banner */}
      {selectedNode && (
        <div
          style={{
            marginTop: '1rem',
            padding: '0.85rem 1rem',
            background: '#0f172a',
            border: '1px solid #334155',
            borderRadius: '0.5rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '1rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '0.5rem',
                background: 'rgba(167, 139, 250, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Cpu size={20} color="#a78bfa" />
            </div>
            <div>
              <div style={{ fontWeight: 600, color: '#f8fafc', fontSize: '0.95rem' }}>{selectedNode.name}</div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                ID: {selectedNode.id} &bull; Type: {selectedNode.type} &bull; Region: {selectedNode.region || 'N/A'}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', fontSize: '0.85rem' }}>
            <div>
              <span style={{ color: '#64748b' }}>Latency: </span>
              <span style={{ color: '#0ea5e9', fontWeight: 600 }}>{selectedNode.latencyMs} ms</span>
            </div>
            <div>
              <span style={{ color: '#64748b' }}>Signal Strength: </span>
              <span style={{ color: '#22c55e', fontWeight: 600 }}>{selectedNode.signalStrength}%</span>
            </div>
            <div>
              <span style={{ color: '#64748b' }}>Messages: </span>
              <span style={{ color: '#a78bfa', fontWeight: 600 }}>{selectedNode.messageCount || 0}</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default NexusMeshGraph
