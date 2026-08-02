import { useEffect, useCallback, useRef } from 'react'

interface RealtimeConfig {
  token?: string
  onResourceTick?: (data: any) => void
  onNexusMessage?: (data: any) => void
  onVoxtexMessage?: (data: any) => void
  onCivilizationEvent?: (data: any) => void
  onSimulationTick?: (data: any) => void
  onStatusChange?: (connected: boolean) => void
}

export function useRealtimeUpdates({
  token,
  onResourceTick,
  onNexusMessage,
  onVoxtexMessage,
  onCivilizationEvent,
  onSimulationTick,
  onStatusChange,
}: RealtimeConfig) {
  const ws = useRef<WebSocket | null>(null)
  const reconnectTimeout = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)
  const nexusMsgHandler = onNexusMessage || onVoxtexMessage
  const handlers = useRef({ onResourceTick, onNexusMessage: nexusMsgHandler, onCivilizationEvent, onSimulationTick })

  useEffect(() => {
    handlers.current = { onResourceTick, onNexusMessage: nexusMsgHandler, onCivilizationEvent, onSimulationTick }
  })

  const connect = useCallback(() => {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
    const url = `${protocol}//${location.host}/ws/nexus${token ? `?token=${token}` : ''}`

    ws.current = new WebSocket(url)

    ws.current.onopen = () => onStatusChange?.(true)

    ws.current.onclose = () => {
      onStatusChange?.(false)
      reconnectTimeout.current = setTimeout(connect, 3000)
    }

    ws.current.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data)
        const type = msg.type || ''
        const payload = msg.payload || msg

        switch (type) {
          case 'RESOURCE_TICK':
          case 'resource_tick':
            handlers.current.onResourceTick?.(payload)
            break
          case 'NEXUS_MESSAGE':
          case 'nexus_message':
          case 'VOXTEX_MESSAGE':
          case 'voxtex_message':
            handlers.current.onNexusMessage?.(payload)
            break
          case 'CIVILIZATION_EVENT':
          case 'civilization_event':
            handlers.current.onCivilizationEvent?.(payload)
            break
          case 'SIMULATION_TICK':
          case 'simulation_tick':
            handlers.current.onSimulationTick?.(payload)
            break
        }
      } catch {
        // ignore parse errors
      }
    }
  }, [token, onStatusChange])

  useEffect(() => {
    connect()
    return () => {
      clearTimeout(reconnectTimeout.current)
      ws.current?.close()
    }
  }, [connect])

  const send = useCallback((type: string, payload?: any) => {
    if (ws.current?.readyState === WebSocket.OPEN) {
      ws.current.send(JSON.stringify({ type, payload }))
    }
  }, [])

  return { send }
}
