import { useEffect, useRef, useState } from 'react'

type MessageHandler = (data: any) => void

export function useWebSocket(token?: string) {
  const ws = useRef<WebSocket | null>(null)
  const handlers = useRef<Map<string, MessageHandler[]>>(new Map())
  const [connected, setConnected] = useState(false)
  const reconnectTimeout = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  function connect() {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
    const url = `${protocol}//${location.host}/ws/nexus${token ? `?token=${token}` : ''}`

    ws.current = new WebSocket(url)

    ws.current.onopen = () => setConnected(true)

    ws.current.onclose = () => {
      setConnected(false)
      reconnectTimeout.current = setTimeout(connect, 3000)
    }

    ws.current.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data)
        const type = msg.type || 'message'
        const typeHandlers = handlers.current.get(type) || []
        typeHandlers.forEach(fn => fn(msg.payload || msg))
      } catch {
        // ignore parse errors
      }
    }
  }

  function subscribe(type: string, handler: MessageHandler) {
    const existing = handlers.current.get(type) || []
    existing.push(handler)
    handlers.current.set(type, existing)
    return () => {
      const h = handlers.current.get(type) || []
      handlers.current.set(type, h.filter(fn => fn !== handler))
    }
  }

  function send(type: string, payload?: any) {
    if (ws.current?.readyState === WebSocket.OPEN) {
      ws.current.send(JSON.stringify({ type, payload }))
    }
  }

  useEffect(() => {
    connect()
    return () => {
      clearTimeout(reconnectTimeout.current)
      ws.current?.close()
    }
  }, [token])

  return { connected, subscribe, send, ws: ws.current }
}
